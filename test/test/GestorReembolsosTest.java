package test;

import logica.*;
import boletamaster.usuarios.Comprador;
import boletamaster.usuarios.Organizador;
import boletamaster.transacciones.Reembolso;
import boletamaster.tiquetes.Ticket;
import boletamaster.tiquetes.TicketSimple;
import boletamaster.tiquetes.TicketEstado;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;
import boletamaster.eventos.Venue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class GestorReembolsosTest {
    
    private GestorReembolsos gestorReembolsos;
    private Comprador comprador;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        gestorReembolsos = new GestorReembolsos();
        comprador = new Comprador("comp1", "pass123", "Comprador Test");
        //Crear evento con fecha y organizador válidos
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Organizador organizador = new Organizador("org1", "pass123", "Organizador Test");
        Evento evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        Localidad localidad = new Localidad("L001", "General", 100.0, 1000, false);
        ticket = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
    }

    @Test
    public void testProcesarReembolsoCompleto() {
        double saldoInicial = comprador.getSaldo();
        double montoReembolso = 115.0;
        Reembolso reembolso = gestorReembolsos.procesarReembolsoCompleto(comprador, montoReembolso, "Cancelación administrativa");
        assertNotNull(reembolso);
        assertEquals(comprador, reembolso.getUsuario());
        assertEquals(montoReembolso, reembolso.getMontoTotal(), 0.01);
        assertEquals(saldoInicial + montoReembolso, comprador.getSaldo(), 0.01);
    }

    @Test
    public void testProcesarReembolsoParcial() {
        double saldoInicial = comprador.getSaldo();
        double montoBase = 100.0;
        Reembolso reembolso = gestorReembolsos.procesarReembolsoParcial(comprador, montoBase, "Cancelación organizador");
        assertNotNull(reembolso);
        assertEquals(montoBase, reembolso.getMontoTotal(), 0.01);
        assertEquals(saldoInicial + montoBase, comprador.getSaldo(), 0.01);
    }

    @Test
    public void testPuedeReembolsarTicketDisponible() {
        ticket.setEstado(TicketEstado.DISPONIBLE);
        boolean puedeReembolsar = gestorReembolsos.puedeReembolsar(ticket);
        assertTrue(puedeReembolsar);
    }

    @Test
    public void testNoPuedeReembolsarTicketUsado() {
        ticket.setEstado(TicketEstado.USADO);
        boolean puedeReembolsar = gestorReembolsos.puedeReembolsar(ticket);
        assertFalse(puedeReembolsar);
    }

    @Test
    public void testNoPuedeReembolsarTicketCancelado() {
        ticket.setEstado(TicketEstado.CANCELADO);
        boolean puedeReembolsar = gestorReembolsos.puedeReembolsar(ticket);
        assertFalse(puedeReembolsar);
    }

    @Test
    public void testPuedeReembolsarTicketVendido() {
        ticket.setEstado(TicketEstado.VENDIDO);
        boolean puedeReembolsar = gestorReembolsos.puedeReembolsar(ticket);
        assertTrue(puedeReembolsar);
    }

    @Test
    public void testReembolsoCompletoActualizaSaldo() {
        comprador.depositarSaldo(50.0);
        double saldoInicial = comprador.getSaldo();
        double montoReembolso = 75.0;
        Reembolso reembolso = gestorReembolsos.procesarReembolsoCompleto(comprador, montoReembolso, "Test");
        assertEquals(saldoInicial + montoReembolso, comprador.getSaldo(), 0.01);
        assertEquals(montoReembolso, reembolso.getMontoTotal(), 0.01);
    }

    @Test
    public void testReembolsoParcialActualizaSaldo() {
        comprador.depositarSaldo(25.0);
        double saldoInicial = comprador.getSaldo();
        double montoBase = 50.0;
        Reembolso reembolso = gestorReembolsos.procesarReembolsoParcial(comprador, montoBase, "Test");
        assertEquals(saldoInicial + montoBase, comprador.getSaldo(), 0.01);
        assertEquals(montoBase, reembolso.getMontoTotal(), 0.01);
    }
}
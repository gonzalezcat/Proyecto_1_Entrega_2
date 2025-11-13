package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import logica.*;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;
import boletamaster.eventos.Venue;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.Compra;
import boletamaster.usuarios.Comprador;
import boletamaster.usuarios.Organizador;
import java.time.LocalDateTime;
import java.util.Arrays;

public class GestorFinanzasTest {
    
    private GestorFinanzas gestorFinanzas;
    private Organizador organizador;

    @Before
    public void setUp() {
        gestorFinanzas = new GestorFinanzas();
        organizador = new Organizador("org1", "pass123", "Organizador Test");
    }

    @Test
    public void testCalcularGananciasOrganizador() {
        Comprador comprador = new Comprador("comp1", "pass456", "Comprador Test");
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        
        // Asignar fecha al evento
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        Localidad localidad = new Localidad("L001", "General", 100.0, 1000, false);
        
        TicketSimple ticket1 = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        TicketSimple ticket2 = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        
        // Crear compra normal (no necesita fecha específica)
        Compra compra = new Compra(comprador, 240.0, Arrays.asList(ticket1, ticket2));
        
        gestorFinanzas.agregarTransaccion(compra);
        
        // Usar rango que incluya la compra recién creada
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);
       
        double ganancias = gestorFinanzas.calcularGananciasOrganizador(organizador, inicio, fin);
        
        assertEquals(200.0, ganancias, 0.01); // 2 tickets × 100.0 (precio base)
    }

    @Test
    public void testCalcularGananciasPlataforma() {
        Comprador comprador = new Comprador("comp1", "pass456", "Comprador Test");
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        
        // Asignar fecha al evento
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        Localidad localidad = new Localidad("L001", "General", 100.0, 1000, false);
        
        TicketSimple ticket1 = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        
        // Comisión por ticket: (100 * 0.15) + 5 = 15 + 5 = 20
        double comisionPorTicket = (100.0 * 0.15) + 5.0;
        
        Compra compra = new Compra(comprador, 120.0, Arrays.asList(ticket1));
        
        gestorFinanzas.agregarTransaccion(compra);
        
        // Usar rango que incluya la compra recién creada
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);
        double ganancias = gestorFinanzas.calcularGananciasPlataforma(inicio, fin);
        assertEquals(comisionPorTicket, ganancias, 0.01);
    }

    @Test
    public void testCalcularGananciasFueraDelRango() {
        Comprador comprador = new Comprador("comp1", "pass456", "Comprador Test");
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        //  Asignar fecha al evento
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        Localidad localidad = new Localidad("L001", "General", 100.0, 1000, false);
        
        TicketSimple ticket1 = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        
        Compra compra = new Compra(comprador, 120.0, Arrays.asList(ticket1));
        
        gestorFinanzas.agregarTransaccion(compra);
        
        // Rango de búsqueda: fuera del rango de la compra
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fin = LocalDateTime.now().minusDays(5);
        double ganancias = gestorFinanzas.calcularGananciasOrganizador(organizador, inicio, fin);
        
        //  No debería incluir la compra fuera del rango
        assertEquals(0.0, ganancias, 0.01);
    }

    @Test
    public void testConfigurarTarifas() {
        double nuevaCuotaFija = 7.5;
        gestorFinanzas.configurarTarifas(nuevaCuotaFija, 0.12);
        assertEquals(nuevaCuotaFija, gestorFinanzas.getCuotaFijaGlobal(), 0.01);
    }

    @Test
    public void testAgregarTransaccion() {
        Comprador comprador = new Comprador("comp1", "pass456", "Comprador Test");
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        //  Asignar fecha al evento
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        Localidad localidad = new Localidad("L001", "General", 100.0, 1000, false);
        
        TicketSimple ticket1 = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        Compra compra = new Compra(comprador, 120.0, Arrays.asList(ticket1));
        gestorFinanzas.agregarTransaccion(compra);
        assertEquals(1, gestorFinanzas.getTransacciones().size());
        assertEquals(compra, gestorFinanzas.getTransacciones().get(0));
    }
}
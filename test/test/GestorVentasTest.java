package test;

import logica.*;
import boletamaster.usuarios.*;
import boletamaster.tiquetes.*;
import boletamaster.eventos.*;
import boletamaster.transacciones.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime; 

class GestorVentasTest {
    private GestorVentas gestorVentas;
    private GestorFinanzas gestorFinanzas;
    private GestorOfertas gestorOfertas;
    private Comprador comprador;
    private Evento evento;
    private Localidad localidad;
//2
    @BeforeEach
    void setUp() {
        gestorFinanzas = new GestorFinanzas();
        gestorOfertas = new GestorOfertas();
        gestorVentas = new GestorVentas(gestorFinanzas, gestorOfertas);
        
        comprador = new Comprador("comprador1", "pass123", "Comprador Test");
        Organizador organizador = new Organizador("org1", "pass456", "Organizador Test");
        Venue venue = new Venue("V001", "Estadio", "Ciudad", 50000, true);
        
        // Asignar fecha/hora válida al evento
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7); // Evento en 7 días
        evento = new Evento("E001", "Concierto", fechaEvento, venue, organizador);
        
        localidad = new Localidad("L001", "General", 100.0, 1000, false);
    }

    @Test
    void testProcesarCompraExitosaConSaldo() {
        comprador.depositarSaldo(500.0);
        List<Ticket> tickets = Arrays.asList(
            new TicketSimple(evento, localidad, 100.0, 0.15, 5.0),
            new TicketSimple(evento, localidad, 100.0, 0.15, 5.0)
        );
        Compra compra = gestorVentas.procesarCompra(comprador, tickets, true);
        assertNotNull(compra);
        assertEquals(comprador, compra.getUsuario());
        assertEquals(2, compra.getTickets().size());
        
        // Verificar que los tickets fueron asignados al comprador
        for (Ticket ticket : tickets) {
            assertEquals(comprador, ticket.getPropietario());
            assertEquals(TicketEstado.VENDIDO, ticket.getEstado());
        }
        
        // Verificar que se registró la transacción
        assertEquals(1, gestorFinanzas.getTransacciones().size());
    }

    @Test
    void testProcesarCompraConSaldoInsuficiente() {
        comprador.depositarSaldo(50.0); // Saldo insuficiente
        List<Ticket> tickets = Arrays.asList(
            new TicketSimple(evento, localidad, 100.0, 0.15, 5.0)
        );
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            gestorVentas.procesarCompra(comprador, tickets, true);
        });
        
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void testProcesarCompraConRestriccionMaxTickets() {
        //Crear más de 10 tickets (límite por transacción)
        comprador.depositarSaldo(5000.0);
        Ticket[] muchosTickets = new Ticket[11];
        for (int i = 0; i < 11; i++) {
            muchosTickets[i] = new TicketSimple(evento, localidad, 100.0, 0.15, 5.0);
        }
        List<Ticket> tickets = Arrays.asList(muchosTickets);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            gestorVentas.procesarCompra(comprador, tickets, true);
        });
        
        assertEquals("No se cumplen las restricciones de compra", exception.getMessage());
    }

    @Test
    void testCalcularMontoTotalConOferta() {
        Localidad localidadConOferta = new Localidad("L002", "VIP", 200.0, 100, false);
        
        // Asignar fecha al evento para este test
        LocalDateTime fechaEvento = LocalDateTime.now().plusDays(7);
        Evento eventoConOferta = new Evento("E002", "Concierto VIP", fechaEvento, evento.getVenue(), evento.getOrganizador());
        
        // Crear fechas para la oferta (vigente)
        LocalDateTime inicioOferta = LocalDateTime.now().minusDays(1);
        LocalDateTime finOferta = LocalDateTime.now().plusDays(1);
        Oferta oferta = new Oferta(localidadConOferta, 0.2, inicioOferta, finOferta); // 20% descuento
        
        // Simular que existe una oferta vigente
        
        
        
        Ticket ticket = new TicketSimple(eventoConOferta, localidadConOferta, 200.0, 0.15, 5.0);
        List<Ticket> tickets = Arrays.asList(ticket);
        
        // Act - El monto debería incluir el descuento
        double montoTotal = tickets.stream()
            .mapToDouble(t -> t.precioConDescuento(oferta))
            .sum();
        
        // El descuento se aplica al precio final, no solo al precio base
        double precioFinalSinDescuento = 200.0 + (200.0 * 0.15) + 5.0; // 235.0
        double precioConDescuento = precioFinalSinDescuento * (1 - 0.2); // 235.0 * 0.8 = 188.0
        
        assertEquals(188.0, montoTotal, 0.01, 
            "El cálculo del monto total con oferta no es correcto. " +
            "Se esperaba: " + precioConDescuento + " pero se obtuvo: " + montoTotal);
    }
}
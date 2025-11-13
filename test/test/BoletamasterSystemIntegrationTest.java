package test;

import logica.*;
import boletamaster.transacciones.*;
import boletamaster.usuarios.*;
import boletamaster.eventos.*;
import boletamaster.tiquetes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

class BoletamasterSystemIntegrationTest {
    private BoletamasterSystem sistema;
    private Comprador comprador;
    private Organizador organizador;
    private Administrador administrador;

    @BeforeEach
    void setUp() {
        //  Resetear la instancia del sistema antes de cada test
        BoletamasterSystem.resetInstance();
        sistema = BoletamasterSystem.getInstance();
        sistema.setTestingMode(true); // Desactivar persistencia en tests
        
        //Usar logins únicos para cada test
        String timestamp = String.valueOf(System.currentTimeMillis());
        comprador = sistema.getGestorUsuarios().registrarComprador(
            "compInt" + timestamp, "pass123", "Comprador Integración");
        organizador = sistema.getGestorUsuarios().registrarOrganizador(
            "orgInt" + timestamp, "pass456", "Organizador Integración");
        
        // Crear administrador si no existe
        administrador = (Administrador) sistema.getGestorUsuarios().buscarUsuarioPorLogin("admin").orElse(null);
        if (administrador == null) {
            administrador = sistema.getGestorUsuarios().registrarAdministrador("admin", "admin123", "Administrador Sistema");
        }
    }

    @Test
    void testFlujoCompletoCompraTicket() {
        // Historia de usuario: Compra exitosa de tickets
        
        // 1. Crear venue y evento
        Venue venue = new Venue("V-INT", "Estadio Integración", "Ciudad", 50000, true);
        sistema.agregarVenue(venue);
        
        Evento evento = sistema.getGestorEventos().crearEvento(
            organizador, "Concierto Integración", 
            LocalDateTime.now().plusDays(30), venue
        );
        sistema.agregarEvento(evento);
        
        // 2. Configurar localidades
        Localidad localidad = new Localidad("L-INT", "General", 100.0, 100, false);
        evento.addLocalidad(localidad);
        
        // 3. Generar tickets
        List<Ticket> tickets = sistema.getGestorTiquetes().generarTicketsParaLocalidad(
            evento, localidad, 10, 0.15, 5.0
        );
        
        // 4. Recargar saldo y comprar
        sistema.recargarSaldo(comprador, 500.0);
        List<Ticket> ticketsAComprar = tickets.subList(0, 2); // Comprar 2 tickets
        
        Compra compra = sistema.getGestorVentas().procesarCompra(comprador, ticketsAComprar, true);
        
        // 5. Verificaciones
        assertNotNull(compra);
        assertEquals(2, compra.getTickets().size());
        assertEquals(comprador, compra.getUsuario());
        
        for (Ticket ticket : ticketsAComprar) {
            assertEquals(TicketEstado.VENDIDO, ticket.getEstado());
            assertEquals(comprador, ticket.getPropietario());
        }
        
        // Verificar que el saldo se descontó correctamente
        
        assertTrue(comprador.getSaldo() < 500.0); // Se descontó algo
    }

    @Test
    void testFlujoCancelacionEventoAdministrativo() {
        // Historia de usuario: Cancelación de evento por administrador con reembolso completo
        
        // 1. Configurar escenario
        Venue venue = new Venue("V-CANC", "Estadio Cancelación", "Ciudad", 50000, true);
        sistema.agregarVenue(venue);
        
        Evento evento = sistema.getGestorEventos().crearEvento(
            organizador, "Evento a Cancelar", 
            LocalDateTime.now().plusDays(30), venue
        );
        sistema.agregarEvento(evento);
        
        Localidad localidad = new Localidad("L-CANC", "VIP", 200.0, 50, false);
        evento.addLocalidad(localidad);
        
        List<Ticket> tickets = sistema.getGestorTiquetes().generarTicketsParaLocalidad(
            evento, localidad, 5, 0.15, 5.0
        );
        
        // 2. Realizar compra
        sistema.recargarSaldo(comprador, 1000.0);
        List<Ticket> ticketsComprados = tickets.subList(0, 2);
        sistema.getGestorVentas().procesarCompra(comprador, ticketsComprados, true);
        
        double saldoDespuesCompra = comprador.getSaldo();
        
        // 3. Simular reembolso completo (sin llamar a guardarDatos)
        double montoReembolso = 2 * (200.0 + (200.0 * 0.15) + 5.0); // 2 tickets completos
        sistema.getGestorReembolsos().procesarReembolsoCompleto(comprador, montoReembolso, "Cancelación por lluvia");
        
        // 4. Verificar reembolso
        assertTrue(comprador.getSaldo() > saldoDespuesCompra);
        assertEquals(saldoDespuesCompra + montoReembolso, comprador.getSaldo(), 0.01);
    }

    @Test
    void testCreacionYValidacionVenue() {
        // Historia de usuario: Creación y aprobación de venues
        
        // 1. Organizador crea venue (no aprobado inicialmente)
        Venue venuePropuesto = new Venue("V-PROP", "Nuevo Lugar", "Nueva Ciudad", 10000, false);
        sistema.agregarVenue(venuePropuesto);
        
        // 2. Verificar que no se puede usar sin aprobación
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            sistema.getGestorEventos().crearEvento(
                organizador, "Evento en venue no aprobado", 
                LocalDateTime.now().plusDays(30), venuePropuesto
            );
        });
        
        assertTrue(exception.getMessage().contains("Venue no aprobado"));
        
        // 3. Administrador aprueba el venue (simulado)
        venuePropuesto.setAprobado(true);
        
        // 4. Ahora sí se puede crear el evento
        Evento evento = sistema.getGestorEventos().crearEvento(
            organizador, "Evento Aprobado", 
            LocalDateTime.now().plusDays(30), venuePropuesto
        );
        
        assertNotNull(evento);
    }
}
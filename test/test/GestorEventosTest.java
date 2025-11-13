package test;
import logica.*;

import boletamaster.usuarios.*;
import boletamaster.eventos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class GestorEventosTest {
    private GestorEventos gestorEventos;
    private Organizador organizador;
    private Venue venueAprobado;
    private Venue venueNoAprobado;

    @BeforeEach
    void setUp() {
        gestorEventos = new GestorEventos();
        organizador = new Organizador("org1", "pass123", "Organizador Test");
        venueAprobado = new Venue("V001", "Estadio Nacional", "Ciudad", 50000, true);
        venueNoAprobado = new Venue("V002", "Lote Abandonado", "Afueras", 1000, false);
    }

    @Test
    void testCrearEventoExitoso() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(30);
        Evento evento = gestorEventos.crearEvento(organizador, "Concierto Rock", fechaFutura, venueAprobado);
        assertNotNull(evento);
        assertEquals("Concierto Rock", evento.getNombre());
        assertEquals(organizador, evento.getOrganizador());
        assertEquals(venueAprobado, evento.getVenue());
    }

    @Test
    void testCrearEventoConVenueNoAprobado() {
        LocalDateTime fechaFutura = LocalDateTime.now().plusDays(30);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            gestorEventos.crearEvento(organizador, "Evento Ilegal", fechaFutura, venueNoAprobado);
        });
        
        assertTrue(exception.getMessage().contains("Venue no aprobado"));
    }

    @Test
    void testCrearEventoConFechaPasada() {
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestorEventos.crearEvento(organizador, "Evento Pasado", fechaPasada, venueAprobado);
        });
        
        assertEquals("La fecha del evento debe ser futura", exception.getMessage());
    }

    @Test
    void testGetEventosPorOrganizador() {
        // Usar venues diferentes para evitar conflicto de fechas
        LocalDateTime fecha1 = LocalDateTime.now().plusDays(30);
        LocalDateTime fecha2 = LocalDateTime.now().plusDays(60);
        LocalDateTime fecha3 = LocalDateTime.now().plusDays(45);
        
        // Crear un segundo venue aprobado para el tercer evento
        Venue venueAprobado2 = new Venue("V003", "Auditorio Central", "Ciudad", 2000, true);
        
        Evento evento1 = gestorEventos.crearEvento(organizador, "Evento 1", fecha1, venueAprobado);
        Evento evento2 = gestorEventos.crearEvento(organizador, "Evento 2", fecha2, venueAprobado);
        
        Organizador otroOrganizador = new Organizador("org2", "pass456", "Otro Organizador");
        Evento evento3 = gestorEventos.crearEvento(otroOrganizador, "Evento 3", fecha3, venueAprobado2);
        var eventosOrganizador = gestorEventos.getEventosPorOrganizador(organizador);
        
        assertEquals(2, eventosOrganizador.size());
        assertTrue(eventosOrganizador.contains(evento1));
        assertTrue(eventosOrganizador.contains(evento2));
        assertFalse(eventosOrganizador.contains(evento3));
    }
}
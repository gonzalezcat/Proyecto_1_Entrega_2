package logica;
import boletamaster.usuarios.*;
import boletamaster.eventos.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class GestorEventos {
    private List<Evento> eventos;  // 
    
    public GestorEventos() {
        this.eventos = new ArrayList<>();  
    }
    
    public Evento crearEvento(Organizador organizador, String nombre, 
                             LocalDateTime fecha, Venue venue) {
        
        // ✅ VALIDACIÓN 1: Venue aprobado
        if (!venue.isAprobado()) {
            throw new IllegalStateException("Venue no aprobado: " + venue.getNombre());
        }
        
        // ✅ VALIDACIÓN 2: Fecha futura
        if (fecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha del evento debe ser futura");
        }
        
        // ✅ VALIDACIÓN 3: No hay otro evento en el mismo venue y fecha
        if (existeEventoEnVenueYFecha(venue, fecha)) {
            throw new IllegalStateException("Ya hay un evento en " + venue.getNombre() + " para " + fecha.toLocalDate());
        }
        
        // Generar ID único para el evento
        String idEvento = "EVT-" + System.currentTimeMillis();
        
        // Crear y retornar el evento
        Evento nuevoEvento = new Evento(idEvento, nombre, fecha, venue, organizador);
        eventos.add(nuevoEvento);  // ✅ AGREGAR a la lista interna
        return nuevoEvento;
    }
    
    //  Validar disponibilidad de venue
    private boolean existeEventoEnVenueYFecha(Venue venue, LocalDateTime fecha) {
        for (Evento eventoExistente : eventos) {
            boolean mismoVenue = eventoExistente.getVenue().equals(venue);
            boolean mismaFecha = eventoExistente.getFechaHora().toLocalDate().equals(fecha.toLocalDate());
            
            if (mismoVenue && mismaFecha) {
                return true;
            }
        }
        return false;
    }
    
    // Obtener eventos de un organizador
    public List<Evento> getEventosPorOrganizador(Organizador organizador) {
        List<Evento> eventosOrganizador = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getOrganizador().equals(organizador)) {
                eventosOrganizador.add(evento);
            }
        }
        return eventosOrganizador;
    }
    
    public boolean aprobarEvento(Administrador admin, Evento evento) {
        // Lógica de aprobación 
        System.out.println("Evento '" + evento.getNombre() + "' aprobado por " + admin.getNombre());
        return true;
    }
    
    // GETTER para la lista de eventos
    public List<Evento> getEventos() {
        return new ArrayList<>(eventos);
    }

    
}
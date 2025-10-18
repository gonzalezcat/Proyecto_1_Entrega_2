package logica;
import java.time.LocalDateTime;
import boletamaster.eventos.*;
import boletamaster.usuarios.*;
public class MainPrueba {
    public static void main(String[] args) {
        BoletamasterSystem sistema = BoletamasterSystem.getInstance();
        
        try {
            // 1. Registrar usuarios
            Comprador comprador = sistema.getGestorUsuarios()
                .registrarComprador("cliente1", "pass123", "Juan Pérez");
            
            Organizador organizador = sistema.getGestorUsuarios()
                .registrarOrganizador("org1", "pass456", "Eventos SA");
            
            // 2. Crear venue y evento
            Venue venue = new Venue("V001", "Estadio Nacional", "Ciudad", 50000, true);
            sistema.agregarVenue(venue);
            
            Evento evento = sistema.getGestorEventos().crearEvento(
                organizador, "Concierto Rock", 
                LocalDateTime.now().plusDays(30), venue
            );
            sistema.agregarEvento(evento);
            
            // 3. Guardar datos al finalizar
            sistema.guardarDatos();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
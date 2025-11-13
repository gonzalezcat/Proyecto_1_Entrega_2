package logica;

import java.time.LocalDateTime;
import boletamaster.eventos.*;
import boletamaster.usuarios.*;

public class MainPrueba {
    public static void main(String[] args) {
        BoletamasterSystem sistema = BoletamasterSystem.getInstance();

        try {
            // 1️⃣ Registrar organizador manualmente
            Organizador organizador = new Organizador("org1", "pass456", "Eventos SA");
            sistema.registrarUsuario(organizador);

            // 2️⃣ Crear y registrar un venue
            Venue venue = new Venue("V001", "Estadio Nacional", "Ciudad", 50000, true);
            sistema.agregarVenue(venue);

            // 3️⃣ Crear evento
            Evento evento = new Evento("E001", "Concierto Rock", organizador,
                    LocalDateTime.now().plusDays(30), venue, TipoEvento.MUSICAL);
            sistema.agregarEvento(evento);

            // 4️⃣ Verificar datos
            System.out.println("✅ Evento creado: " + evento.getNombre());
            System.out.println("Organizador: " + organizador.getNombre());
            System.out.println("Venue: " + venue.getNombre());
            System.out.println("Eventos totales: " + sistema.getEventos().size());

            // 5️⃣ Mostrar resumen del sistema
            System.out.println("\n--- Estado del sistema ---");
            System.out.println(sistema);

        } catch (Exception e) {
            System.err.println("❌ Error en prueba: " + e.getMessage());
        }
    }
}

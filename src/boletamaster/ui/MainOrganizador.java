package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;
import boletamaster.eventos.Venue;
import boletamaster.tiquetes.*;
import boletamaster.usuarios.Organizador;
import boletamaster.usuarios.Usuario;
import logica.BoletamasterSystem;

import java.time.LocalDateTime;
import java.util.List;

public class MainOrganizador {
    public static void main(String[] args) {
    	BoletamasterSystem core = BoletamasterSystem.getInstance();
    	
        Sistema sistema = new Sistema(core);


        System.out.println("=== BoletaMaster - Interfaz Organizador ===");
        String login = ConsoleUtils.readLine("Login");
        String pass = ConsoleUtils.readPassword("Password");

        Usuario u = sistema.buscarUsuarioPorLogin(login);
        if (u == null || !u.checkPassword(pass) || !(u instanceof Organizador)) {
            System.out.println("Credenciales inválidas o no es organizador. Saliendo.");
            return;
        }

        Organizador org = (Organizador) u;

        while (true) {
            System.out.println("\n--- Menú Organizador ---");
            System.out.println("1. Ver eventos activos");
            System.out.println("2. Crear evento");
            System.out.println("3. Crear localidad en un evento");
            System.out.println("4. Reservar ticket para invitado");
            System.out.println("5. Ver cantidad por tipo de tickets en un evento");
            System.out.println("6. Salir");
            int opt = ConsoleUtils.readInt("Elija opción", 1, 6);
            try {
                switch (opt) {
                    case 1:
                        verEventosActivos(sistema, org);
                        break;
                    case 2:
                        crearEvento(sistema, org);
                        break;
                    case 3:
                        crearLocalidad(sistema, org);
                        break;
                    case 4:
                        reservarParaInvitado(sistema, org);
                        break;
                    case 5:
                        cantidadPorTipo(sistema, org);
                        break;
                    case 6:
                        System.out.println("Adiós.");
                        return;
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void verEventosActivos(Sistema sistema, Organizador org) {
        List<Evento> evs = org.eventosActivos(sistema);
        if (evs.isEmpty()) {
            System.out.println("No tiene eventos activos.");
            return;
        }
        for (Evento e : evs) System.out.println(e);
    }

    private static void crearEvento(Sistema sistema, Organizador org) {
        String id = ConsoleUtils.readLine("Id evento (único)");
        String nombre = ConsoleUtils.readLine("Nombre evento");
        String venueId = ConsoleUtils.readLine("Id venue existente");
        Venue v = buscarVenuePorId(sistema, venueId);
        if (v == null) {
            System.out.println("Venue no existe.");
            return;
        }
        String fechaStr = ConsoleUtils.readLine("Fecha y hora (YYYY-MM-DDTHH:MM) e.g. 2025-12-01T20:00");
        LocalDateTime fecha = LocalDateTime.parse(fechaStr);
        Evento e = new Evento(id, nombre, fecha, v, org);
        sistema.registrarEvento(e);
        System.out.println("Evento creado.");
    }

    private static Venue buscarVenuePorId(Sistema sistema, String id) {
        for (Object o : sistema.getRepo().getVenues()) {
            if (!(o instanceof Venue)) continue;
            Venue v = (Venue) o;
            if (v.getId().equals(id)) return v;
        }
        return null;
    }

    private static void crearLocalidad(Sistema sistema, Organizador org) {
        String eventoId = ConsoleUtils.readLine("Id del evento");
        Evento e = buscarEventoPorId(sistema, eventoId);
        if (e == null) {
            System.out.println("Evento no encontrado.");
            return;
        }
        if (!e.getOrganizador().getLogin().equals(org.getLogin())) {
            System.out.println("No es el organizador de este evento.");
            return;
        }
        String id = ConsoleUtils.readLine("Id localidad");
        String nombre = ConsoleUtils.readLine("Nombre localidad");
        double precio = ConsoleUtils.readDouble("Precio base", 0, Double.MAX_VALUE);
        int capacidad = ConsoleUtils.readInt("Capacidad", 1, Integer.MAX_VALUE);
        int numerada = ConsoleUtils.readInt("Numerada? 1=Si 2=No", 1, 2);
        Localidad loc = new Localidad(id, nombre, precio, capacidad, numerada==1);
        if (numerada==1) {
            while (true) {
                String asiento = ConsoleUtils.readLine("Agregar asiento (o dejar vacío para terminar)");
                if (asiento.isEmpty()) break;
                loc.addAsiento(asiento);
            }
        }
        e.addLocalidad(loc);
        System.out.println("Localidad creada.");
    }

    private static Evento buscarEventoPorId(Sistema sistema, String id) {
        for (Object o : sistema.getRepo().getEventos()) {
            if (!(o instanceof Evento)) continue;
            Evento e = (Evento) o;
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    private static void reservarParaInvitado(Sistema sistema, Organizador org) {
        String eventoId = ConsoleUtils.readLine("Id del evento");
        Evento e = buscarEventoPorId(sistema, eventoId);
        if (e == null) { System.out.println("Evento no existe"); return; }
        String ticketId = ConsoleUtils.readLine("Id del ticket a reservar");
        Ticket t = buscarTicketPorId(sistema, ticketId);
        if (t == null) { System.out.println("Ticket no encontrado"); return; }
        String invitadoLogin = ConsoleUtils.readLine("Login del invitado");
        Usuario invitado = sistema.buscarUsuarioPorLogin(invitadoLogin);
        if (invitado == null) { System.out.println("Usuario invitado no existe"); return; }

        org.reservarTicketParaInvitado(e, t, invitado);
        System.out.println("Ticket reservado para invitado.");
    }

    private static Ticket buscarTicketPorId(Sistema sistema, String id) {
        for (Object o : sistema.getRepo().getTickets()) {
            if (!(o instanceof Ticket)) continue;
            Ticket t = (Ticket) o;
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    private static void cantidadPorTipo(Sistema sistema, Organizador org) {
        String eventoId = ConsoleUtils.readLine("Id del evento");
        Evento e = buscarEventoPorId(sistema, eventoId);
        if (e == null) { System.out.println("Evento no existe"); return; }
        System.out.println(org.cantidadTipoTickets(e));
    }
}

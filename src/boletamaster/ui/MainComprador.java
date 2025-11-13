package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.ui.MarketplaceUI;
import boletamaster.tiquetes.Ticket;
import boletamaster.transacciones.Compra;
import boletamaster.usuarios.Comprador;
import boletamaster.usuarios.Usuario;

public class MainComprador {

    public static void main(String[] args) {
        Sistema sistema = new Sistema();

        System.out.println("=== BoletaMaster - Interfaz Comprador ===");
        String login = ConsoleUtils.readLine("Login");
        String pass = ConsoleUtils.readPassword("Password");

        Usuario u = sistema.buscarUsuarioPorLogin(login);
        if (u == null || !u.checkPassword(pass) || !(u instanceof Comprador)) {
            System.out.println("Credenciales inválidas o no es comprador. Saliendo.");
            return;
        }

        Comprador comprador = (Comprador) u;
        Marketplace marketplace = new Marketplace(sistema);
        MarketplaceUI marketplaceUI = new MarketplaceUI(marketplace, sistema);

        while (true) {
            System.out.println("\n--- Menú Comprador ---");
            System.out.println("1. Ver saldo");
            System.out.println("2. Ver mis tickets");
            System.out.println("3. Comprar ticket (plataforma)");
            System.out.println("4. Ir al Marketplace");
            System.out.println("5. Salir");

            int opt = ConsoleUtils.readInt("Elija opción", 1, 5);
            try {
                switch (opt) {
                    case 1:
                        System.out.println("Saldo actual: " + comprador.getSaldo());
                        break;
                    case 2:
                        mostrarMisTickets(sistema, comprador);
                        break;
                    case 3:
                        comprarEnPlataforma(sistema, comprador);
                        break;
                    case 4:
                        marketplaceUI.menu();
                        break;
                    case 5:
                        System.out.println("Sesión finalizada. ¡Hasta luego!");
                        return;
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void mostrarMisTickets(Sistema sistema, Comprador comprador) {
        System.out.println("\n--- Mis Tickets ---");
        boolean hayTickets = false;
        for (Object o : sistema.getRepo().getTickets()) {
            if (!(o instanceof Ticket)) continue;
            Ticket t = (Ticket) o;
            if (t.getPropietario() != null && 
                t.getPropietario().getLogin().equals(comprador.getLogin())) {
                System.out.println("ID: " + t.getId() + " | Estado: " + t.getEstado() + 
                                   " | Precio base: " + t.getPrecioBase());
                hayTickets = true;
            }
        }
        if (!hayTickets) System.out.println("No tienes tickets registrados.");
    }

    private static void comprarEnPlataforma(Sistema sistema, Comprador comprador) {
        String ticketId = ConsoleUtils.readLine("ID del ticket a comprar");
        Ticket t = buscarTicketGlobalPorId(sistema, ticketId);
        if (t == null) {
            System.out.println("El ticket no existe.");
            return;
        }
        Compra c = sistema.comprarTicket(comprador, t);
        System.out.println("Compra realizada correctamente. Monto: " + c.getMontoTotal());
    }

    private static Ticket buscarTicketGlobalPorId(Sistema sistema, String id) {
        for (Object o : sistema.getRepo().getTickets()) {
            if (o instanceof Ticket) {
                Ticket t = (Ticket) o;
                if (t.getId().equals(id)) return t;
            }
        }
        return null;
    }
}

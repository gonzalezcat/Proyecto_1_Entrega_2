package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.Oferta;
import boletamaster.usuarios.Usuario;
import boletamaster.tiquetes.Ticket;
import java.util.List;

public class MarketplaceUI {
    private final Marketplace marketplace;
    private final Sistema sistema;

    public MarketplaceUI(Marketplace marketplace, Sistema sistema) {
        this.marketplace = marketplace;
        this.sistema = sistema;
    }

    public void menu(Usuario usuario) {
        while (true) {
            System.out.println("\n=== MARKETPLACE ===");
            System.out.println("1. Publicar ticket en reventa");
            System.out.println("2. Ver ofertas activas");
            System.out.println("3. Comprar oferta");
            System.out.println("4. Contraofertar");
            System.out.println("5. Cancelar mi oferta");
            System.out.println("6. Volver");
            int op = ConsoleUtils.readInt("Opción", 1, 6);

            try {
                switch (op) {
                    case 1:
                        String ticketId = ConsoleUtils.readLine("Id del ticket");
                        double precio = ConsoleUtils.readDouble("Precio de venta", 0, Double.MAX_VALUE);
                        marketplace.publicarOferta(ticketId, usuario, precio);
                        System.out.println("Oferta publicada.");
                        break;
                    case 2:
                        listarOfertas();
                        break;
                    case 3:
                        String id = ConsoleUtils.readLine("Id de la oferta");
                        marketplace.comprarOferta(id, usuario);
                        System.out.println("Compra realizada.");
                        break;
                    case 4:
                        String idc = ConsoleUtils.readLine("Id de la oferta");
                        double monto = ConsoleUtils.readDouble("Monto ofrecido", 0, Double.MAX_VALUE);
                        marketplace.contraOfertar(idc, usuario, monto);
                        System.out.println("Contraoferta registrada.");
                        break;
                    case 5:
                        String idCancel = ConsoleUtils.readLine("Id de la oferta a cancelar");
                        marketplace.cancelarOferta(idCancel, usuario);
                        System.out.println("Oferta cancelada.");
                        break;
                    case 6:
                        return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listarOfertas() {
        List<Oferta> of = marketplace.listarOfertasActivas();
        if (of.isEmpty()) {
            System.out.println("No hay ofertas activas.");
            return;
        }
        for (Oferta o : of) {
            Ticket t = o.getTicket();
            System.out.println("ID: " + o.getId() + " | Ticket: " + t.getId() + " | Precio: " + o.getPrecioPublico() +
                    " | Vendedor: " + o.getVendedor().getLogin());
        }
    }
}


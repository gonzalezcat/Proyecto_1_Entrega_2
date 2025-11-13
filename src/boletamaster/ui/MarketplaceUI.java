package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.Oferta;
import boletamaster.marketplace.Oferta.ContraOferta;
import boletamaster.tiquetes.Ticket;
import boletamaster.tiquetes.TicketDeluxe;
import boletamaster.usuarios.Comprador;
import boletamaster.usuarios.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class MarketplaceUI {
    private final Marketplace marketplace;
    private final Sistema sistema;
    private final Comprador cliente; // New field to hold the authenticated client

    public MarketplaceUI(Marketplace marketplace, Sistema sistema, Comprador cliente) {
        this.marketplace = marketplace;
        this.sistema = sistema;
        this.cliente = cliente;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== MARKETPLACE (Cliente: " + cliente.getLogin() + ") ===");
            System.out.println("1. Publicar Ticket en Reventa");
            System.out.println("2. Ver Ofertas y Contraofertar");
            System.out.println("3. Ver mis ofertas y Aceptar Contraofertas");
            System.out.println("4. Volver al menú principal");

            int op = ConsoleUtils.readInt("Opción", 1, 4);

            try {
                switch (op) {
                    case 1 -> publicarOfertaResaleUI();
                    case 2 -> listarYContraOfertarUI();
                    case 3 -> gestionarMisOfertasUI();
                    case 4 -> { return; }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // --- New Resale UI Methods ---

    private void publicarOfertaResaleUI() {
        // 1. Show tickets owned by the client
        List<Ticket> misTickets = sistema.obtenerTicketsPorPropietario(cliente); 
        if (misTickets.isEmpty()) {
            System.out.println("No tienes tickets para revender.");
            return;
        }

        System.out.println("\n--- Mis Tickets para Reventa ---");
        for (int i = 0; i < misTickets.size(); i++) {
            Ticket t = misTickets.get(i);
            // Basic check if it's potentially resellable (Marketplace logic has full validation)
            if (!(t instanceof TicketDeluxe)) { 
                 System.out.printf("%d. ID: %s | Evento: %s | Localidad: %s\n", 
                                i + 1, t.getId(), t.getEvento().getNombre(), t.getLocalidad().getNombre());
            }
        }

        String idTicket = ConsoleUtils.readLine("ID del ticket a revender");
        Ticket ticketSeleccionado = sistema.buscarTicketGlobalPorId(idTicket); 

        if (ticketSeleccionado == null || !ticketSeleccionado.getPropietario().equals(cliente)) {
            System.out.println("Ticket inválido o no te pertenece.");
            return;
        }

        double precio = ConsoleUtils.readDouble("Precio de venta inicial", 0.01, Double.MAX_VALUE);

        try {
            Oferta o = marketplace.publicarOfertaResale(ticketSeleccionado, cliente, precio);
            System.out.println("Oferta publicada con ID: " + o.getId());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error al publicar oferta: " + e.getMessage());
        }
    }

    private void listarYContraOfertarUI() {
        List<Oferta> ofertas = marketplace.listarOfertasVigentes();
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas vigentes.");
            return;
        }

        System.out.println("\n--- Ofertas de Reventa Vigentes ---");
        for (Oferta o : ofertas) {
            System.out.printf("ID: %s | Ticket: %s | Vendedor: %s | Precio Actual: %.2f\n",
                              o.getId(), o.getTicket().getId(), o.getVendedor().getLogin(), o.getPrecioActual());
            if (!o.getContraOfertas().isEmpty()) {
                 ContraOferta lastCo = o.getContraOfertas().get(o.getContraOfertas().size() - 1);
                 System.out.printf("   > Última contraoferta de %s por %.2f.\n", lastCo.getComprador().getLogin(), lastCo.getPrecio());
            }
        }
        
        String op = ConsoleUtils.readLine("ID de oferta a contraofertar (o ENTER para volver)");
        if (op.isEmpty()) return;

        try {
            double precio = ConsoleUtils.readDouble("Tu precio propuesto", 0.01, Double.MAX_VALUE);
            marketplace.hacerContraOferta(op, cliente, precio);
            System.out.println("Contraoferta realizada con éxito.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void gestionarMisOfertasUI() {
        // Find client's active offers
        List<Oferta> misOfertas = marketplace.listarOfertasVigentes().stream()
                .filter(o -> o.getVendedor().equals(cliente))
                .collect(Collectors.toList());

        if (misOfertas.isEmpty()) {
            System.out.println("No tienes ofertas activas.");
            return;
        }
        
        System.out.println("\n--- Mis Ofertas Activas ---");
        for (Oferta o : misOfertas) {
            System.out.printf("ID: %s | Precio Inicial: %.2f | Precio Actual: %.2f | Contraofertas: %d\n",
                    o.getId(), o.getPrecioPublico(), o.getPrecioActual(), o.getContraOfertas().size());
        }
        
        System.out.println("\n¿Qué deseas hacer?");
        System.out.println("1. Aceptar la última Contraoferta");
        System.out.println("2. Cancelar una oferta mía");
        System.out.println("3. Volver");
        int op = ConsoleUtils.readInt("Opción", 1, 3);
        
        String idOferta = ConsoleUtils.readLine("ID de la oferta");
        
        try {
            if (op == 1) {
                String pass = ConsoleUtils.readPassword("Ingresa tu contraseña para confirmar la venta");
                marketplace.aceptarOfertaOContraOferta(idOferta, cliente, pass);
                System.out.println("¡Venta completada! El saldo ha sido transferido.");
            } else if (op == 2) {
                marketplace.borrarOfertaPorCliente(idOferta, cliente);
                System.out.println("Oferta cancelada exitosamente. Registro guardado en el log.");
            }
        } catch (Exception e) {
            System.out.println("Error en la gestión: " + e.getMessage());
        }
    }
}
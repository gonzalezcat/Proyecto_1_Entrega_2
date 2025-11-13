package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.Oferta;
import boletamaster.marketplace.LogRegistro;
import boletamaster.eventos.Localidad;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MarketplaceUI {
    private final Marketplace marketplace;
    private final Sistema sistema;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MarketplaceUI(Marketplace marketplace, Sistema sistema) {
        this.marketplace = marketplace;
        this.sistema = sistema;
    }

    public void menu() {
        while (true) {
            System.out.println("\n=== MARKETPLACE ===");
            System.out.println("1. Publicar oferta");
            System.out.println("2. Ver ofertas vigentes");
            System.out.println("3. Cancelar oferta");
            System.out.println("4. Ver log");
            System.out.println("5. Volver");

            int op = ConsoleUtils.readInt("Opción", 1, 5);

            try {
                switch (op) {
                    case 1 -> publicarOfertaUI();
                    case 2 -> listarOfertas();
                    case 3 -> cancelarOfertaUI();
                    case 4 -> verLog();
                    case 5 -> { return; }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void publicarOfertaUI() {
        String nombreLoc = ConsoleUtils.readLine("Nombre de la localidad");
        Localidad loc = sistema.buscarLocalidad(nombreLoc);
        if (loc == null) {
            System.out.println("Localidad no encontrada.");
            return;
        }

        double porcentaje = ConsoleUtils.readDouble("Porcentaje de descuento (0 a 1)", 0.01, 0.99);

        LocalDateTime inicio = leerFechaHora("Fecha y hora inicio (yyyy-MM-dd HH:mm)");
        LocalDateTime fin = leerFechaHora("Fecha y hora fin (yyyy-MM-dd HH:mm)");
        if (inicio == null || fin == null) {
            System.out.println("Fechas inválidas. Operación cancelada.");
            return;
        }

        try {
            boletamaster.eventos.Oferta o = marketplace.publicarOferta(loc, porcentaje, inicio, fin);
            System.out.println("Oferta publicada con ID: " + o.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error al publicar oferta: " + e.getMessage());
        }
    }

    private LocalDateTime leerFechaHora(String prompt) {
        while (true) {
            String input = ConsoleUtils.readLine(prompt);
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use yyyy-MM-dd HH:mm.");
            }
        }
    }

    private void cancelarOfertaUI() {
        String id = ConsoleUtils.readLine("ID de la oferta a cancelar");
        try {
            marketplace.cancelarOferta(id);
            System.out.println("Oferta cancelada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarOfertas() {
        List<boletamaster.eventos.Oferta> ofertas = marketplace.listarOfertasVigentes();
        if (ofertas.isEmpty()) {
            System.out.println("No hay ofertas vigentes.");
            return;
        }

        for (boletamaster.eventos.Oferta o : ofertas) {
            Localidad loc = o.getLocalidad();
            System.out.println("ID: " + o.getId() +
                               " | Localidad: " + loc.getNombre() +
                               " | Descuento: " + (o.getPorcentajeDescuento() * 100) + "%" +
                               " | Vigente: " + o.estaVigente());
        }
    }

    private void verLog() {
        List<LogRegistro> logs = marketplace.getLog();
        if (logs.isEmpty()) {
            System.out.println("No hay registros.");
            return;
        }

        for (LogRegistro log : logs) {
            System.out.println(log.getDescripcion() + " | " + log.getFechaHora());
        }
    }
}

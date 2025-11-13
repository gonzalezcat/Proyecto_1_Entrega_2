package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.LogRegistro;
import boletamaster.usuarios.Administrador;
import boletamaster.usuarios.Usuario;
import boletamaster.eventos.Evento;
import boletamaster.transacciones.Reembolso;
import java.util.List;

public class MainAdministrador {

    public static void main(String[] args) {
        Sistema sistema = new Sistema();

        System.out.println("=== BoletaMaster - Interfaz Administrador ===");
        String login = ConsoleUtils.readLine("Login");
        String pass = ConsoleUtils.readPassword("Password");

        Usuario u = sistema.buscarUsuarioPorLogin(login);
        if (u == null || !u.checkPassword(pass) || !(u instanceof Administrador)) {
            System.out.println("Credenciales inválidas o no es administrador. Saliendo.");
            return;
        }

        Administrador admin = (Administrador) u;
        Marketplace marketplace = new Marketplace(sistema);

        while (true) {
            System.out.println("\n--- Menú Administrador ---");
            System.out.println("1. Cancelar evento y realizar reembolsos");
            System.out.println("2. Consultar log del Marketplace");
            System.out.println("3. Ver ganancias generales");
            System.out.println("4. Salir");

            int opt = ConsoleUtils.readInt("Opción", 1, 4);
            try {
                switch (opt) {
                    case 1:
                        String id = ConsoleUtils.readLine("Nombre del evento a cancelar");
                        Evento e = buscarEventoPorNombre(sistema, id);
                        if (e == null) {
                            System.out.println("Evento no encontrado.");
                        } else {
                            List<Reembolso> reembolsos = sistema.cancelarEventoYReembolsar(e, admin);
                            System.out.println("Evento cancelado. Se realizaron " + reembolsos.size() + " reembolsos.");
                        }
                        break;
                    case 2:
                        List<LogRegistro> logs = sistema.getRepo().getLog();
                        if (logs == null || logs.isEmpty()) {
                            System.out.println("El log del Marketplace está vacío.");
                        } else {
                            System.out.println("\n--- Log de Actividades ---");
                            for (LogRegistro l : logs) {
                                System.out.println(l.getFechaHora() + " | " + l.getDescripcion());
                            }
                        }
                        break;

                    case 3:
                        double[] datos = admin.gananciasGenerales(sistema);
                        System.out.println("Ventas totales: " + datos[0]);
                        System.out.println("Ganancias plataforma: " + datos[1]);
                        break;

                        
                   
                    case 4:
                        System.out.println("Sesión finalizada. Hasta luego.");
                        return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static Evento buscarEventoPorNombre(Sistema sistema, String nombre) {
        for (Object o : sistema.getRepo().getEventos()) {
            if (o instanceof Evento) {
                Evento e = (Evento) o;
                if (e.getNombre().equalsIgnoreCase(nombre)) return e;
            }
        }
        return null;
    }
}

package boletamaster.ui;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.LogRegistro;
import boletamaster.usuarios.Administrador;
import boletamaster.usuarios.Usuario;

import java.util.List;

public class MainAdministrador {
    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        Marketplace marketplace = new Marketplace();
        InitialData.seed(sistema);

        System.out.println("=== BoletaMaster - Interfaz Administrador ===");
        String login = ConsoleUtils.readLine("Login");
        String pass = ConsoleUtils.readPassword("Password");

        Usuario u = sistema.buscarUsuarioPorLogin(login);
        if (u == null || !u.checkPassword(pass) || !(u instanceof Administrador)) {
            System.out.println("Credenciales inválidas o no es administrador. Saliendo.");
            return;
        }

        Administrador admin = (Administrador) u;

        while (true) {
            System.out.println("\n--- Menú Administrador ---");
            System.out.println("1. Ver log del Marketplace");
            System.out.println("2. Eliminar oferta del Marketplace");
            System.out.println("3. Ver ganancias generales");
            System.out.println("4. Salir");
            int opt = ConsoleUtils.readInt("Elija opción", 1, 4);
            try {
                switch (opt) {
                    case 1:
                        verLog(marketplace);
                        break;
                    case 2:
                        eliminarOferta(marketplace, admin);
                        break;
                    case 3:
                        verGanancias(sistema, admin);
                        break;
                    case 4:
                        System.out.println("Adiós.");
                        return;
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private static void verLog(Marketplace marketplace) {
        List<LogRegistro> logs = marketplace.obtenerLog();
        if (logs.isEmpty()) {
            System.out.println("Log vacío.");
            return;
        }
        for (LogRegistro r : logs) System.out.println(r);
    }

    private static void eliminarOferta(Marketplace marketplace, Administrador admin) {
        String id = ConsoleUtils.readLine("Id de la oferta a eliminar");
        marketplace.eliminarOfertaPorAdmin(id, admin);
        System.out.println("Oferta eliminada (registro en log creado).");
    }

    private static void verGanancias(Sistema sistema, Administrador admin) {
        double[] res = admin.gananciasGenerales(sistema);
        System.out.println("Ventas totales: " + res[0]);
        System.out.println("Ganancias plataforma: " + res[1]);
    }
}

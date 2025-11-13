package boletamaster.ui;

import java.util.Scanner;

public class ConsoleUtils {
	private static final Scanner SC = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt + ": ");
        return SC.nextLine().trim();
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                String s = readLine(prompt);
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Valor fuera de rango ("+min+" - "+max+"). Intente nuevamente.");
                } else return v;
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            try {
                String s = readLine(prompt);
                double v = Double.parseDouble(s);
                if (v < min || v > max) {
                    System.out.println("Valor fuera de rango ("+min+" - "+max+"). Intente nuevamente.");
                } else return v;
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Ingrese un número válido.");
            }
        }
    }

    public static String readPassword(String prompt) {
        return readLine(prompt); 
    }

    public static void pausa() {
        System.out.println("Presione Enter para continuar...");
        SC.nextLine();
    }

}

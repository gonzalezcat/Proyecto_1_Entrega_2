package boletamaster.persistence;

import java.io.*;
import java.util.*;

public class DataManager {

    public static <T> void guardarLista(String ruta, List<T> lista) {
        crearDirectorio(ruta);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Error guardando " + ruta + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> cargarLista(String ruta) {
        File f = new File(ruta);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error cargando " + ruta + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void crearDirectorio(String ruta) {
        File dir = new File(ruta).getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
    }
}

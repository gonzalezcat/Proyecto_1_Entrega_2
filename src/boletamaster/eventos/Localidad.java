package boletamaster.eventos;

import java.util.ArrayList;
import java.util.List;

public class Localidad {
    private final String id;
    private final String nombre;
    private final double precioBase;
    private final int capacidad;
    private final boolean numerada;
    private final List<String> asientosNumerados; // ids o etiquetas de asientos

    public Localidad(String id, String nombre, double precioBase, int capacidad, boolean numerada) {
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.capacidad = capacidad;
        this.numerada = numerada;
        this.asientosNumerados = numerada ? new ArrayList<>() : null;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public int getCapacidad() { return capacidad; }
    public boolean isNumerada() { return numerada; }
    public List<String> getAsientosNumerados() { return asientosNumerados; }

    public void addAsiento(String etiqueta) {
        if (!numerada) throw new IllegalStateException("Localidad no numerada");
        this.asientosNumerados.add(etiqueta);
    }

    @Override
    public String toString() {
        return "Localidad{" + nombre + ", precioBase=" + precioBase + ", capacidad=" + capacidad + ", numerada=" + numerada + '}';
    }
}

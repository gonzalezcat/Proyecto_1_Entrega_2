package boletamaster.eventos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import boletamaster.usuarios.Organizador;

public class Evento {
    private final String id;
    private final String nombre;
    private final LocalDateTime fechaHora;
    private final Venue venue;
    private final Organizador organizador;
    private final List<Localidad> localidades;
    private boolean cancelado;

    public Evento(String id, String nombre, LocalDateTime fechaHora, Venue venue, Organizador organizador) {
        this.id = id;
        this.nombre = nombre;
        this.fechaHora = fechaHora;
        this.venue = venue;
        this.organizador = organizador;
        this.localidades = new ArrayList<>();
        this.cancelado = false;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Venue getVenue() { return venue; }
    public Organizador getOrganizador() { return organizador; }
    public List<Localidad> getLocalidades() { return localidades; }
    public boolean isCancelado() { return cancelado; }
    public void cancelar() { this.cancelado = true; }

    public void addLocalidad(Localidad loc) { this.localidades.add(loc); }

    @Override
    public String toString() {
        return "Evento{" + nombre + " en " + venue.getNombre() + " el " + fechaHora + ", organizador=" + organizador.getNombre() + "}";
    }
}


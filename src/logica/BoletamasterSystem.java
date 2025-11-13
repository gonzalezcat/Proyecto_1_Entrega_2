package logica;

import boletamaster.persistence.SimpleRepository;
import boletamaster.marketplace.Marketplace;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.*;
import boletamaster.usuarios.*;
import boletamaster.eventos.*;

import java.util.ArrayList;
import java.util.List;

public class BoletamasterSystem {

    private static BoletamasterSystem instance;

    private final SimpleRepository repo;
    private final Marketplace marketplace;
    private final GestorFinanzas gestorFinanzas;
    private final GestorVentas gestorVentas;
    private final GestorTiquetes gestorTiquetes;
    private final Reporteador reporteador;

    private final List<Evento> eventos;
    private final List<Venue> venues;
    private final List<Usuario> usuarios;

    private boolean testingMode = false;

    private BoletamasterSystem() {
        this.repo = new SimpleRepository();

        boletamaster.app.Sistema facade = new boletamaster.app.Sistema();

        this.marketplace = new Marketplace(facade);
        this.gestorFinanzas = new GestorFinanzas(facade);
        this.gestorVentas = new GestorVentas(facade, gestorFinanzas, marketplace);
        this.gestorTiquetes = new GestorTiquetes(facade);
        this.reporteador = new Reporteador(facade, gestorFinanzas);

        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public static BoletamasterSystem getInstance() {
        if (instance == null) instance = new BoletamasterSystem();
        return instance;
    }

    public static void resetInstance() { instance = null; }

    // ===== Usuarios =====
    public void registrarUsuario(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Usuario nulo");
        usuarios.add(u);
        repo.addUsuario(u);
    }

    public Usuario buscarUsuario(String login) {
        for (Usuario u : usuarios)
            if (u.getLogin().equals(login)) return u;
        return null;
    }

    // ===== Venues y eventos =====
    public void agregarVenue(Venue v) {
        if (v == null) throw new IllegalArgumentException("Venue nulo");
        venues.add(v);
        repo.addVenue(v);
    }

    public void agregarEvento(Evento e) {
        if (e == null) throw new IllegalArgumentException("Evento nulo");
        eventos.add(e);
        repo.addEvento(e);
    }

    public List<Evento> getEventos() { return new ArrayList<>(eventos); }
    public List<Venue> getVenues() { return new ArrayList<>(venues); }
    public List<Usuario> getUsuarios() { return new ArrayList<>(usuarios); }

    // ===== Getters =====
    public SimpleRepository getRepo() { return repo; }
    public Marketplace getMarketplace() { return marketplace; }
    public GestorFinanzas getGestorFinanzas() { return gestorFinanzas; }
    public GestorVentas getGestorVentas() { return gestorVentas; }
    public GestorTiquetes getGestorTiquetes() { return gestorTiquetes; }
    public Reporteador getReporteador() { return reporteador; }

    @Override
    public String toString() {
        return "BoletamasterSystem {" +
                "usuarios=" + usuarios.size() +
                ", eventos=" + eventos.size() +
                ", venues=" + venues.size() +
                ", tickets=" + repo.getTickets().size() +
                '}';
    }
}


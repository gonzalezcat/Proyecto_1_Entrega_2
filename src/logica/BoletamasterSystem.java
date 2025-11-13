package logica;

import boletamaster.app.Sistema;
import boletamaster.marketplace.*;
import boletamaster.persistence.SimpleRepository;
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
    private final GestorOfertasImpl gestorOfertas;
    private final GestorVentas gestorVentas;
    private final GestorTiquetes gestorTiquetes;
    private final Reporteador reporteador;

    private final List<Evento> eventos;
    private final List<Venue> venues;
    private final List<Usuario> usuarios;

    private boolean testingMode = false;

    private BoletamasterSystem() {
        this.repo = new SimpleRepository();

        this.marketplace = new Marketplace(new SistemaStub(this));
        this.gestorFinanzas = new GestorFinanzas(new SistemaStub(this));
        this.gestorOfertas = new GestorOfertasImpl(marketplace);
        this.gestorVentas = new GestorVentas(new SistemaStub(this), gestorFinanzas, gestorOfertas);
        this.gestorTiquetes = new GestorTiquetes(new SistemaStub(this));
        this.reporteador = new Reporteador(new SistemaStub(this), gestorFinanzas);

        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public static BoletamasterSystem getInstance() {
        if (instance == null) instance = new BoletamasterSystem();
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }



    public void registrarUsuario(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Usuario nulo");
        usuarios.add(u);
        repo.addUsuario(u);
    }

    public Usuario buscarUsuario(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) return u;
        }
        return null;
    }

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

    public List<Evento> getEventos() {
        return new ArrayList<>(eventos);
    }

    public List<Venue> getVenues() {
        return new ArrayList<>(venues);
    }

    public void registrarTicket(Ticket t) {
        repo.addTicket(t);
    }

    public List<Ticket> getTickets() {
        return repo.getTickets();
    }


    public void registrarTransaccion(Object transaccion) {
        repo.addTransaccion(transaccion);
    }

    public List<Object> getTransacciones() {
        return repo.getTransacciones();
    }


    public SimpleRepository getRepo() {
        return repo;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public GestorFinanzas getGestorFinanzas() {
        return gestorFinanzas;
    }

    public GestorOfertasImpl getGestorOfertas() {
        return gestorOfertas;
    }

    public GestorVentas getGestorVentas() {
        return gestorVentas;
    }

    public GestorTiquetes getGestorTiquetes() {
        return gestorTiquetes;
    }

    public Reporteador getReporteador() {
        return reporteador;
    }

    public boolean isTestingMode() {
        return testingMode;
    }

    public void setTestingMode(boolean testingMode) {
        this.testingMode = testingMode;
    }

    @Override
    public String toString() {
        return "BoletamasterSystem {" +
                "usuarios=" + usuarios.size() +
                ", eventos=" + eventos.size() +
                ", venues=" + venues.size() +
                ", tickets=" + repo.getTickets().size() +
                '}';
    }

    public Sistema getSistema() {
        return new SistemaStub(this);
    }

    
    private static class SistemaStub extends boletamaster.app.Sistema {
        private final BoletamasterSystem core;

        public SistemaStub(BoletamasterSystem core) {
            super(); // evita bucles
            this.core = core;
        }

        @Override
        public SimpleRepository getRepo() {
            return core.getRepo();
        }
    }
}

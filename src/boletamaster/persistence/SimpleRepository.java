package boletamaster.persistence;

import java.util.ArrayList;
import java.util.List;


public class SimpleRepository {
    private final List<Object> usuarios = new ArrayList<>();
    private final List<Object> eventos = new ArrayList<>();
    private final List<Object> tickets = new ArrayList<>();
    private final List<Object> transacciones = new ArrayList<>();
    private final List<Object> venues = new ArrayList<>();


    public void addUsuario(Object u) { usuarios.add(u); }
    public List<Object> getUsuarios() { return usuarios; }

    public void addVenue(Object v) { venues.add(v); }
    public List<Object> getVenues() { return venues; }

    public void addEvento(Object e) { eventos.add(e); }
    public List<Object> getEventos() { return eventos; }

    public void addTicket(Object t) { tickets.add(t); }
    public List<Object> getTickets() { return tickets; }

    public void addTransaccion(Object t) { transacciones.add(t); }
    public List<Object> getTransacciones() { return transacciones; }

    
}


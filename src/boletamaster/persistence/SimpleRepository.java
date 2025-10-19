package boletamaster.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria simple para usuarios, eventos, tickets y transacciones
 */
public class SimpleRepository {
    private final List<Object> usuarios = new ArrayList<>();
    private final List<Object> eventos = new ArrayList<>();
    private final List<Object> tickets = new ArrayList<>();
    private final List<Object> transacciones = new ArrayList<>();

    public void addUsuario(Object u) { usuarios.add(u); }
    public void addEvento(Object e) { eventos.add(e); }
    public void addTicket(Object t) { tickets.add(t); }
    public void addTransaccion(Object tr) { transacciones.add(tr); }

    public List<Object> getUsuarios() { return usuarios; }
    public List<Object> getEventos() { return eventos; }
    public List<Object> getTickets() { return tickets; }
    public List<Object> getTransacciones() { return transacciones; }
}


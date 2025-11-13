package boletamaster.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import boletamaster.persistence.SimpleRepository;
import boletamaster.eventos.*;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.*;
import boletamaster.usuarios.*;
import logica.BoletamasterSystem;

public class Sistema {

    private final BoletamasterSystem core;

    public Sistema() {
        this.core = BoletamasterSystem.getInstance();
    }

    // ===== Usuarios =====
    public void registrarUsuario(Usuario u) {
        core.registrarUsuario(u);
    }

    public Usuario buscarUsuarioPorLogin(String login) {
        return core.buscarUsuario(login);
    }

    // ===== Venues y Eventos =====
    public void registrarVenue(Venue v) {
        core.agregarVenue(v);
    }

    public void registrarEvento(Evento e) {
        core.agregarEvento(e);
    }

    public List<Evento> eventosActivosPorOrganizador(Organizador org) {
        List<Evento> resultado = new ArrayList<>();
        for (Evento e : core.getEventos()) {
            if (e.getOrganizador().equals(org)) resultado.add(e);
        }
        return resultado;
    }

    // ===== Tickets =====
    public Ticket generarTicketSimple(Localidad loc) {
        return core.getGestorTiquetes().crearTicketSimple(loc);
    }

    public Ticket generarTicketNumerado(Localidad loc, String asiento) {
        return core.getGestorTiquetes().crearTicketNumerado(loc, asiento);
    }

    public TicketMultiple generarTicketMultiple(Localidad loc, int cantidad, double precioPaquete) {
        return core.getGestorTiquetes().crearTicketMultiple(loc, cantidad, precioPaquete);
    }

    public TicketDeluxe generarTicketDeluxe(Localidad loc) {
        return core.getGestorTiquetes().crearTicketDeluxe(loc);
    }

    // ===== Compras y Transferencias =====
    public Compra comprarTicket(Usuario comprador, Ticket t) {
        if (!(comprador instanceof Comprador)) 
            throw new IllegalArgumentException("El usuario no es un comprador válido");
        return core.getGestorVentas().procesarCompra((Comprador) comprador, Collections.singletonList(t), true);
    }

    public void transferirTicket(Ticket t, Usuario actual, String password, Usuario nuevo) {
        core.getGestorVentas().transferirTicket(t, actual, password, nuevo);
    }

    // ===== Cancelación de eventos y reembolsos =====
    public List<Reembolso> cancelarEventoYReembolsar(Evento e, Administrador admin) {
        List<Reembolso> resultado = new ArrayList<>();
        for (Ticket t : core.getRepo().getTickets()) {
            if (t.getEvento() != null && t.getEvento().equals(e) && t.getPropietario() != null) {
                double monto = t.getPrecioBase() + (t.getPrecioBase() * t.getPorcentajeServicio());
                t.getPropietario().depositarSaldo(monto);
                Reembolso r = new Reembolso(t.getPropietario(), monto);
                core.getRepo().addTransaccion(r);
                resultado.add(r);
                t.setEstado(TicketEstado.CANCELADO);
            }
        }
        return resultado;
    }

    // ===== Reportes =====
    public void reporteFinancieroPorOrganizador(Organizador org) {
        core.getReporteador().generarReportePorOrganizador(org);
    }

    // ===== Repositorio =====
    public SimpleRepository getRepo() {
        return core.getRepo();
    }

    // ===== Configuración global =====
    public void setCuotaFijaGlobal(double cuotaFijaGlobal) {
        core.getGestorFinanzas().setCuotaFijaGlobal(cuotaFijaGlobal);
    }

    public void setPorcentajeServicioGlobal(double porcentajeServicioGlobal) {
        core.getGestorFinanzas().setPorcentajeServicioGlobal(porcentajeServicioGlobal);
    }
    public Localidad buscarLocalidad(String nombre) {
        if (nombre == null || nombre.isEmpty()) return null;

        for (Evento e : core.getEventos()) {
            for (Localidad loc : e.getLocalidades()) { 
                if (loc.getNombre().equalsIgnoreCase(nombre)) {
                    return loc;
                }
            }
        }
        return null; 
}
}

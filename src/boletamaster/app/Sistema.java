package boletamaster.app;

import java.util.List;

import logica.BoletamasterSystem;
import boletamaster.eventos.*;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.*;
import boletamaster.usuarios.*;
import boletamaster.persistence.SimpleRepository;

public class Sistema {

    private final BoletamasterSystem core;

    public Sistema() {
        this.core = BoletamasterSystem.getInstance();
    }


    public void registrarUsuario(Usuario u) {
        core.registrarUsuario(u);
    }

    public Usuario buscarUsuarioPorLogin(String login) {
        return core.buscarUsuario(login);
    }


    public void registrarVenue(Venue v) {
        core.agregarVenue(v);
    }

    public void registrarEvento(Evento e) {
        core.agregarEvento(e);
    }

    public List<Evento> eventosActivosPorOrganizador(Organizador org) {
        return core.getEventos();
    }


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


    public Compra comprarTicket(Usuario comprador, Ticket t) {
        return core.getGestorVentas().procesarCompra((boletamaster.usuarios.Comprador) comprador,
                                                    java.util.Collections.singletonList(t),
                                                    true);
    }

    public void transferirTicket(Ticket t, Usuario actual, String password, Usuario nuevo) {
        core.getGestorVentas().transferirTicket(t, actual, password, nuevo);
    }

    
    public java.util.List<Reembolso> cancelarEventoYReembolsar(Evento e, Administrador admin) {
        
        java.util.List<Reembolso> resultado = new java.util.ArrayList<>();
        for (Ticket t : core.getSistema().getRepo().getTickets()) {
            if (t.getEvento() != null && t.getEvento().equals(e)) {
                if (t.getPropietario() != null) {
                    double monto = t.getPrecioBase() + (t.getPrecioBase() * t.getPorcentajeServicio());
                    t.getPropietario().depositarSaldo(monto);
                    Reembolso r = new Reembolso(t.getPropietario(), monto);
                    core.getSistema().getRepo().addTransaccion(r);
                    resultado.add(r);
                    t.setEstado(boletamaster.tiquetes.TicketEstado.CANCELADO);
                }
            }
        }
        return resultado;
    }

    
    public void reporteFinancieroPorOrganizador(Organizador org) {
        core.getReporteador(); 
        core.getReporteador(); 
    }

    
    public SimpleRepository getRepo() {
     
        return core.getSistema().getRepo();
    }

    public void setCuotaFijaGlobal(double cuotaFijaGlobal) {


    }

    public void setPorcentajeServicioGlobal(double porcentajeServicioGlobal) {

    }
}


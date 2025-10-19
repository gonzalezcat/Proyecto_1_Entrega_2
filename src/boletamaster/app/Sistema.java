package boletamaster.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import boletamaster.persistence.SimpleRepository;
import boletamaster.usuarios.*;
import boletamaster.eventos.*;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.*;

public class Sistema {
    private final SimpleRepository repo;
    private double cuotaFijaGlobal;
    private double porcentajeServicioGlobal;

    public Sistema() {
        this.repo = new SimpleRepository();
        this.cuotaFijaGlobal = 1.0; // default
        this.porcentajeServicioGlobal = 0.10; // 10%
    }

    // usuarios
    public void registrarUsuario(Usuario u) {
        repo.addUsuario(u);
    }

    public Usuario buscarUsuarioPorLogin(String login) {
        for (Object o : repo.getUsuarios()) {
            Usuario u = (Usuario) o;
            if (u.getLogin().equals(login)) return u;
        }
        return null;
    }

    // venues y eventos
    public void registrarVenue(Venue v) { repo.addEvento(v); } // guardamos en eventos 
    public void registrarEvento(Evento e) { repo.addEvento(e); }

    // crear tickets a partir de localidad
    public Ticket generarTicketSimpleParaLocalidad(Localidad loc) {
        Ticket t = new TicketSimple(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
        repo.addTicket(t);
        return t;
    }

    public Ticket generarTicketNumeradoParaLocalidad(Localidad loc, String asiento) {
        Ticket t = new TicketNumerado(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal, asiento);
        repo.addTicket(t);
        return t;
    }

    public TicketMultiple generarTicketMultiple(Localidad loc, int cantidad, double precioPaquete) {
        TicketMultiple paquete = new TicketMultiple(precioPaquete, porcentajeServicioGlobal, cuotaFijaGlobal);
        for (int i=0;i<cantidad;i++) {
            TicketSimple t = new TicketSimple(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
            paquete.addElemento(t);
            repo.addTicket(t);
        }
        repo.addTicket(paquete);
        return paquete;
    }

    public TicketDeluxe generarTicketDeluxe(Localidad loc) {
        TicketDeluxe td = new TicketDeluxe(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
        repo.addTicket(td);
        return td;
    }

    // compra
    public Compra comprarTicket(Usuario comprador, Ticket t) {
        if (comprador instanceof Administrador) throw new IllegalStateException("Administrador no puede comprar");
        if (t.getEstado() != TicketEstado.DISPONIBLE) throw new IllegalStateException("Ticket no disponible");
        double monto = t.precioFinal();
        // si comprador tiene saldo preferimos descontar de su saldo
        if (comprador.getSaldo() >= monto) {
            comprador.descontarSaldo(monto);
        } else {
            // pago externo
        }
        t.venderA(comprador);
        Compra c = new Compra(comprador, monto);
        repo.addTransaccion(c);
        return c;
    }

    public void transferirTicket(Ticket t, Usuario actual, String password, Usuario nuevo) {
        t.transferirA(nuevo, actual, password);
    }

    // cancelar evento y reembolsos
    public List<Reembolso> cancelarEventoYReembolsar(Evento e, Administrador admin) {
        if (!admin.getLogin().equals(admin.getLogin())) {
            
        }
        e.cancelar();
        List<Reembolso> reembolsos = new ArrayList<>();
        // buscar tickets 
        for (Object obj : repo.getTickets()) {
            if (!(obj instanceof Ticket)) continue;
            Ticket t = (Ticket) obj;
            if (t.getPropietario() != null) {
                Usuario u = t.getPropietario();
                double montoRembolso = t.getPrecioBase(); 
                
                montoRembolso = t.getPrecioBase() + t.getPrecioBase()*t.getPorcentajeServicio();
                montoRembolso = montoRembolso; 
                u.depositarSaldo(montoRembolso);
                Reembolso r = new Reembolso(u, montoRembolso);
                repo.addTransaccion(r);
                reembolsos.add(r);
                t.estado = TicketEstado.CANCELADO;
            }
        }
        return reembolsos;
    }

    //reporte
    public void reporteFinancieroPorOrganizador(Organizador org) {
        double ingresos = 0;
        int vendidos = 0;
        for (Object obj : repo.getTransacciones()) {
            if (obj instanceof Compra) {
                Compra c = (Compra) obj;
                if (c.getUsuario() instanceof Comprador) {
                    ingresos += c.getMontoTotal();
                    vendidos++;
                }
            }
        }
        System.out.println("Reporte organizador " + org.getNombre() + ": vendidos=" + vendidos + ", ingresos=" + ingresos);
    }

    public SimpleRepository getRepo() { return repo; }

    public void setCuotaFijaGlobal(double cuotaFijaGlobal) { this.cuotaFijaGlobal = cuotaFijaGlobal; }
    public void setPorcentajeServicioGlobal(double porcentajeServicioGlobal) { this.porcentajeServicioGlobal = porcentajeServicioGlobal; }
}

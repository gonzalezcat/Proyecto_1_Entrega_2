package boletamaster.app;

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
    public void registrarVenue(Venue v) { repo.addVenue(v); }
    public void registrarEvento(Evento e) { repo.addEvento(e); }

    // crear tickets a partir de localidad y asociarlos al evento
    public Ticket generarTicketSimpleParaLocalidad(Evento evento, Localidad loc) {
        Ticket t = new TicketSimple(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
        t.setEvento(evento);
        evento.addTicket(t);
        repo.addTicket(t);
        return t;
    }

    public Ticket generarTicketNumeradoParaLocalidad(Evento evento, Localidad loc, String asiento) {
        Ticket t = new TicketNumerado(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal, asiento);
        t.setEvento(evento);
        evento.addTicket(t);
        repo.addTicket(t);
        return t;
    }

    public TicketMultiple generarTicketMultiple(Evento evento, Localidad loc, int cantidad, double precioPaquete) {
        TicketMultiple paquete = new TicketMultiple(precioPaquete, porcentajeServicioGlobal, cuotaFijaGlobal);
        paquete.setEvento(evento);
        for (int i=0;i<cantidad;i++) {
            TicketSimple t = new TicketSimple(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
            t.setEvento(evento);
            paquete.addElemento(t);
            evento.addTicket(t); // elementos también asociados
            repo.addTicket(t);
        }
        evento.addTicket(paquete); // agrega el paquete
        repo.addTicket(paquete);
        return paquete;
    }

    public TicketDeluxe generarTicketDeluxe(Evento evento, Localidad loc) {
        TicketDeluxe td = new TicketDeluxe(loc.getPrecioBase(), porcentajeServicioGlobal, cuotaFijaGlobal);
        td.setEvento(evento);
        evento.addTicket(td);
        repo.addTicket(td);
        return td;
    }

    // compra
    public Compra comprarTicket(Usuario comprador, Ticket t) {
        if (comprador instanceof Administrador) throw new IllegalStateException("Administrador no puede comprar");
        if (t.ticketVencido()) throw new IllegalStateException("Ticket vencido, no se puede comprar");
        if (t.getEvento() != null && t.getEvento().isCancelado()) throw new IllegalStateException("Evento cancelado");
        if (t.getEstado() != TicketEstado.DISPONIBLE) throw new IllegalStateException("Ticket no disponible");

        double monto = t.precioFinal();

        // si comprador tiene saldo preferimos descontar de su saldo
        if (comprador.getSaldo() >= monto) {
            comprador.descontarSaldo(monto);
        } else {
            // pago externo: asumimos aprobado
        }

        // Si es paquete multiple, marcar también los elementos como vendidos y con mismo propietario
        if (t instanceof TicketMultiple) {
            TicketMultiple paquete = (TicketMultiple) t;
            paquete.venderA(comprador); // cambia propietario y estado del paquete
            for (Ticket elem : paquete.getElementos()) {
                elem.propietario = comprador;
                elem.setEstado(TicketEstado.VENDIDO);
            }
        } else {
            t.venderA(comprador);
        }

        Compra c = new Compra(comprador, monto);
        repo.addTransaccion(c);
        return c;
    }

    public void transferirTicket(Ticket t, Usuario actual, String password, Usuario nuevo) {
        if (t.ticketVencido()) throw new IllegalStateException("Ticket vencido, no puede transferirse");
        if (t.getEvento() != null && t.getEvento().isCancelado()) throw new IllegalStateException("Evento cancelado");
        t.transferirA(nuevo, actual, password);
    }

    // cancelar evento y reembolsos (administrador)
    public List<Reembolso> cancelarEventoYReembolsar(Evento evento, Administrador admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Solo el administrador puede cancelar eventos");
        }

        // marcar el evento como cancelado
        evento.cancelar();

        List<Reembolso> reembolsos = new ArrayList<>();

        // buscar tickets vendidos y reembolsar (solo tickets del evento)
        for (Object obj : repo.getTickets()) {
            if (!(obj instanceof Ticket)) continue;

            Ticket t = (Ticket) obj;

            // Solo tickets del evento cancelado
            if (t.getEvento() == null || !t.getEvento().getId().equals(evento.getId())) continue;

            // Solo reembolsar si fue vendido o transferido
            if (t.getEstado() == TicketEstado.VENDIDO || t.getEstado() == TicketEstado.TRANSFERIDO) {
                Usuario propietario = t.getPropietario();
                if (propietario != null) {
                    // Regla: reembolso completo excepto cuota fija
                    double montoReembolso = t.getPrecioBase() + (t.getPrecioBase() * t.getPorcentajeServicio());
                    propietario.depositarSaldo(montoReembolso);

                    // registrar la transa
                    Reembolso r = new Reembolso(propietario, montoReembolso);
                    repo.addTransaccion(r);
                    reembolsos.add(r);

                    // marcar ticket como cancelado
                    t.setEstado(TicketEstado.CANCELADO);
                }
            }
        }

        return reembolsos;
    }

    // reporte
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

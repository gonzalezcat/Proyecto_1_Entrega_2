package boletamaster.marketplace;

import java.util.*;
import boletamaster.app.Sistema;
import boletamaster.tiquetes.*;
import boletamaster.usuarios.*;
import boletamaster.transacciones.*;

public class Marketplace {
    private final Sistema sistema;
    private final List<Oferta> ofertas;
    private final List<LogRegistro> log;

    public Marketplace(Sistema sistema) {
        this.sistema = sistema;
        this.ofertas = new ArrayList<>();
        this.log = new ArrayList<>();
    }

    public List<Oferta> getOfertas() { return ofertas; }
    public List<LogRegistro> getLog() { return log; }

    private void registrarLog(String desc) {
        LogRegistro r = new LogRegistro(desc);
        log.add(r);
        // luego persistiremos automáticamente el log
    }

    public Ticket buscarTicketPorId(String id) {
        for (Object o : sistema.getRepo().getTickets()) {
            if (!(o instanceof Ticket)) continue;
            Ticket t = (Ticket) o;
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    public Oferta publicarOferta(String ticketId, Usuario vendedor, double precio) {
        Ticket ticket = buscarTicketPorId(ticketId);
        if (ticket == null) throw new IllegalArgumentException("Ticket no existe");
        if (ticket.getPropietario() == null || !ticket.getPropietario().getLogin().equals(vendedor.getLogin()))
            throw new IllegalStateException("No es propietario del ticket");
        if (ticket.ticketVencido()) throw new IllegalStateException("Ticket vencido");
        if (ticket.getEstado() != TicketEstado.VENDIDO && ticket.getEstado() != TicketEstado.TRANSFERIDO)
            throw new IllegalStateException("Ticket no válido para reventa");

        Oferta of = new Oferta(ticket, vendedor, precio);
        ofertas.add(of);
        registrarLog("PUBLICÓ OFERTA " + of.getId() + " de " + vendedor.getLogin() + " ticket=" + ticket.getId());
        return of;
    }

    public void cancelarOferta(String id, Usuario vendedor) {
        Oferta o = getOfertaActiva(id);
        if (!o.getVendedor().getLogin().equals(vendedor.getLogin()))
            throw new IllegalStateException("Solo el vendedor puede cancelarla");
        o.setActiva(false);
        registrarLog("CANCELÓ OFERTA " + id + " por " + vendedor.getLogin());
    }

    public void comprarOferta(String id, Usuario comprador) {
        Oferta o = getOfertaActiva(id);
        Ticket t = o.getTicket();

        if (t.ticketVencido()) throw new IllegalStateException("Ticket vencido");
        if (comprador.getLogin().equals(o.getVendedor().getLogin()))
            throw new IllegalStateException("No puede comprar su propia oferta");

        double precio = o.getPrecioPublico();
        if (comprador.getSaldo() < precio)
            throw new IllegalStateException("Saldo insuficiente");

        comprador.descontarSaldo(precio);
        o.getVendedor().depositarSaldo(precio);
        t.setEstado(TicketEstado.TRANSFERIDO);
        t.propietario = comprador;

        o.setActiva(false);
        sistema.getRepo().addTransaccion(new Compra(comprador, precio));
        registrarLog("COMPRA OFERTA " + id + " comprador=" + comprador.getLogin() + " vendedor=" + o.getVendedor().getLogin());
    }

    public void contraOfertar(String id, Usuario comprador, double precio) {
        Oferta o = getOfertaActiva(id);
        o.agregarContraOferta(new Oferta.ContraOferta(comprador, precio));
        registrarLog("CONTRAOFERTA en " + id + " por " + comprador.getLogin() + " precio=" + precio);
    }

    public void aceptarContraOferta(String id, Usuario vendedor, int indice) {
        Oferta o = getOfertaActiva(id);
        if (!o.getVendedor().getLogin().equals(vendedor.getLogin()))
            throw new IllegalStateException("Solo el vendedor puede aceptar");

        Oferta.ContraOferta co = o.getContraOfertas().get(indice);
        Usuario comprador = co.getComprador();
        double precio = co.getPrecio();

        comprador.descontarSaldo(precio);
        vendedor.depositarSaldo(precio);

        o.getTicket().setEstado(TicketEstado.TRANSFERIDO);
        o.getTicket().propietario = comprador;
        o.setActiva(false);

        sistema.getRepo().addTransaccion(new Compra(comprador, precio));
        registrarLog("ACEPTÓ CONTRAOFERTA " + id + " comprador=" + comprador.getLogin() + " vendedor=" + vendedor.getLogin());
    }

    private Oferta getOfertaActiva(String id) {
        for (Oferta o : ofertas)
            if (o.getId().equals(id) && o.isActiva())
                return o;
        throw new IllegalArgumentException("Oferta no activa o inexistente");
    }

    public List<Oferta> listarOfertasActivas() {
        List<Oferta> r = new ArrayList<>();
        for (Oferta o : ofertas) if (o.isActiva()) r.add(o);
        return r;
    }
}


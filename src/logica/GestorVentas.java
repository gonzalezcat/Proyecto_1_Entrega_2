package logica;

import boletamaster.app.Sistema;
import boletamaster.marketplace.Oferta;
import boletamaster.tiquetes.*;
import boletamaster.transacciones.Compra;
import boletamaster.usuarios.Comprador;

import java.util.List;
import java.util.Optional;

/**
 * GestorVentas: procesa la compra y transferencia de tickets,
 * verificando restricciones y aplicando ofertas cuando existan.
 */
public class GestorVentas {

    private final GestorFinanzas gestorFinanzas;
    private final GestorOfertas gestorOfertas;
    private final Sistema sistema;

    public GestorVentas(Sistema sistema, GestorFinanzas gestorFinanzas, GestorOfertas gestorOfertas) {
        if (sistema == null) throw new IllegalArgumentException("Sistema requerido");
        if (gestorFinanzas == null) throw new IllegalArgumentException("GestorFinanzas requerido");
        if (gestorOfertas == null) throw new IllegalArgumentException("GestorOfertas requerido");
        this.sistema = sistema;
        this.gestorFinanzas = gestorFinanzas;
        this.gestorOfertas = gestorOfertas;
    }

    
    public Compra procesarCompra(Comprador comprador, List<Ticket> tickets, boolean usarSaldo) {
        if (comprador == null) throw new IllegalArgumentException("Comprador nulo");
        if (tickets == null || tickets.isEmpty()) throw new IllegalArgumentException("Lista de tickets vacía");

        verificarRestricciones(tickets);

        double montoTotal = 0.0;
        for (Ticket t : tickets) {
            Optional<Oferta> of = gestorOfertas.obtenerOfertaVigente(t);
            montoTotal += of.map(Oferta::getPrecioPublico).orElse(t.precioFinal());
        }

        if (usarSaldo) {
            if (comprador.getSaldo() < montoTotal)
                throw new IllegalStateException("Saldo insuficiente");
            comprador.descontarSaldo(montoTotal);
        }

        for (Ticket t : tickets) {
            t.venderA(comprador);
        }

        Compra compra = new Compra(comprador, montoTotal);
        gestorFinanzas.agregarTransaccion(compra);

        return compra;
    }

    private void verificarRestricciones(List<Ticket> tickets) {
        final int maxTickets = 10;
        if (tickets.size() > maxTickets)
            throw new IllegalStateException("Máximo " + maxTickets + " tickets por transacción");

        for (Ticket t : tickets) {
            if (t.getEstado() != TicketEstado.DISPONIBLE)
                throw new IllegalStateException("Ticket no disponible: " + t.getId());
            if (t.ticketVencido())
                throw new IllegalStateException("Ticket vencido: " + t.getId());
        }
    }

    public void transferirTicket(Ticket ticket,
                                 boletamaster.usuarios.Usuario actual,
                                 String password,
                                 boletamaster.usuarios.Usuario nuevo) {
        sistema.transferirTicket(ticket, actual, password, nuevo);
    }
}

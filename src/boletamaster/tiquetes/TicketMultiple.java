package boletamaster.tiquetes;

import java.util.ArrayList;
import java.util.List;

public class TicketMultiple extends Ticket {
    private final List<Ticket> elementos; // tickets que componen el paquete
    private boolean paqueteTransferido; // si se ha transferido el paquete

    public TicketMultiple(double precioBase, double porcentajeServicio, double cuotaFija) {
        super(precioBase, porcentajeServicio, cuotaFija);
        this.elementos = new ArrayList<>();
        this.paqueteTransferido = false;
    }

    public void addElemento(Ticket t) {
        elementos.add(t);
    }

    public List<Ticket> getElementos() { return elementos; }

    @Override
    public boolean esTransferible() {
        // transferible solo si ninguno de sus elementos ha sido transferido y todos sin usar
        if (paqueteTransferido) return false;
        for (Ticket t : elementos) {
            if (t.getEstado() != TicketEstado.VENDIDO && t.getEstado() != TicketEstado.DISPONIBLE) return false;
            // if already transferred individually -> not allowed
            if (t.getEstado() == TicketEstado.TRANSFERIDO) return false;
        }
        return true;
    }

    @Override
    public void transferirA(boletamaster.usuarios.Usuario nuevoPropietario, boletamaster.usuarios.Usuario actual, String passwordDelActual) {
        super.transferirA(nuevoPropietario, actual, passwordDelActual);
        // propagar alos elementos
        for (Ticket t : elementos) {
            t.propietario = nuevoPropietario;
            t.estado = TicketEstado.TRANSFERIDO;
        }
        this.paqueteTransferido = true;
    }
}

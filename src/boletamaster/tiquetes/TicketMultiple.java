package boletamaster.tiquetes;

import java.util.ArrayList;
import java.util.List;
import boletamaster.usuarios.Usuario;

public class TicketMultiple extends Ticket {
    private final List<Ticket> elementos; // tickets que componen el paquete
    private boolean paqueteTransferido; // si se ha transferido el paquete

    public TicketMultiple(double precioBase, double porcentajeServicio, double cuotaFija) {
        super(precioBase, porcentajeServicio, cuotaFija);
        this.elementos = new ArrayList<>();
        this.paqueteTransferido = false;
    }

    public void addElemento(Ticket t) {
        if (t == null) throw new IllegalArgumentException("Elemento nulo");
        elementos.add(t);
    }

    public List<Ticket> getElementos() { return elementos; }

    @Override
    public boolean esTransferible() {
        // transferible si solo si ninguno de sus elementos ha sido transferido y todos sin usar
        if (paqueteTransferido) return false;
        for (Ticket t : elementos) {
            if (t.getEstado() == TicketEstado.TRANSFERIDO) return false;
            if (t.getEstado() == TicketEstado.USADO) return false;
        }
        return true;
    }

    @Override
    public void transferirA(Usuario nuevoPropietario, Usuario actual, String passwordDelActual) {
        super.transferirA(nuevoPropietario, actual, passwordDelActual);
       
        for (Ticket t : elementos) {
            t.propietario = nuevoPropietario;
            t.setEstado(TicketEstado.TRANSFERIDO);
        }
        this.paqueteTransferido = true;
    }

    /**
     *cantidad ind. q hay en el paquete
     */
    public int ticketsMultiples() {
        return elementos.size();
    }
}

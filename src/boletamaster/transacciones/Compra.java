package boletamaster.transacciones;

import boletamaster.usuarios.Usuario;
import boletamaster.tiquetes.Ticket;
import java.util.List;
import java.util.ArrayList;

public class Compra extends Transaccion {
    private final List<Ticket> tickets;  // ✅ NUEVO: Asociación con tickets comprados

    public Compra(Usuario usuario, double montoTotal, List<Ticket> tickets) {
        super(usuario, montoTotal);
        this.tickets = new ArrayList<>(tickets);
    }

    public List<Ticket> getTickets() { 
        return new ArrayList<>(tickets); 
    }

    @Override
    public String toString() {
        return "Compra{" + getId() + ", usuario=" + usuario.getLogin() + 
               ", monto=" + montoTotal + ", tickets=" + tickets.size() + "}";
    }
}
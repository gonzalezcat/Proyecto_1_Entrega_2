package boletamaster.transacciones;

import boletamaster.usuarios.Usuario;
import boletamaster.tiquetes.Ticket;
import java.util.List;
import java.util.ArrayList;

public class Compra extends Transaccion {
    private final List<Ticket> tickets;  

    // ✅ Constructor completo (tu versión original)
    public Compra(Usuario usuario, double montoTotal, List<Ticket> tickets) {
        super(usuario, montoTotal);
        this.tickets = new ArrayList<>(tickets);
    }

    // ✅ Nuevo constructor simple (para Marketplace o ventas directas)
    public Compra(Usuario usuario, double montoTotal) {
        super(usuario, montoTotal);
        this.tickets = new ArrayList<>();
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

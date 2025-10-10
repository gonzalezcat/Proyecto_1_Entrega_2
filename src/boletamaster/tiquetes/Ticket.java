package boletamaster.tiquetes;

import java.util.UUID;
import boletamaster.usuarios.Usuario;

public abstract class Ticket {
    protected final String id;
    protected final double precioBase;
    protected final double porcentajeServicio;
    protected final double cuotaFija;
    protected TicketEstado estado;
    protected Usuario propietario; // null si no vendido

    public Ticket(double precioBase, double porcentajeServicio, double cuotaFija) {
        this.id = UUID.randomUUID().toString();
        this.precioBase = precioBase;
        this.porcentajeServicio = porcentajeServicio;
        this.cuotaFija = cuotaFija;
        this.estado = TicketEstado.DISPONIBLE;
    }

    public String getId() { return id; }
    public double getPrecioBase() { return precioBase; }
    public double getPorcentajeServicio() { return porcentajeServicio; }
    public double getCuotaFija() { return cuotaFija; }
    public TicketEstado getEstado() { return estado; }
    public Usuario getPropietario() { return propietario; }

    public double precioFinal() {
        return precioBase + precioBase * porcentajeServicio + cuotaFija;
    }

    public void venderA(Usuario u) {
        if (estado != TicketEstado.DISPONIBLE) throw new IllegalStateException("Ticket no disponible para venta");
        this.propietario = u;
        this.estado = TicketEstado.VENDIDO;
    }

    public abstract boolean esTransferible();

    public void transferirA(Usuario nuevoPropietario, Usuario actual, String passwordDelActual) {
        if (!esTransferible()) throw new IllegalStateException("Ticket no transferible");
        if (propietario == null) throw new IllegalStateException("Ticket sin propietario");
        if (!propietario.getLogin().equals(actual.getLogin())) throw new IllegalStateException("Solo propietario puede transferir");
        if (!actual.checkPassword(passwordDelActual)) throw new IllegalArgumentException("Contraseña incorrecta");
        this.propietario = nuevoPropietario;
        this.estado = TicketEstado.TRANSFERIDO;
    }

    @Override
    public String toString() {
        return "Ticket{" + id + ", precioBase=" + precioBase + ", estado=" + estado + ", propietario=" + (propietario!=null?propietario.getLogin():"-") + "}";
    }
}

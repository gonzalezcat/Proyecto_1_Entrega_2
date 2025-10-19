package boletamaster.tiquetes;

import java.time.LocalDateTime;
import boletamaster.eventos.Evento;
import boletamaster.usuarios.Usuario;

public abstract class Ticket {
    protected final String id;
    protected final double precioBase;
    protected final double porcentajeServicio;
    protected final double cuotaFija;
    protected TicketEstado estado;
    public Usuario propietario; // null si no vendido
    protected Evento evento; // referencia al evento asociado 

    public Ticket(double precioBase, double porcentajeServicio, double cuotaFija) {
        this.id = java.util.UUID.randomUUID().toString();
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

    public Evento getEvento() { return evento; }
    public void setEvento(Evento e) { this.evento = e; }

    public void setEstado(TicketEstado nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("Estado no puede ser null");
        this.estado = nuevoEstado;
    }

    public double precioFinal() {
        return precioBase + precioBase * porcentajeServicio + cuotaFija;
    }

    public void venderA(Usuario u) {
        if (ticketVencido()) throw new IllegalStateException("Ticket vencido, no se puede vender");
        if (estado != TicketEstado.DISPONIBLE) throw new IllegalStateException("Ticket no disponible para venta");
        if (u == null) throw new IllegalArgumentException("Usuario inválido");
        this.propietario = u;
        this.estado = TicketEstado.VENDIDO;
    }

    public abstract boolean esTransferible();

    public void transferirA(Usuario nuevoPropietario, Usuario actual, String passwordDelActual) {
        if (!esTransferible()) throw new IllegalStateException("Ticket no transferible");
        if (propietario == null) throw new IllegalStateException("Ticket sin propietario");
        if (!propietario.getLogin().equals(actual.getLogin())) throw new IllegalStateException("Solo propietario puede transferir");
        if (!actual.checkPassword(passwordDelActual)) throw new IllegalArgumentException("Contraseña incorrecta");
        if (ticketVencido()) throw new IllegalStateException("Ticket vencido no se puede transferir");
        this.propietario = nuevoPropietario;
        this.estado = TicketEstado.TRANSFERIDO;
    }

    /**
     * Un ticket esta vencido si el evento ya fue
     */
    public boolean ticketVencido() {
        if (evento == null) return false;
        LocalDateTime ahora = LocalDateTime.now();
        return evento.getFechaHora().isBefore(ahora);
    }

    @Override
    public String toString() {
        return "Ticket{" + id + ", precioBase=" + precioBase + ", estado=" + estado + ", propietario=" + (propietario!=null?propietario.getLogin():"-") + "}";
    }
}

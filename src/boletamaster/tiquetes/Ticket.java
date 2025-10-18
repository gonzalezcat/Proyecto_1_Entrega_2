package boletamaster.tiquetes;

import java.util.UUID;
import boletamaster.usuarios.Usuario;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;
import boletamaster.eventos.Oferta;

public abstract class Ticket {
    protected final String id;
    protected final Evento evento;           // ✅ NUEVO: Asociación con evento
    protected final Localidad localidad;
    protected final double precioBase;
    protected final double porcentajeServicio;
    protected final double cuotaFija;
    protected TicketEstado estado;
    protected Usuario propietario; // null si no vendido

    public Ticket(Evento evento, Localidad localidad, double precioBase, double porcentajeServicio, double cuotaFija) {
        this.id = UUID.randomUUID().toString();
        this.evento = evento;           // ✅ INICIALIZAR evento
        this.localidad = localidad;
        this.precioBase = precioBase;
        this.porcentajeServicio = porcentajeServicio;
        this.cuotaFija = cuotaFija;
        this.estado = TicketEstado.DISPONIBLE;
    }

    public String getId() { return id; }
    public Evento getEvento() { return evento; }
    public Localidad getLocalidad() { return localidad; }
    public double getPrecioBase() { return precioBase; }
    public double getPorcentajeServicio() { return porcentajeServicio; }
    public double getCuotaFija() { return cuotaFija; }
    public TicketEstado getEstado() { return estado; }
    public Usuario getPropietario() { return propietario; }

    public double precioFinal() {
        double precioConServicio = precioBase + precioBase * porcentajeServicio + cuotaFija;
        return precioConServicio;
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
    public double precioConDescuento(Oferta oferta) {
        if (oferta != null && oferta.estaVigente() && 
            oferta.getLocalidad().equals(this.localidad)) {
            return oferta.aplicarDescuento(precioFinal());
        }
        return precioFinal();
    }

    @Override
    public String toString() {
        return "Ticket{" + id + ", precioBase=" + precioBase + ", estado=" + estado + ", propietario=" + (propietario!=null?propietario.getLogin():"-") + "}";
    }
}

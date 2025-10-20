package boletamaster.transacciones;

import java.time.LocalDateTime;
import java.util.UUID;
import boletamaster.usuarios.Usuario;

public abstract class Transaccion {
    protected final String id;
    protected final LocalDateTime fecha;
    protected final Usuario usuario;
    protected final double montoTotal;

    public Transaccion(Usuario usuario, double montoTotal) {
        this.id = UUID.randomUUID().toString();
        this.fecha = LocalDateTime.now();
        this.usuario = usuario;
        this.montoTotal = montoTotal;
    }

    public String getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getUsuario() { return usuario; }
    public double getMontoTotal() { return montoTotal; }

    @Override
    public String toString() {
        return "Transaccion{" + id + ", fecha=" + fecha + ", usuario=" + usuario.getLogin() + ", monto=" + montoTotal + "}";
    }
}

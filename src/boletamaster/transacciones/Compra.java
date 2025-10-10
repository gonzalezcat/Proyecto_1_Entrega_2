package boletamaster.transacciones;

import boletamaster.usuarios.Usuario;

public class Compra extends Transaccion {
    public Compra(Usuario usuario, double montoTotal) {
        super(usuario, montoTotal);
    }

    @Override
    public String toString() {
        return "Compra{" + getId() + ", usuario=" + usuario.getLogin() + ", monto=" + montoTotal + "}";
    }
}
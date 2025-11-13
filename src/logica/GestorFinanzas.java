package logica;

import boletamaster.app.Sistema;

public class GestorFinanzas {
    private final Sistema sistema;

    public GestorFinanzas(Sistema sistema) {
        if (sistema == null) throw new IllegalArgumentException("Sistema requerido");
        this.sistema = sistema;
    }

    public void agregarTransaccion(Object transaccion) {
        sistema.getRepo().addTransaccion(transaccion);
    }
}

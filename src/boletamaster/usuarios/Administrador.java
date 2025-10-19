package boletamaster.usuarios;

import boletamaster.app.Sistema;
import boletamaster.tiquetes.Ticket;
import boletamaster.tiquetes.TicketMultiple;
import boletamaster.transacciones.Reembolso;
import java.util.List;

public class Administrador extends Usuario {
    public Administrador(String login, String password, String nombre) {
        super(login, password, nombre);
    }

    //autoriza reembolso 
    public Reembolso reembolso(Ticket t, Sistema sistema) {
        if (t == null) throw new IllegalArgumentException("Ticket nulo");
        if (sistema == null) throw new IllegalArgumentException("Sistema nulo");
        if (t.getPropietario() == null) throw new IllegalStateException("Ticket no tiene propietario para reembolsar");

        // Monto: precioBase + porcentaje de servicio (excluye cuota fija)
        double montoReembolso = t.getPrecioBase() + (t.getPrecioBase() * t.getPorcentajeServicio());

        // depositar al propietario
        t.getPropietario().depositarSaldo(montoReembolso);

        Reembolso r = new Reembolso(t.getPropietario(), montoReembolso);
        sistema.getRepo().addTransaccion(r);

        t.setEstado(boletamaster.tiquetes.TicketEstado.CANCELADO);

        return r;
    }

    //ventas totales + ganancias plataforma 
    public double[] gananciasGenerales(Sistema sistema) {
        if (sistema == null) throw new IllegalArgumentException("Sistema nulo");

        double ventasTotales = 0.0;
        double gananciasPlataforma = 0.0;

        List<Object> tickets = sistema.getRepo().getTickets();
        for (Object o : tickets) {
            if (!(o instanceof Ticket)) continue;
            Ticket t = (Ticket) o;

         
            if (t.getEstado() == boletamaster.tiquetes.TicketEstado.VENDIDO || t.getEstado() == boletamaster.tiquetes.TicketEstado.TRANSFERIDO) {
                if (t instanceof TicketMultiple) {
                    ventasTotales += t.getPrecioBase();
                    gananciasPlataforma += t.getPrecioBase() * t.getPorcentajeServicio() + t.getCuotaFija();
                } else {
                    ventasTotales += t.getPrecioBase();
                    gananciasPlataforma += t.getPrecioBase() * t.getPorcentajeServicio() + t.getCuotaFija();
                }
            }
        }

        return new double[] { ventasTotales, gananciasPlataforma };
    }

    @Override
    public String toString() {
        return "Administrador{" + "login=" + login + ", nombre=" + nombre + '}';
    }
}

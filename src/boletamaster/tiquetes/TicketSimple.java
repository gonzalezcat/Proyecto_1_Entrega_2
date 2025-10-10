package boletamaster.tiquetes;

public class TicketSimple extends Ticket {
    public TicketSimple(double precioBase, double porcentajeServicio, double cuotaFija) {
        super(precioBase, porcentajeServicio, cuotaFija);
    }

    @Override
    public boolean esTransferible() {
        return true;
    }
}

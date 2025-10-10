package boletamaster.tiquetes;

public class TicketDeluxe extends Ticket {
    public TicketDeluxe(double precioBase, double porcentajeServicio, double cuotaFija) {
        super(precioBase, porcentajeServicio, cuotaFija);
    }

    @Override
    public boolean esTransferible() {
        return false; 
    }
}


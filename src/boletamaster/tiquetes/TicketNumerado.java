package boletamaster.tiquetes;

public class TicketNumerado extends Ticket {
    private final String asiento; //asiento
    public TicketNumerado(double precioBase, double porcentajeServicio, double cuotaFija, String asiento) {
        super(precioBase, porcentajeServicio, cuotaFija);
        this.asiento = asiento;
    }

    public String getAsiento() { return asiento; }

    @Override
    public boolean esTransferible() {
        return true;
    }

    @Override
    public String toString() {
        return "TicketNumerado{" + "asiento=" + asiento + ", id=" + id + ", estado=" + estado + "}";
    }
}

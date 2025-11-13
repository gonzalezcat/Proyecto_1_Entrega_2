package boletamaster.tiquetes;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;

public class TicketSimple extends Ticket {
    public TicketSimple(Evento evento, Localidad localidad,double precioBase, double porcentajeServicio, double cuotaFija) {
        super(evento, localidad,precioBase, porcentajeServicio, cuotaFija);
    }
    public TicketSimple(Localidad localidad) {
        super(null, localidad, 0.0, 0.0, 0.0); // valores por defecto
    }

    @Override
    public boolean esTransferible() {
        return true;
    }
}
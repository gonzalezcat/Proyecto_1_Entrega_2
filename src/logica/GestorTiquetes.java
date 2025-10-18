package logica;
import boletamaster.tiquetes.*;
import boletamaster.eventos.*;
import java.util.ArrayList;
import java.util.List;

public class GestorTiquetes {
    
    public TicketSimple crearTicketSimple(Evento evento, Localidad localidad, 
                                         double porcentajeServicio, double cuotaFija) {
        return new TicketSimple(evento, localidad, localidad.getPrecioBase(), 
                               porcentajeServicio, cuotaFija);
    }
    
    public TicketNumerado crearTicketNumerado(Evento evento, Localidad localidad, 
                                             double porcentajeServicio, double cuotaFija, 
                                             String asiento) {
        if (!localidad.isNumerada()) {
            throw new IllegalArgumentException("La localidad no es numerada");
        }
        return new TicketNumerado(evento, localidad, localidad.getPrecioBase(), 
                                 porcentajeServicio, cuotaFija, asiento);
    }
    
    public TicketDeluxe crearTicketDeluxe(Evento evento, Localidad localidad, 
                                         double precioPaquete, double porcentajeServicio, 
                                         double cuotaFija) {
        return new TicketDeluxe(evento, localidad, precioPaquete, 
                               porcentajeServicio, cuotaFija);
    }
    
    public TicketMultiple crearTicketMultiple(Evento evento, Localidad localidad, 
                                             List<Ticket> tickets, double precioPaquete, 
                                             double porcentajeServicio, double cuotaFija) {
        TicketMultiple ticketMultiple = new TicketMultiple(evento, localidad, precioPaquete, 
                                                          porcentajeServicio, cuotaFija);
        for (Ticket ticket : tickets) {
            ticketMultiple.addElemento(ticket);
        }
        return ticketMultiple;
    }
    
    public List<Ticket> generarTicketsParaLocalidad(Evento evento, Localidad localidad, 
                                                   int cantidad, double porcentajeServicio, 
                                                   double cuotaFija) {
        List<Ticket> tickets = new ArrayList<>();
        
        if (localidad.isNumerada()) {
            // Generar tickets numerados
            for (int i = 0; i < Math.min(cantidad, localidad.getAsientosNumerados().size()); i++) {
                String asiento = localidad.getAsientosNumerados().get(i);
                tickets.add(crearTicketNumerado(evento, localidad, porcentajeServicio, 
                                               cuotaFija, asiento));
            }
        } else {
            // Generar tickets simples
            for (int i = 0; i < cantidad; i++) {
                tickets.add(crearTicketSimple(evento, localidad, porcentajeServicio, cuotaFija));
            }
        }
        
        return tickets;
    }
}
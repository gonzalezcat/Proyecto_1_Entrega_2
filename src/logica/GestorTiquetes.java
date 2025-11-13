package logica;

import boletamaster.app.Sistema;
import boletamaster.eventos.Evento;
import boletamaster.eventos.Localidad;
import boletamaster.tiquetes.*;
import java.util.ArrayList;
import java.util.List;

public class GestorTiquetes {

    private final Sistema sistema;

    public GestorTiquetes(Sistema sistema) {
        if (sistema == null) throw new IllegalArgumentException("Sistema requerido");
        this.sistema = sistema;
    }

    public TicketSimple crearTicketSimple(Localidad localidad) {
        Ticket t = sistema.generarTicketSimple(localidad);
        return (TicketSimple) t;
    }

    public TicketNumerado crearTicketNumerado(Localidad localidad, String asiento) {
        Ticket t = sistema.generarTicketNumerado(localidad, asiento);
        return (TicketNumerado) t;
    }

    public TicketDeluxe crearTicketDeluxe(Localidad localidad) {
        Ticket t = sistema.generarTicketDeluxe(localidad);
        return (TicketDeluxe) t;
    }

    public TicketMultiple crearTicketMultiple(Localidad localidad, int cantidad, double precioPaquete) {
        TicketMultiple tm = sistema.generarTicketMultiple(localidad, cantidad, precioPaquete);
        return tm;
    }

    public List<Ticket> generarTicketsParaLocalidad(Evento evento, Localidad localidad, int cantidad) {
        List<Ticket> lista = new ArrayList<>();
        if (localidad.isNumerada()) {
            List<String> asientos = localidad.getAsientosNumerados();
            int limite = Math.min(cantidad, asientos.size());
            for (int i = 0; i < limite; i++) {
                lista.add(crearTicketNumerado(localidad, asientos.get(i)));
            }
        } else {
            for (int i = 0; i < cantidad; i++) {
                lista.add(crearTicketSimple(localidad));
            }
        }
        return lista;
    }
}


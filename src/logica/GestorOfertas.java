package logica;

import boletamaster.marketplace.Oferta;
import boletamaster.tiquetes.Ticket;
import java.util.Optional;


public interface GestorOfertas {

    
    Optional<Oferta> obtenerOfertaVigente(Ticket ticket);
}

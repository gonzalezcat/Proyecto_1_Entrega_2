package logica;

import boletamaster.marketplace.Marketplace;
import boletamaster.marketplace.Oferta;
import boletamaster.tiquetes.Ticket;

import java.util.List;
import java.util.Optional;


public class GestorOfertasImpl implements GestorOfertas {

    private final Marketplace marketplace;

    public GestorOfertasImpl(Marketplace marketplace) {
        if (marketplace == null)
            throw new IllegalArgumentException("Marketplace no puede ser nulo");
        this.marketplace = marketplace;
    }

    @Override
    public Optional<Oferta> obtenerOfertaVigente(Ticket ticket) {
        if (ticket == null) return Optional.empty();

        List<Oferta> activas = marketplace.listarOfertasActivas();
        for (Oferta o : activas) {
            // Verifica coincidencia de ticket y estado activo
            if (o.getTicket() != null
                    && o.getTicket().getId().equals(ticket.getId())
                    && o.isActiva()) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }
}

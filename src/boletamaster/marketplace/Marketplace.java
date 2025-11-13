package boletamaster.marketplace;

import java.util.*;
import boletamaster.app.Sistema;
import boletamaster.eventos.Oferta;
import boletamaster.eventos.Localidad;
import boletamaster.tiquetes.*;
import boletamaster.usuarios.*;
import boletamaster.transacciones.*;

public class Marketplace {
    private final Sistema sistema;
    private final List<Oferta> ofertas;
    private final List<LogRegistro> log;

    public Marketplace(Sistema sistema) {
        this.sistema = sistema;
        this.ofertas = new ArrayList<>();
        this.log = new ArrayList<>();
    }

    public List<Oferta> getOfertas() { return ofertas; }
    public List<LogRegistro> getLog() { return log; }

    private void registrarLog(String desc) {
        LogRegistro r = new LogRegistro(desc);
        log.add(r);
        sistema.getRepo().addLog(r);
    }

    public Oferta publicarOferta(Localidad loc, double porcentajeDescuento, 
                                 java.time.LocalDateTime inicio, java.time.LocalDateTime fin) {
        if (loc == null) throw new IllegalArgumentException("Localidad nula");
        if (porcentajeDescuento <= 0 || porcentajeDescuento >= 1)
            throw new IllegalArgumentException("Porcentaje inválido");

        Oferta oferta = new Oferta(loc, porcentajeDescuento, inicio, fin);
        ofertas.add(oferta);
        sistema.getRepo().addOferta(oferta);
        registrarLog("PUBLICÓ OFERTA " + oferta.getId() + " en " + loc.getNombre());
        return oferta;
    }

    public void cancelarOferta(String id) {
        Oferta o = getOfertaActiva(id);
        o.setActiva(false);
        sistema.getRepo().addOferta(o);
        registrarLog("CANCELÓ OFERTA " + id);
    }

    public List<Oferta> listarOfertasVigentes() {
        List<Oferta> vigentes = new ArrayList<>();
        for (Oferta o : ofertas)
            if (o.estaVigente()) vigentes.add(o);
        return vigentes;
    }

    private Oferta getOfertaActiva(String id) {
        for (Oferta o : ofertas)
            if (o.getId().equals(id) && o.isActiva())
                return o;
        throw new IllegalArgumentException("Oferta no activa o inexistente");
    }
}


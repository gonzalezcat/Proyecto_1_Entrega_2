package logica;

import boletamaster.eventos.Oferta;
import boletamaster.eventos.Localidad;
import boletamaster.usuarios.Organizador;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import boletamaster.eventos.*;

public class GestorOfertas {
    private List<Oferta> ofertas;
    
    public GestorOfertas() {
        this.ofertas = new ArrayList<>();
    }
    
    // ✅ Crear una nueva oferta
    public Oferta crearOferta(Organizador organizador, Localidad localidad, 
                             double porcentajeDescuento, LocalDateTime inicio, 
                             LocalDateTime fin) {
        
        // Validaciones
        if (porcentajeDescuento <= 0 || porcentajeDescuento > 0.5) { // Máximo 50% de descuento
            throw new IllegalArgumentException("El descuento debe estar entre 1% y 50%");
        }
        
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
        
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La oferta no puede empezar en el pasado");
        }
        
        // Verificar que no hay ofertas superpuestas para la misma localidad
        if (existeOfertaSuperpuesta(localidad, inicio, fin)) {
            throw new IllegalStateException("Ya existe una oferta para esta localidad en ese período");
        }
        
        Oferta nuevaOferta = new Oferta(localidad, porcentajeDescuento, inicio, fin);
        ofertas.add(nuevaOferta);
        
        return nuevaOferta;
    }
    
    // ✅ Verificar ofertas superpuestas
    private boolean existeOfertaSuperpuesta(Localidad localidad, LocalDateTime inicio, LocalDateTime fin) {
        return ofertas.stream()
                .filter(o -> o.getLocalidad().equals(localidad))
                .filter(Oferta::isActiva)
                .anyMatch(o -> seSuperponen(o.getInicio(), o.getFin(), inicio, fin));
    }
    
    private boolean seSuperponen(LocalDateTime inicio1, LocalDateTime fin1, 
                                LocalDateTime inicio2, LocalDateTime fin2) {
        return inicio1.isBefore(fin2) && fin1.isAfter(inicio2);
    }
    
    // ✅ Obtener oferta vigente para una localidad
    public Oferta obtenerOfertaVigente(Localidad localidad) {
        return ofertas.stream()
                .filter(o -> o.getLocalidad().equals(localidad))
                .filter(Oferta::estaVigente)
                .findFirst()
                .orElse(null);
    }
    
    // ✅ Obtener todas las ofertas vigentes
    public List<Oferta> obtenerOfertasVigentes() {
        return ofertas.stream()
                .filter(Oferta::estaVigente)
                .collect(Collectors.toList());
    }
    
    // ✅ Desactivar oferta
    public void desactivarOferta(String idOferta) {
        ofertas.stream()
                .filter(o -> o.getId().equals(idOferta))
                .findFirst()
                .ifPresent(o -> o.setActiva(false));
    }
    
    // ✅ Obtener ofertas por organizador (a través de las localidades de sus eventos)
    public List<Oferta> obtenerOfertasPorOrganizador(Organizador organizador, List<Evento> eventos) {
        return ofertas.stream()
                .filter(o -> perteneceAOrganizador(o.getLocalidad(), organizador, eventos))
                .collect(Collectors.toList());
    }
    
    private boolean perteneceAOrganizador(Localidad localidad, Organizador organizador, List<Evento> eventos) {
        return eventos.stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .anyMatch(e -> e.getLocalidades().contains(localidad));
    }
    
    public List<Oferta> getOfertas() {
        return new ArrayList<>(ofertas);
    }
}

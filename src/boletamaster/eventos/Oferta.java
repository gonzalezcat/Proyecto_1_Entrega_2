package boletamaster.eventos;

import java.time.LocalDateTime;

public class Oferta {
    private final String id;
    private final Localidad localidad;
    private final double porcentajeDescuento;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private boolean activa;
    
    public Oferta(Localidad localidad, double porcentajeDescuento, 
                  LocalDateTime inicio, LocalDateTime fin) {
        this.id = "OFR-" + System.currentTimeMillis();
        this.localidad = localidad;
        this.porcentajeDescuento = porcentajeDescuento;
        this.inicio = inicio;
        this.fin = fin;
        this.activa = true;
    }
    
    // ✅ Verificar si la oferta está vigente
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return activa && ahora.isAfter(inicio) && ahora.isBefore(fin);
    }
    
    // ✅ Aplicar descuento a un precio
    public double aplicarDescuento(double precioOriginal) {
        if (!estaVigente()) {
            return precioOriginal; // No aplicar descuento si no está vigente
        }
        return precioOriginal * (1 - porcentajeDescuento);
    }
    
    // Getters
    public String getId() { return id; }
    public Localidad getLocalidad() { return localidad; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    
    @Override
    public String toString() {
        return "Oferta{" + id + ", localidad=" + localidad.getNombre() + 
               ", descuento=" + (porcentajeDescuento * 100) + "%, " +
               "vigente=" + estaVigente() + "}";
    }
}
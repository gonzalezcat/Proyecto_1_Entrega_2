package logica;

import boletamaster.eventos.TipoEvento;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionSistema {
    private double cuotaFijaGlobal;
    private Map<TipoEvento, Double> porcentajesServicio;
    
    public ConfiguracionSistema() {
        this.cuotaFijaGlobal = 5.0;
        this.porcentajesServicio = new HashMap<>();
        inicializarPorcentajesPorDefecto();
    }
    
    private void inicializarPorcentajesPorDefecto() {
        porcentajesServicio.put(TipoEvento.MUSICAL, 0.15);
        porcentajesServicio.put(TipoEvento.CULTURAL, 0.10);
        porcentajesServicio.put(TipoEvento.DEPORTIVO, 0.12);
        porcentajesServicio.put(TipoEvento.RELIGIOSO, 0.08);
    }
    
    public double getCuotaFijaGlobal() { return cuotaFijaGlobal; }
    public void setCuotaFijaGlobal(double cuota) { this.cuotaFijaGlobal = cuota; }
    
    public double getPorcentajeServicio(TipoEvento tipo) {
        return porcentajesServicio.getOrDefault(tipo, 0.10);
    }
    
    public void setPorcentajeServicio(TipoEvento tipo, double porcentaje) {
        porcentajesServicio.put(tipo, porcentaje);
    }
}
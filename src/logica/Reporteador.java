package logica;

import boletamaster.usuarios.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Reporteador {
    private GestorFinanzas gestorFinanzas;
    
    public Reporteador(GestorFinanzas gestorFinanzas) {
        this.gestorFinanzas = gestorFinanzas;
    }
    
    // Reportes para organizadores - retorna Map
    public Map<String, Object> generarReporteOrganizador(Organizador org, LocalDateTime inicio, 
                                                       LocalDateTime fin) {
        
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("organizador", org.getNombre());
        reporte.put("periodo", inicio + " a " + fin);
        reporte.put("ganancias", gestorFinanzas.calcularGananciasOrganizador(org, inicio, fin));
        reporte.put("tipo", "ORGANIZADOR");
        
        return reporte;
    }
    
    // Reportes para administrador - retorna Map
    public Map<String, Object> generarReporteAdministrador(LocalDateTime inicio, 
                                                          LocalDateTime fin, 
                                                          String filtro) {
        
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("tipo", "ADMINISTRADOR");
        reporte.put("filtro", filtro);
        reporte.put("periodo", inicio + " a " + fin);
        reporte.put("gananciasPlataforma", gestorFinanzas.calcularGananciasPlataforma(inicio, fin));
        reporte.put("cuotaFijaGlobal", gestorFinanzas.getCuotaFijaGlobal());
        
        return reporte;
    }
    
    // Método auxiliar para generar reporte en texto
    public String generarReporteTexto(Map<String, Object> reporte) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE ===\n");
        reporte.forEach((key, value) -> {
            sb.append(key).append(": ").append(value).append("\n");
        });
        return sb.toString();
    }
}
package logica;

import boletamaster.usuarios.*;
import boletamaster.transacciones.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class GestorFinanzas {
    private List<Transaccion> transacciones;
    private double cuotaFijaGlobal;
    
    public GestorFinanzas() {
        this.transacciones = new ArrayList<>();
        this.cuotaFijaGlobal = 5.0; // Valor por defecto
    }
    
    // Para administrador: configurar tarifas
    public void configurarTarifas(double cuotaFija, double porcentajeBase) {
        this.cuotaFijaGlobal = cuotaFija;
        // Aquí podrías guardar porcentajes por tipo de evento
    }
    
    // Calcular ganancias para organizador
    public double calcularGananciasOrganizador(Organizador organizador, LocalDateTime inicio, LocalDateTime fin) {
        return transacciones.stream()
                .filter(t -> t instanceof Compra)
                .filter(t -> t.getFecha().isAfter(inicio) && t.getFecha().isBefore(fin))
                .map(t -> (Compra) t)
                .flatMap(compra -> compra.getTickets().stream()) // ✅ Usar tickets de la compra
                .filter(ticket -> ticket.getEvento().getOrganizador().equals(organizador)) // ✅ Filtrar por organizador
                .mapToDouble(ticket -> ticket.getPrecioBase()) // ✅ Solo precio base para organizador
                .sum();
    }
    
    // Calcular ganancias de la plataforma
    public double calcularGananciasPlataforma(LocalDateTime inicio, LocalDateTime fin) {
        return transacciones.stream()
                .filter(t -> t instanceof Compra)
                .filter(t -> t.getFecha().isAfter(inicio) && t.getFecha().isBefore(fin))
                .map(t -> (Compra) t)
                .flatMap(compra -> compra.getTickets().stream())
                .mapToDouble(ticket -> 
                    (ticket.precioFinal() - ticket.getPrecioBase()) // ✅ Comisión = precioFinal - precioBase
                )
                .sum();
    }
    
    private double calcularComisionTransaccion(Transaccion transaccion) {
        if (transaccion instanceof Compra) {
            // La plataforma se queda con los cargos de servicio
            // Esto es una simplificación - en realidad necesitarías acceso a los tickets
            return transaccion.getMontoTotal() * 0.15; // 15% de comisión ejemplo
        }
        return 0;
    }
    
    public void agregarTransaccion(Transaccion transaccion) {
        transacciones.add(transaccion);
    }
    public List<Transaccion> getTransacciones() {
        return new ArrayList<>(transacciones);
    }
    public double getCuotaFijaGlobal() {
        return cuotaFijaGlobal;
    }
}
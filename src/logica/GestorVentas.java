package logica;
import java.util.List;
import boletamaster.usuarios.*;
import boletamaster.transacciones.*;
import boletamaster.tiquetes.*;
import boletamaster.eventos.*;

public class GestorVentas {
    private GestorFinanzas gestorFinanzas;
    private GestorOfertas gestorOfertas;// ✅ NUEVO: Dependencia
    
    // ✅ CONSTRUCTOR MODIFICADO: Recibe GestorFinanzas
    public GestorVentas(GestorFinanzas gestorFinanzas, GestorOfertas gestorOfertas) {
        this.gestorFinanzas = gestorFinanzas;
        this.gestorOfertas = gestorOfertas;
    }
    
    public Compra procesarCompra(Comprador comprador, List<Ticket> tickets, 
                                boolean usarSaldo) {
        
        // 1. Verificar restricciones por transacción
        if (!verificarRestricciones(comprador, tickets)) {
            throw new IllegalStateException("No se cumplen las restricciones de compra");
        }
        
        // 2. Calcular monto total
        double montoTotal = calcularMontoTotal(tickets);
        
        // 3. Aplicar métodos de pago
        if (usarSaldo) {
            if (comprador.getSaldo() < montoTotal) {
                throw new IllegalStateException("Saldo insuficiente");
            }
            comprador.descontarSaldo(montoTotal);
        }
        // Aquí iría integración con pasarela de pago externa
        
        // 4. Actualizar inventario - asignar tickets al comprador
        for (Ticket ticket : tickets) {
            ticket.venderA(comprador);
        }
        
        // 5. Crear transacción
        Compra compra = new Compra(comprador, montoTotal,tickets);
        
        // ✅ NUEVO: Registrar transacción en el sistema financiero
        gestorFinanzas.agregarTransaccion(compra);
        
        return compra;
    }
    
    private boolean verificarRestricciones(Comprador comprador, List<Ticket> tickets) {
        // Lógica de máximos por transacción
        int maxTicketsPorTransaccion = 10;
        
        // Considerar tickets múltiples como una unidad
        int cantidadUnidades = calcularUnidadesTransaccion(tickets);
        
        if (cantidadUnidades > maxTicketsPorTransaccion) {
            return false;
        }
        
        // Verificar que todos los tickets estén disponibles
        for (Ticket ticket : tickets) {
            if (ticket.getEstado() != TicketEstado.DISPONIBLE) {
                return false;
            }
        }
        
        return true;
    }
    
    private int calcularUnidadesTransaccion(List<Ticket> tickets) {
        int unidades = 0;
        
        for (Ticket ticket : tickets) {
            if (ticket instanceof TicketMultiple) {
                // Los tickets múltiples cuentan como 1 unidad
                unidades += 1;
            } else {
                // Tickets individuales cuentan como 1 unidad
                unidades += 1;
            }
        }
        
        return unidades;
    }
    private double calcularMontoTotal(List<Ticket> tickets) {
        double total = 0.0;
        
        for (Ticket ticket : tickets) {
            Oferta ofertaVigente = gestorOfertas.obtenerOfertaVigente(ticket.getLocalidad());
            total += ticket.precioConDescuento(ofertaVigente);
        }
        
        return total;
    }
    
    
    
    // ✅ MÉTODO NUEVO: Para transferir tickets
    public void transferirTicket(Ticket ticket, Usuario actual, Usuario nuevoPropietario, String password) {
        ticket.transferirA(nuevoPropietario, actual, password);
    }
}
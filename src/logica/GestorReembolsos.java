package logica;

import boletamaster.usuarios.*;
import boletamaster.transacciones.Reembolso;
import boletamaster.tiquetes.*;

public class GestorReembolsos {
    
    public Reembolso procesarReembolsoCompleto(Usuario usuario, double monto, String motivo) {
        // Reembolso completo (menos costo emisión) - por cancelación administrativa
        usuario.depositarSaldo(monto);
        return new Reembolso(usuario, monto);
    }
    
    public Reembolso procesarReembolsoParcial(Usuario usuario, double montoBase, String motivo) {
        // Reembolso solo del precio base - por cancelación de organizador
        usuario.depositarSaldo(montoBase);
        return new Reembolso(usuario, montoBase);
    }
    
    public boolean puedeReembolsar(Ticket ticket) {
        return ticket.getEstado() != TicketEstado.USADO && 
               ticket.getEstado() != TicketEstado.CANCELADO;
    }
}
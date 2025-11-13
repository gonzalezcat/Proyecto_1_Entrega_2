package boletamaster.marketplace;

import java.time.LocalDateTime;

public class LogRegistro {
    private final LocalDateTime fechaHora;
    private final String tipoAccion; // NEW: Specifies the event type (e.g., "OFERTA_CREADA")
    private final String descripcion;

    // Updated Constructor to include the action type
    public LogRegistro(String tipoAccion, String descripcion) {
        this.fechaHora = LocalDateTime.now();
        this.tipoAccion = tipoAccion;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getTipoAccion() { return tipoAccion; } 
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return "[" + fechaHora + "] - " + tipoAccion + ": " + descripcion;
    }
}
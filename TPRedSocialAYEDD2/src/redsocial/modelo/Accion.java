package redsocial.modelo;

import java.time.LocalDateTime;

/**
 * Una accion realizada en la plataforma
 */
public class Accion {
    private TipoAccion tipo;

    private String detalles;
    private LocalDateTime fechaHora;

    public Accion(TipoAccion tipo, String detalles) {
        this.tipo = tipo;
        this.detalles = detalles;
        this.fechaHora = LocalDateTime.now();
    }

    public TipoAccion getTipo() {
        return tipo;
    }

    public String getDetalles() {
        return detalles;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}

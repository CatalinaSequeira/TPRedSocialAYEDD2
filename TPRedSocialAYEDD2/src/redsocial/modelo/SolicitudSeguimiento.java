package redsocial.modelo;

/**
 * Una solicitud de seguir a otro cliente
 */
public class SolicitudSeguimiento {
    private String solicitante;
    private String objetivo;

    public SolicitudSeguimiento(String solicitante, String objetivo) {
        this.solicitante = solicitante.trim();
        this.objetivo = objetivo.trim();
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getObjetivo() {
        return objetivo;
    }
}

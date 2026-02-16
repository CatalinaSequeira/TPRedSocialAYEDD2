package redsocial.modelo;

/**
 * Una solicitud de seguir a otro cliente
 */
public class SolicitudSeguimiento {
    private String seguidor;
    private String seguido;

    public SolicitudSeguimiento(String seguidor, String seguido) {
        this.seguidor = seguidor.trim();
        this.seguido = seguido.trim();
    }

    public String getSeguidor() {
        return seguidor;
    }

    public String getSeguido() {
        return seguido;
    }
}

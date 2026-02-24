package redsocial.modelo;

import java.util.Objects;

public class Relacion {
    private String tipoRelacion;
    private String clienteNombre;

    public Relacion(String tipoRelacion, String clienteNombre) {
        this.tipoRelacion = tipoRelacion;
        this.clienteNombre = clienteNombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relacion r)) return false;
        return tipoRelacion.equals(r.tipoRelacion) && clienteNombre.equals(r.clienteNombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoRelacion, clienteNombre);
    }

    public String getTipoRelacion() {
        return tipoRelacion;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }
}

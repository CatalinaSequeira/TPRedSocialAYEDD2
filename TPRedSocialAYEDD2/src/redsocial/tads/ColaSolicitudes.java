package redsocial.tads;

import redsocial.modelo.SolicitudSeguimiento;

/**
 * Cola de solicitudes con arreglo - procesa en orden FIFO
 */
public class ColaSolicitudes implements ColaSolicitudesTDA {
    private SolicitudSeguimiento[] elementos;
    private int indice;
    private int frente;

    public void InicializarCola() {
        elementos = new SolicitudSeguimiento[100];
        indice = 0;
        frente = 0;
    }

    public void Acolar(SolicitudSeguimiento s) {
        elementos[indice] = s;
        indice++;
    }

    public void Desacolar() {
        if (!ColaVacia()) {
            frente++;
        }
    }

    public boolean ColaVacia() {
        return (frente >= indice);
    }

    public SolicitudSeguimiento Primero() {
        return elementos[frente];
    }
}

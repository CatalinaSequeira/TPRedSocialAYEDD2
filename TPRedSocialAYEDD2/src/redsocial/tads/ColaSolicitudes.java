package redsocial.tads;

import redsocial.modelo.SolicitudSeguimiento;

/**
 * Cola de solicitudes con arreglo - procesa en orden FIFO
 */
public class ColaSolicitudes implements ColaSolicitudesTDA {
    private SolicitudSeguimiento[] elementos;
    private int indice;
    private int frente;

    public void inicializarCola() {
        elementos = new SolicitudSeguimiento[100];
        indice = 0;
        frente = 0;
    }

    public void acolar(SolicitudSeguimiento s) {
        elementos[indice] = s;
        indice++;
    }

    public void desacolar() {
        if (!colaVacia()) {
            frente++;
        }
    }

    public boolean colaVacia() {
        return (frente >= indice);
    }

    public SolicitudSeguimiento primero() {
        return elementos[frente];
    }
}

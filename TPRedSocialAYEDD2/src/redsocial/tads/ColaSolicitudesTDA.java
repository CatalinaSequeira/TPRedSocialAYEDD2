package redsocial.tads;

import redsocial.modelo.SolicitudSeguimiento;

/**
 * TAD Cola de Solicitudes - FIFO para orden de llegada
 */
public interface ColaSolicitudesTDA {
    void inicializarCola();
    void acolar(SolicitudSeguimiento s);
    void desacolar();
    boolean colaVacia();
    SolicitudSeguimiento primero();
}

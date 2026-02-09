package redsocial.tads;

import redsocial.modelo.SolicitudSeguimiento;

/**
 * TAD Cola de Solicitudes - FIFO para orden de llegada
 */
public interface ColaSolicitudesTDA {
    void InicializarCola();
    void Acolar(SolicitudSeguimiento s);
    void Desacolar();
    boolean ColaVacia();
    SolicitudSeguimiento Primero();
}

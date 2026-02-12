package redsocial.tads;

import redsocial.modelo.Accion;

/**
 * TAD Pila de Acciones - para el historial
 */
public interface PilaAccionesTDA {
  void inicializarPila();

  void apilar(Accion a);

  void desapilar();

  boolean pilaVacia();

  Accion tope();

  int cantidad();

  Accion obtenerEn(int pos);
}

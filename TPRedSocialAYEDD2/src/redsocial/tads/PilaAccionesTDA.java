package redsocial.tads;

import redsocial.modelo.Accion;

/**
 * TAD Pila de Acciones - para el historial
 */
public interface PilaAccionesTDA {
  void InicializarPila();

  void Apilar(Accion a);

  void Desapilar();

  boolean PilaVacia();

  Accion Tope();

  int Cantidad();

  Accion ObtenerEn(int pos);
}

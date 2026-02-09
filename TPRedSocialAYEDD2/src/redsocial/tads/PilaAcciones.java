package redsocial.tads;

import redsocial.modelo.Accion;

/**
 * Pila de acciones con arreglo - estilo del curso
 */
public class PilaAcciones implements PilaAccionesTDA {
  private Accion[] elementos;
  private int indice;

  public void InicializarPila() {
    elementos = new Accion[100];
    indice = 0;
  }

  public void Apilar(Accion a) {
    elementos[indice] = a;
    indice++;
  }

  public void Desapilar() {
    if (indice > 0) {
      indice--;
    }
  }

  public boolean PilaVacia() {
    return (indice == 0);
  }

  public Accion Tope() {
    return elementos[indice - 1];
  }

  public int Cantidad() {
    return indice;
  }

  public Accion ObtenerEn(int pos) {
    return elementos[pos];
  }
}

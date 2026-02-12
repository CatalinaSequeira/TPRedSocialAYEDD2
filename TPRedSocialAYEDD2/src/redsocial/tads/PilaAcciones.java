package redsocial.tads;

import redsocial.modelo.Accion;

/**
 * Pila de acciones con arreglo - estilo del curso
 */
public class PilaAcciones implements PilaAccionesTDA {
  private Accion[] elementos;
  private int indice;

  public void inicializarPila() {
    elementos = new Accion[100];
    indice = 0;
  }

  public void apilar(Accion a) {
    elementos[indice] = a;
    indice++;
  }

  public void desapilar() {
    if (indice > 0) {
      indice--;
    }
  }

  public boolean pilaVacia() {
    return (indice == 0);
  }

  public Accion tope() {
    return elementos[indice - 1];
  }

  public int cantidad() {
    return indice;
  }

  public Accion obtenerEn(int pos) {
    return elementos[pos];
  }
}

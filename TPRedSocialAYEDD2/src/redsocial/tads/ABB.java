package redsocial.tads;

import java.util.ArrayList;
import java.util.List;

public class ABB<T extends Comparable<T>> implements ABBTDA<T> {

    private Nodo<T> raiz;

    @Override
    public void insertar(T elemento) {
        raiz = insertarRec(raiz, elemento);
    }

    @Override
    public boolean estaVacio() {
        return false;
    }

    @Override
    public List<T> obtenerNivel(int nivel) {
        List<T> resultado = new ArrayList<>();
        obtenerNivelRec(raiz, 1, nivel, resultado);
        return resultado;
    }

    private Nodo<T> insertarRec(Nodo<T> nodo, T elemento) {
        if (nodo == null) {
            return new Nodo<>(elemento);
        }

        if (elemento.compareTo(nodo.elemento) < 0) {
            nodo.izq = insertarRec(nodo.izq, elemento);
        } else {
            nodo.der = insertarRec(nodo.der, elemento);
        }

        return nodo;
    }

    private void obtenerNivelRec(Nodo<T> nodo, int actual, int buscado, List<T> lista) {
        if (nodo == null) return;

        if (actual == buscado) {
            lista.add(nodo.elemento);
        } else {
            obtenerNivelRec(nodo.izq, actual + 1, buscado, lista);
            obtenerNivelRec(nodo.der, actual + 1, buscado, lista);
        }
    }

    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void imprimirRec(Nodo<T> nodo, int nivel) {
        if (nodo == null) return;

        imprimirRec(nodo.der, nivel + 1);

        for (int i = 0; i < nivel; i++) {
            System.out.print("    ");
        }
        System.out.println(nodo.elemento);

        imprimirRec(nodo.izq, nivel + 1);
    }

}
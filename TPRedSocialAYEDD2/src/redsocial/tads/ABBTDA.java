package redsocial.tads;

import java.util.List;

public interface ABBTDA<T extends Comparable<T>> {

    void insertar(T elemento);

    boolean estaVacio();

    List<T> obtenerNivel(int nivel);
}


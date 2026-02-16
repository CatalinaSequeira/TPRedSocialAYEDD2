package redsocial.tads;

class Nodo<T extends Comparable<T>> {
    T elemento;
    Nodo<T> izq;
    Nodo<T> der;

    public Nodo(T elemento) {
        this.elemento = elemento;
    }
    @Override
    public String toString() {
        return this.elemento.toString();
    }
}

package redsocial.tads;

import redsocial.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ABB implements ABBTDA {

    private NodoCliente raiz;

    @Override
    public void insertar(Cliente cliente) {
        raiz = insertarRec(raiz, cliente);
    }

    @Override
    public void eliminar(Cliente cliente) {
        raiz = eliminarRec(raiz, cliente);
    }

    @Override
    public boolean estaVacio() {
        return raiz == null;
    }

    @Override
    public List<Cliente> obtenerNivel(int nivel) {
        List<Cliente> resultado = new ArrayList<>();
        obtenerNivelRec(raiz, 1, nivel, resultado);
        return resultado;
    }

    @Override
    public List<Cliente> listarEnOrden() {
        List<Cliente> lista = new ArrayList<>();
        listarEnOrdenRec(raiz, lista);
        return lista;
    }

    @Override
    public List<Cliente> buscarPorScoring(int scoring) {
        List<Cliente> resultado = new ArrayList<>();
        buscarPorScoringRec(raiz, scoring, resultado);
        return resultado;
    }

    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void listarEnOrdenRec(NodoCliente nodo, List<Cliente> lista) {
        if (nodo == null) return;
        listarEnOrdenRec(nodo.izq, lista);
        lista.add(nodo.cliente);
        listarEnOrdenRec(nodo.der, lista);
    }

    private NodoCliente eliminarRec(NodoCliente nodo, Cliente cliente) {
        if (nodo == null) return null;
        int cmp = comparar(cliente, nodo.cliente);
        if (cmp < 0) {
            nodo.izq = eliminarRec(nodo.izq, cliente);
        } else if (cmp > 0) {
            nodo.der = eliminarRec(nodo.der, cliente);
        } else {
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;
            Cliente min = minimo(nodo.der);
            nodo.cliente = min;
            nodo.der = eliminarRec(nodo.der, min);
        }

        actualizarAltura(nodo);
        return balancear(nodo);
    }

    private Cliente minimo(NodoCliente nodo) {
        return nodo.izq == null ? nodo.cliente : minimo(nodo.izq);
    }

    private NodoCliente insertarRec(NodoCliente nodo, Cliente cliente) {
        if (nodo == null) return new NodoCliente(cliente);

        int cmp = comparar(cliente, nodo.cliente);
        if (cmp < 0) {
            nodo.izq = insertarRec(nodo.izq, cliente);
        } else if (cmp > 0) {
            nodo.der = insertarRec(nodo.der, cliente);
        } else {
            return nodo;
        }

        actualizarAltura(nodo);
        return balancear(nodo);
    }

    private int comparar(Cliente a, Cliente b) {
        int cmpScoring = Integer.compare(a.getScoring(), b.getScoring());
        if (cmpScoring != 0) {
            return cmpScoring;
        }
        return a.getNombre().compareToIgnoreCase(b.getNombre());
    }

    private int altura(NodoCliente nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private void actualizarAltura(NodoCliente nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izq), altura(nodo.der));
    }

    private int factorBalance(NodoCliente nodo) {
        return nodo == null ? 0 : altura(nodo.izq) - altura(nodo.der);
    }

    private NodoCliente balancear(NodoCliente nodo) {
        int balance = factorBalance(nodo);

        if (balance > 1) {
            if (factorBalance(nodo.izq) < 0) {
                nodo.izq = rotarIzquierda(nodo.izq);
            }
            return rotarDerecha(nodo);
        }

        if (balance < -1) {
            if (factorBalance(nodo.der) > 0) {
                nodo.der = rotarDerecha(nodo.der);
            }
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    private NodoCliente rotarDerecha(NodoCliente y) {
        NodoCliente x = y.izq;
        NodoCliente t2 = x.der;

        x.der = y;
        y.izq = t2;

        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    private NodoCliente rotarIzquierda(NodoCliente x) {
        NodoCliente y = x.der;
        NodoCliente t2 = y.izq;

        y.izq = x;
        x.der = t2;

        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    private void buscarPorScoringRec(NodoCliente nodo, int scoring, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }

        int cmp = Integer.compare(scoring, nodo.cliente.getScoring());
        if (cmp < 0) {
            buscarPorScoringRec(nodo.izq, scoring, resultado);
        } else if (cmp > 0) {
            buscarPorScoringRec(nodo.der, scoring, resultado);
        } else {
            resultado.add(nodo.cliente);
            buscarPorScoringRec(nodo.izq, scoring, resultado);
            buscarPorScoringRec(nodo.der, scoring, resultado);
        }
    }

    private void obtenerNivelRec(NodoCliente nodo, int actual, int buscado, List<Cliente> lista) {
        if (nodo == null) return;
        if (actual == buscado) {
            lista.add(nodo.cliente);
        } else {
            obtenerNivelRec(nodo.izq, actual + 1, buscado, lista);
            obtenerNivelRec(nodo.der, actual + 1, buscado, lista);
        }
    }

    private void imprimirRec(NodoCliente nodo, int nivel) {
        if (nodo == null) return;
        imprimirRec(nodo.der, nivel + 1);
        for (int i = 0; i < nivel; i++) System.out.print("    ");
        System.out.println(nodo.cliente);
        imprimirRec(nodo.izq, nivel + 1);
    }

    private static class NodoCliente {
        Cliente cliente;
        NodoCliente izq;
        NodoCliente der;
        int altura;

        NodoCliente(Cliente cliente) {
            this.cliente = cliente;
            this.altura = 1;
        }
    }
}

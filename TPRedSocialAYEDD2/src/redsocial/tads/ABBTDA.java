package redsocial.tads;

import redsocial.modelo.Cliente;

import java.util.List;

public interface ABBTDA {

    void insertar(Cliente cliente);

    void eliminar(Cliente cliente);

    boolean estaVacio();

    List<Cliente> obtenerNivel(int nivel);

    List<Cliente> listarEnOrden();

    List<Cliente> buscarPorScoring(int scoring);
}

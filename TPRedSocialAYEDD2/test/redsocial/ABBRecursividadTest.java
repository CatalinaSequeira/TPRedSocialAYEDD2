package redsocial;

import org.junit.jupiter.api.Test;
import redsocial.modelo.Cliente;
import redsocial.tads.ABB;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Pruebas enfocadas en el comportamiento recursivo del ABB.
 */
class ABBRecursividadTest {

  @Test
  void testListarEnOrdenRecorridoRecursivo() {
    ABB abb = new ABB();

    // Inserto clientes de forma que el arbol tenga rama izquierda y derecha
    abb.insertar(new Cliente("C", 20));
    abb.insertar(new Cliente("A", 10));
    abb.insertar(new Cliente("B", 15));
    abb.insertar(new Cliente("D", 30));

    List<Cliente> enOrden = abb.listarEnOrden();

    // El recorrido en orden deberia devolverlos ordenados por scoring / nombre
    assertFalse(enOrden.isEmpty());
    assertEquals(4, enOrden.size());
    assertEquals("A", enOrden.get(0).getNombre());
    assertEquals("B", enOrden.get(1).getNombre());
    assertEquals("C", enOrden.get(2).getNombre());
    assertEquals("D", enOrden.get(3).getNombre());
  }
}


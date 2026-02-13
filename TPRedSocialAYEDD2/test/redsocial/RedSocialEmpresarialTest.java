package redsocial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.modelo.TipoAccion;
import redsocial.persistencia.CargadorJSON;
import redsocial.sistema.RedSocialEmpresarial;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del sistema - iteracion 1
 */
class RedSocialEmpresarialTest {

  private RedSocialEmpresarial red;

  @BeforeEach
  void setUp() {
    red = new RedSocialEmpresarial();
  }

  @Test
  void testAgregarYBuscarPorNombre() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    Cliente alice = red.buscarPorNombre("Alice");
    assertNotNull(alice);
    assertEquals("Alice", alice.getNombre());
    assertEquals(95, alice.getScoring());

    assertNull(red.buscarPorNombre("NoExiste"));
  }

  @Test
  void testBuscarPorNombreNoImportaMayusculas() {
    red.agregarCliente("Alice", 95);
    assertNotNull(red.buscarPorNombre("alice"));
    assertNotNull(red.buscarPorNombre("ALICE"));
  }

  @Test
  void testBuscarPorScoring() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    Cliente c = red.buscarPorScoring(95);
    assertNotNull(c);
    assertEquals(95, c.getScoring());

    ArrayList<Cliente> todos95 = red.obtenerClientesConScoring(95);
    assertEquals(1, todos95.size());
  }

  @Test
  void testVariosConMismoScoring() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Charlie", 95);

    ArrayList<Cliente> lista = red.obtenerClientesConScoring(95);
    assertEquals(2, lista.size());
  }

  @Test
  void testNoPermitirDuplicados() {
    red.agregarCliente("Alice", 95);
    assertThrows(IllegalArgumentException.class, () -> red.agregarCliente("Alice", 80));
  }

  @Test
  void testHistorial() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    assertTrue(red.hayAccionesParaDeshacer());
    ArrayList<Accion> hist = red.obtenerHistorialAcciones();
    assertEquals(2, hist.size());
    assertEquals(TipoAccion.AGREGAR_CLIENTE, hist.get(0).getTipo());
  }

  @Test
  void testDeshacer() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    assertEquals(2, red.cantidadClientes());

    Accion desh = red.deshacerUltimaAccion();
    assertNotNull(desh);
    assertTrue(desh.getDetalles().contains("Bob"));
    assertEquals(1, red.cantidadClientes());

    red.deshacerUltimaAccion();
    assertEquals(0, red.cantidadClientes());
  }

  @Test
  void testDeshacerConHistorialVacio() {
    assertNull(red.deshacerUltimaAccion());
  }

  @Test
  void testSolicitudesEnOrden() {
    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.enviarSolicitudSeguimiento("Bob", "Charlie");

    assertTrue(red.haySolicitudesPendientes());

    SolicitudSeguimiento s1 = red.procesarSiguienteSolicitud();
    assertEquals("Alice", s1.getSolicitante());
    assertEquals("Bob", s1.getObjetivo());

    SolicitudSeguimiento s2 = red.procesarSiguienteSolicitud();
    assertEquals("Bob", s2.getSolicitante());

    assertFalse(red.haySolicitudesPendientes());
  }

  @Test
  void testCargaJSON() throws IOException {
    File temp = File.createTempFile("clientes", ".json");
    FileWriter fw = new FileWriter(temp);
    fw.write("{\"clientes\":[{\"nombre\":\"Test1\",\"scoring\":100,\"siguiendo\":[]},");
    fw.write("{\"nombre\":\"Test2\",\"scoring\":90,\"siguiendo\":[]}]}");
    fw.close();

    RedSocialEmpresarial sistema = CargadorJSON.CargarDesdeArchivo(temp.getAbsolutePath());
    assertEquals(2, sistema.cantidadClientes());
    assertNotNull(sistema.buscarPorNombre("Test1"));
    assertEquals(100, sistema.buscarPorNombre("Test1").getScoring());

    temp.delete();
  }

  @Test
  void testClienteInvalido() {
    assertThrows(IllegalArgumentException.class, () -> new Cliente("", 50));
    assertThrows(IllegalArgumentException.class, () -> new Cliente("Alice", -1));
  }

  @Test
  void testObtenerSeguimientos() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarCliente("Charlie", 70);

    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.enviarSolicitudSeguimiento("Bob", "Charlie");
    red.enviarSolicitudSeguimiento("Alice", "Charlie");

    red.procesarSiguienteSolicitud(); // Alice -> Bob
    red.procesarSiguienteSolicitud(); // Bob -> Charlie
    red.procesarSiguienteSolicitud(); // Alice -> Charlie

    ArrayList<SolicitudSeguimiento> seguimientos = red.obtenerSeguimientos();
    assertEquals(3, seguimientos.size());
    assertEquals("Alice", seguimientos.get(0).getSolicitante());
    assertEquals("Bob", seguimientos.get(0).getObjetivo());
    assertEquals("Bob", seguimientos.get(1).getSolicitante());
    assertEquals("Charlie", seguimientos.get(1).getObjetivo());
    assertEquals("Alice", seguimientos.get(2).getSolicitante());
    assertEquals("Charlie", seguimientos.get(2).getObjetivo());
  }

  @Test
  void testObtenerSeguidosPor() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarCliente("Charlie", 70);
    red.agregarCliente("David", 60);

    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.enviarSolicitudSeguimiento("Bob", "Charlie");
    red.enviarSolicitudSeguimiento("Alice", "Charlie");
    red.enviarSolicitudSeguimiento("David", "Alice");

    red.procesarSiguienteSolicitud(); // Alice -> Bob
    red.procesarSiguienteSolicitud(); // Bob -> Charlie
    red.procesarSiguienteSolicitud(); // Alice -> Charlie
    red.procesarSiguienteSolicitud(); // David -> Alice

    ArrayList<String> seguidosPorAlice = red.obtenerSeguidosPor("Alice");
    assertEquals(2, seguidosPorAlice.size());
    assertTrue(seguidosPorAlice.contains("Bob"));
    assertTrue(seguidosPorAlice.contains("Charlie"));

    ArrayList<String> seguidosPorBob = red.obtenerSeguidosPor("Bob");
    assertEquals(1, seguidosPorBob.size());
    assertTrue(seguidosPorBob.contains("Charlie"));

    ArrayList<String> seguidosPorDavid = red.obtenerSeguidosPor("David");
    assertEquals(1, seguidosPorDavid.size());
    assertTrue(seguidosPorDavid.contains("Alice"));

    ArrayList<String> seguidosPorNoExiste = red.obtenerSeguidosPor("NoExiste");
    assertTrue(seguidosPorNoExiste.isEmpty());

    ArrayList<String> seguidosPorNadie = red.obtenerSeguidosPor("Charlie");
    assertTrue(seguidosPorNadie.isEmpty());
  }
}

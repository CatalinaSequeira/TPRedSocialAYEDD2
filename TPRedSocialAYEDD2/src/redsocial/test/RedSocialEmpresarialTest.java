package redsocial.test;

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
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);

    Cliente alice = red.BuscarPorNombre("Alice");
    assertNotNull(alice);
    assertEquals("Alice", alice.getNombre());
    assertEquals(95, alice.getScoring());

    assertNull(red.BuscarPorNombre("NoExiste"));
  }

  @Test
  void testBuscarPorNombreNoImportaMayusculas() {
    red.AgregarCliente("Alice", 95);
    assertNotNull(red.BuscarPorNombre("alice"));
    assertNotNull(red.BuscarPorNombre("ALICE"));
  }

  @Test
  void testBuscarPorScoring() {
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);

    Cliente c = red.BuscarPorScoring(95);
    assertNotNull(c);
    assertEquals(95, c.getScoring());

    ArrayList<Cliente> todos95 = red.ObtenerClientesConScoring(95);
    assertEquals(1, todos95.size());
  }

  @Test
  void testVariosConMismoScoring() {
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Charlie", 95);

    ArrayList<Cliente> lista = red.ObtenerClientesConScoring(95);
    assertEquals(2, lista.size());
  }

  @Test
  void testNoPermitirDuplicados() {
    red.AgregarCliente("Alice", 95);
    assertThrows(IllegalArgumentException.class, () -> red.AgregarCliente("Alice", 80));
  }

  @Test
  void testHistorial() {
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);

    assertTrue(red.HayAccionesParaDeshacer());
    ArrayList<Accion> hist = red.ObtenerHistorialAcciones();
    assertEquals(2, hist.size());
    assertEquals(TipoAccion.AGREGAR_CLIENTE, hist.get(0).getTipo());
  }

  @Test
  void testDeshacer() {
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);
    assertEquals(2, red.CantidadClientes());

    Accion desh = red.DeshacerUltimaAccion();
    assertNotNull(desh);
    assertTrue(desh.getDetalles().contains("Bob"));
    assertEquals(1, red.CantidadClientes());

    red.DeshacerUltimaAccion();
    assertEquals(0, red.CantidadClientes());
  }

  @Test
  void testDeshacerConHistorialVacio() {
    assertNull(red.DeshacerUltimaAccion());
  }

  @Test
  void testSolicitudesEnOrden() {
    red.EnviarSolicitudSeguimiento("Alice", "Bob");
    red.EnviarSolicitudSeguimiento("Bob", "Charlie");

    assertTrue(red.HaySolicitudesPendientes());

    SolicitudSeguimiento s1 = red.ProcesarSiguienteSolicitud();
    assertEquals("Alice", s1.getSolicitante());
    assertEquals("Bob", s1.getObjetivo());

    SolicitudSeguimiento s2 = red.ProcesarSiguienteSolicitud();
    assertEquals("Bob", s2.getSolicitante());

    assertFalse(red.HaySolicitudesPendientes());
  }

  @Test
  void testCargaJSON() throws IOException {
    File temp = File.createTempFile("clientes", ".json");
    FileWriter fw = new FileWriter(temp);
    fw.write("{\"clientes\":[{\"nombre\":\"Test1\",\"scoring\":100,\"siguiendo\":[]},");
    fw.write("{\"nombre\":\"Test2\",\"scoring\":90,\"siguiendo\":[]}]}");
    fw.close();

    RedSocialEmpresarial sistema = CargadorJSON.CargarDesdeArchivo(temp.getAbsolutePath());
    assertEquals(2, sistema.CantidadClientes());
    assertNotNull(sistema.BuscarPorNombre("Test1"));
    assertEquals(100, sistema.BuscarPorNombre("Test1").getScoring());

    temp.delete();
  }

  @Test
  void testClienteInvalido() {
    assertThrows(IllegalArgumentException.class, () -> new Cliente("", 50));
    assertThrows(IllegalArgumentException.class, () -> new Cliente("Alice", -1));
  }

  @Test
  void testObtenerSeguimientos() {
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);
    red.AgregarCliente("Charlie", 70);

    red.EnviarSolicitudSeguimiento("Alice", "Bob");
    red.EnviarSolicitudSeguimiento("Bob", "Charlie");
    red.EnviarSolicitudSeguimiento("Alice", "Charlie");

    red.ProcesarSiguienteSolicitud(); // Alice -> Bob
    red.ProcesarSiguienteSolicitud(); // Bob -> Charlie
    red.ProcesarSiguienteSolicitud(); // Alice -> Charlie

    ArrayList<SolicitudSeguimiento> seguimientos = red.ObtenerSeguimientos();
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
    red.AgregarCliente("Alice", 95);
    red.AgregarCliente("Bob", 88);
    red.AgregarCliente("Charlie", 70);
    red.AgregarCliente("David", 60);

    red.EnviarSolicitudSeguimiento("Alice", "Bob");
    red.EnviarSolicitudSeguimiento("Bob", "Charlie");
    red.EnviarSolicitudSeguimiento("Alice", "Charlie");
    red.EnviarSolicitudSeguimiento("David", "Alice");

    red.ProcesarSiguienteSolicitud(); // Alice -> Bob
    red.ProcesarSiguienteSolicitud(); // Bob -> Charlie
    red.ProcesarSiguienteSolicitud(); // Alice -> Charlie
    red.ProcesarSiguienteSolicitud(); // David -> Alice

    ArrayList<String> seguidosPorAlice = red.ObtenerSeguidosPor("Alice");
    assertEquals(2, seguidosPorAlice.size());
    assertTrue(seguidosPorAlice.contains("Bob"));
    assertTrue(seguidosPorAlice.contains("Charlie"));

    ArrayList<String> seguidosPorBob = red.ObtenerSeguidosPor("Bob");
    assertEquals(1, seguidosPorBob.size());
    assertTrue(seguidosPorBob.contains("Charlie"));

    ArrayList<String> seguidosPorDavid = red.ObtenerSeguidosPor("David");
    assertEquals(1, seguidosPorDavid.size());
    assertTrue(seguidosPorDavid.contains("Alice"));

    ArrayList<String> seguidosPorNoExiste = red.ObtenerSeguidosPor("NoExiste");
    assertTrue(seguidosPorNoExiste.isEmpty());

    ArrayList<String> seguidosPorNadie = red.ObtenerSeguidosPor("Charlie");
    assertTrue(seguidosPorNadie.isEmpty());
  }
}

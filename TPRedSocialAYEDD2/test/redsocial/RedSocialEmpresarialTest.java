package redsocial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.Relacion;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.modelo.TipoAccion;
import redsocial.persistencia.CargadorJSON;
import redsocial.sistema.RedSocialEmpresarial;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

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

    ArrayList<Cliente> todos95 = red.buscarPorScoring(95);

    assertEquals(1, todos95.size());
    assertEquals(95, todos95.get(0).getScoring());
  }

  @Test
  void testVariosConMismoScoring() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Charlie", 95);

    ArrayList<Cliente> lista = red.buscarPorScoring(95);
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
    assertEquals(TipoAccion.AGREGAR_CLIENTE, desh.getTipo());
    assertEquals("Bob", desh.getNombreClienteAgregado());
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
    assertEquals("Alice", s1.getSeguidor());
    assertEquals("Bob", s1.getSeguido());

    SolicitudSeguimiento s2 = red.procesarSiguienteSolicitud();
    assertEquals("Bob", s2.getSeguidor());

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
    assertTrue(seguidosPorAlice.contains("bob"));
    assertTrue(seguidosPorAlice.contains("charlie"));

    ArrayList<String> seguidosPorBob = red.obtenerSeguidosPor("Bob");
    assertEquals(1, seguidosPorBob.size());
    assertTrue(seguidosPorBob.contains("charlie"));

    ArrayList<String> seguidosPorDavid = red.obtenerSeguidosPor("David");
    assertEquals(1, seguidosPorDavid.size());
    assertTrue(seguidosPorDavid.contains("alice"));

    ArrayList<String> seguidosPorNoExiste = red.obtenerSeguidosPor("NoExiste");
    assertTrue(seguidosPorNoExiste.isEmpty());

    ArrayList<String> seguidosPorNadie = red.obtenerSeguidosPor("Charlie");
    assertTrue(seguidosPorNadie.isEmpty());
  }

  @Test
  void testSeguirClienteModificaCliente() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.procesarSiguienteSolicitud();

    Cliente alice = red.buscarPorNombre("Alice");
    assertTrue(alice.getSeguidos().contains("bob"));
  }

  @Test
  void testDeshacerSeguir() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.procesarSiguienteSolicitud();

    red.deshacerUltimaAccion();

    Cliente alice = red.buscarPorNombre("Alice");
    assertFalse(alice.getSeguidos().contains("bob"));
  }

  // --- Tests de Relaciones ---

  @Test
  void testAgregarRelacionExitoso() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    boolean agregada = red.agregarRelacion("Alice", "Bob", "conexion");
    assertTrue(agregada);

    Set<Relacion> relacionesAlice = red.obtenerRelaciones("alice");
    assertFalse(relacionesAlice.isEmpty());
    Set<String> vecinosAlice = relacionesAlice.stream()
        .map(Relacion::getClienteNombre)
        .collect(Collectors.toSet());
    assertTrue(vecinosAlice.contains("bob"));
  }

  @Test
  void testAgregarRelacionDuplicadaDevuelveFalse() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    assertTrue(red.agregarRelacion("Alice", "Bob", "conexion"));
    boolean duplicada = red.agregarRelacion("Alice", "Bob", "conexion");
    assertFalse(duplicada);
  }

  @Test
  void testAgregarRelacionNombreVacioLanzaExcepcion() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("", "Bob", "conexion"));
    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("Alice", "", "conexion"));
  }

  @Test
  void testAgregarRelacionTipoVacioLanzaExcepcion() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);

    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("Alice", "Bob", ""));
    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("Alice", "Bob", "   "));
  }

  @Test
  void testAgregarRelacionClienteNoExisteLanzaExcepcion() {
    red.agregarCliente("Alice", 95);

    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("Alice", "NoExiste", "conexion"));
    assertThrows(IllegalArgumentException.class, () -> red.agregarRelacion("NoExiste", "Alice", "conexion"));
  }

  @Test
  void testObtenerRelacionesClienteSinRelaciones() {
    red.agregarCliente("Alice", 95);
    Set<Relacion> relaciones = red.obtenerRelaciones("alice");
    assertTrue(relaciones.isEmpty());
  }

  @Test
  void testCalcularDistanciaMismoCliente() {
    red.agregarCliente("Alice", 95);
    assertEquals(0, red.calcularDistancia("Alice", "Alice"));
  }

  @Test
  void testCalcularDistanciaVecinosDirectos() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarRelacion("Alice", "Bob", "conexion");

    assertEquals(1, red.calcularDistancia("Alice", "Bob"));
    assertEquals(1, red.calcularDistancia("Bob", "Alice"));
  }

  @Test
  void testCalcularDistanciaDosSaltos() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarCliente("Charlie", 70);
    red.agregarRelacion("Alice", "Bob", "conexion");
    red.agregarRelacion("Bob", "Charlie", "conexion");

    assertEquals(2, red.calcularDistancia("Alice", "Charlie"));
    assertEquals(2, red.calcularDistancia("Charlie", "Alice"));
  }

  @Test
  void testCalcularDistanciaSinCamino() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarCliente("Charlie", 70);
    red.agregarRelacion("Alice", "Bob", "conexion");
    // Charlie desconectado

    assertEquals(-1, red.calcularDistancia("Alice", "Charlie"));
    assertEquals(-1, red.calcularDistancia("Charlie", "Bob"));
  }

  @Test
  void testCalcularDistanciaClienteNoExiste() {
    red.agregarCliente("Alice", 95);
    assertEquals(-1, red.calcularDistancia("Alice", "NoExiste"));
    assertEquals(-1, red.calcularDistancia("NoExiste", "Alice"));
  }

  @Test
  void testCargaJSONConRelaciones() throws IOException {
    File temp = File.createTempFile("clientes", ".json");
    FileWriter fw = new FileWriter(temp);
    fw.write("{\"clientes\":[{\"nombre\":\"A\",\"scoring\":100,\"siguiendo\":[]},");
    fw.write("{\"nombre\":\"B\",\"scoring\":90,\"siguiendo\":[]},");
    fw.write("{\"nombre\":\"C\",\"scoring\":80,\"siguiendo\":[]}],");
    fw.write("\"relaciones\":[{\"cliente1\":\"A\",\"cliente2\":\"B\",\"tipo\":\"conexion\"},");
    fw.write("{\"cliente1\":\"B\",\"cliente2\":\"C\",\"tipo\":\"conexion\"}]}");
    fw.close();

    RedSocialEmpresarial sistema = CargadorJSON.CargarDesdeArchivo(temp.getAbsolutePath());
    assertEquals(3, sistema.cantidadClientes());

    assertEquals(1, sistema.calcularDistancia("A", "B"));
    assertEquals(2, sistema.calcularDistancia("A", "C"));
    assertEquals(1, sistema.calcularDistancia("B", "C"));

    temp.delete();
  }

  @Test
  void testCargaJSONConRelacionesDuplicadasIgnoraDuplicados() throws IOException {
    File temp = File.createTempFile("clientes", ".json");
    FileWriter fw = new FileWriter(temp);
    fw.write("{\"clientes\":[{\"nombre\":\"A\",\"scoring\":100,\"siguiendo\":[]},");
    fw.write("{\"nombre\":\"B\",\"scoring\":90,\"siguiendo\":[]}],");
    fw.write("\"relaciones\":[{\"cliente1\":\"A\",\"cliente2\":\"B\",\"tipo\":\"conexion\"},");
    fw.write("{\"cliente1\":\"A\",\"cliente2\":\"B\",\"tipo\":\"conexion\"}]}");
    fw.close();

    RedSocialEmpresarial sistema = CargadorJSON.CargarDesdeArchivo(temp.getAbsolutePath());
    assertEquals(2, sistema.cantidadClientes());
    assertEquals(1, sistema.calcularDistancia("A", "B"));

    temp.delete();
  }

  @Test
  void testContarSeguidoresReales() {
    red.agregarCliente("Alice", 95);
    red.agregarCliente("Bob", 88);
    red.agregarCliente("Charlie", 70);
    red.agregarCliente("David", 60);

    red.enviarSolicitudSeguimiento("Alice", "Bob");
    red.enviarSolicitudSeguimiento("Charlie", "Bob");
    red.enviarSolicitudSeguimiento("David", "Bob");

    red.procesarSiguienteSolicitud();
    red.procesarSiguienteSolicitud();
    red.procesarSiguienteSolicitud();

    assertEquals(3, red.contarSeguidores("Bob"));
    assertEquals(0, red.contarSeguidores("Alice"));
    assertEquals(0, red.contarSeguidores("NoExiste"));
  }

  @Test
  void testMasSeguidoEnCuartoNivelABB() {
    for (int i = 1; i <= 15; i++) {
      red.agregarCliente("C" + i, i * 10);
    }

    ArrayList<Cliente> nivel4 = new ArrayList<>(red.obtenerClientesCuartoNivelABB());
    assertFalse(nivel4.isEmpty());

    String objetivo = nivel4.get(0).getNombre();

    ArrayList<String> candidatos = new ArrayList<>(
        java.util.List.of("C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"));
    candidatos.remove(objetivo);

    String f1 = candidatos.get(0);
    String f2 = candidatos.get(1);
    String f3 = candidatos.get(2);

    red.enviarSolicitudSeguimiento(f1, objetivo);
    red.enviarSolicitudSeguimiento(f2, objetivo);
    red.enviarSolicitudSeguimiento(f3, objetivo);

    red.procesarSiguienteSolicitud();
    red.procesarSiguienteSolicitud();
    red.procesarSiguienteSolicitud();

    Cliente masSeguido = red.obtenerClienteMasSeguidoresCuartoNivelABB();
    assertNotNull(masSeguido);
    assertEquals(objetivo, masSeguido.getNombre());
    assertEquals(3, red.contarSeguidores(objetivo));
  }

}

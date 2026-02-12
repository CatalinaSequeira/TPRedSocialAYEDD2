package redsocial.sistema;

import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.modelo.TipoAccion;
import redsocial.tads.ColaSolicitudes;
import redsocial.tads.PilaAcciones;

import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de gestion de la red social empresarial.
 * Usa Pila para historial de acciones y Cola para solicitudes.
 */

//
public class RedSocialEmpresarial {
  private ArrayList<Cliente> clientes;
  private ArrayList<SolicitudSeguimiento> seguimientos;
  private PilaAcciones historial;
  private ColaSolicitudes solicitudesPendientes;

  public RedSocialEmpresarial() {
    clientes = new ArrayList<Cliente>();
    seguimientos = new ArrayList<SolicitudSeguimiento>();
    historial = new PilaAcciones();
    historial.inicializarPila();
    solicitudesPendientes = new ColaSolicitudes();
    solicitudesPendientes.inicializarCola();
  }

  public void agregarCliente(String nombre, int scoring) {
    agregarClienteInterno(nombre, scoring, true);
  }

  public void cargarClientesIniciales(List<Cliente> lista) {
    for (int i = 0; i < lista.size(); i++) {
      Cliente c = lista.get(i);
      agregarClienteInterno(c.getNombre(), c.getScoring(), false);
    }
  }

  private void agregarClienteInterno(String nombre, int scoring, boolean guardarEnHistorial) {
    if (buscarPorNombre(nombre) != null) {
      throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
    }
    Cliente nuevo = new Cliente(nombre, scoring);
    clientes.add(nuevo);
    if (guardarEnHistorial) {
      Accion acc = new Accion(TipoAccion.AGREGAR_CLIENTE, "Cliente: " + nombre + ", scoring: " + scoring);
      historial.apilar(acc);
    }
  }
  //cambiar los nombres de los metodos para que empiecen con minuscula
  //camabir a hashmap --> diccionario
  public Cliente buscarPorNombre(String nombre) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getNombre().equalsIgnoreCase(nombre)) {
        return clientes.get(i);
      }
    }
    return null;
  }

  public Cliente buscarPorScoring(int scoring) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getScoring() == scoring) {
        return clientes.get(i);
      }
    }
    return null;
  }

  public ArrayList<Cliente> obtenerClientesConScoring(int scoring) {
    ArrayList<Cliente> resultado = new ArrayList<Cliente>();
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getScoring() == scoring) {
        resultado.add(clientes.get(i));
      }
    }
    return resultado;
  }

  //cambiar la logica de solicitud de seguimiento a Cliente
  public void enviarSolicitudSeguimiento(String solicitante, String objetivo) {
    SolicitudSeguimiento sol = new SolicitudSeguimiento(solicitante, objetivo);
    solicitudesPendientes.acolar(sol);
  }

  public SolicitudSeguimiento procesarSiguienteSolicitud() {
    if (solicitudesPendientes.colaVacia()) {
      return null;
    }
    SolicitudSeguimiento sol = solicitudesPendientes.primero();
    solicitudesPendientes.desacolar();

    // Al procesar, guardamos el seguimiento confirmado
    seguimientos.add(sol);

    // Registramos en el historial
    Accion acc = new Accion(TipoAccion.SEGUIR_CLIENTE, sol.getSolicitante() + " sigue a " + sol.getObjetivo());
    registrarAccion(acc);

    return sol;
  }

  public ArrayList<SolicitudSeguimiento> obtenerSeguimientos() {
    return seguimientos;
  }

  public ArrayList<String> obtenerSeguidosPor(String nombreCliente) {
    ArrayList<String> seguidos = new ArrayList<>();
    for (SolicitudSeguimiento sol : seguimientos) {
      if (sol.getSolicitante().equalsIgnoreCase(nombreCliente)) {
        seguidos.add(sol.getObjetivo());
      }
    }
    return seguidos;
  }

  public void registrarAccion(Accion a) {
    historial.apilar(a);
  }

  public Accion deshacerUltimaAccion() {
    if (historial.pilaVacia()) {
      return null;
    }
    Accion ultima = historial.tope();
    historial.desapilar();
    // revertir el efecto segun el tipo de accion
    if (ultima.getTipo().equals(TipoAccion.AGREGAR_CLIENTE) && ultima.getDetalles() != null) {
      String det = ultima.getDetalles();
      if (det.startsWith("Cliente: ")) {
        int coma = det.indexOf(", scoring:");
        if (coma > 0) {
          String nombreCliente = det.substring(9, coma).trim();
          eliminarCliente(nombreCliente);
        }
      }
    }
    return ultima;
  }

  public boolean hayAccionesParaDeshacer() {
    return !historial.pilaVacia();
  }

  public ArrayList<Accion> obtenerHistorialAcciones() {
    ArrayList<Accion> lista = new ArrayList<Accion>();
    for (int i = 0; i < historial.cantidad(); i++) {
      lista.add(historial.obtenerEn(i));
    }
    return lista;
  }

  public void eliminarCliente(String nombre) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getNombre().equalsIgnoreCase(nombre)) {
        clientes.remove(i);
        return;
      }
    }
  }

  public int cantidadClientes() {
    return clientes.size();
  }

  public boolean haySolicitudesPendientes() {
    return !solicitudesPendientes.colaVacia();
  }
}

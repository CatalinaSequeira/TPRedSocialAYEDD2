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
public class RedSocialEmpresarial {
  private ArrayList<Cliente> clientes;
  private ArrayList<SolicitudSeguimiento> seguimientos;
  private PilaAcciones historial;
  private ColaSolicitudes solicitudesPendientes;

  public RedSocialEmpresarial() {
    clientes = new ArrayList<Cliente>();
    seguimientos = new ArrayList<SolicitudSeguimiento>();
    historial = new PilaAcciones();
    historial.InicializarPila();
    solicitudesPendientes = new ColaSolicitudes();
    solicitudesPendientes.InicializarCola();
  }

  public void AgregarCliente(String nombre, int scoring) {
    AgregarClienteInterno(nombre, scoring, true);
  }

  public void CargarClientesIniciales(List<Cliente> lista) {
    for (int i = 0; i < lista.size(); i++) {
      Cliente c = lista.get(i);
      AgregarClienteInterno(c.getNombre(), c.getScoring(), false);
    }
  }

  private void AgregarClienteInterno(String nombre, int scoring, boolean guardarEnHistorial) {
    if (BuscarPorNombre(nombre) != null) {
      throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
    }
    Cliente nuevo = new Cliente(nombre, scoring);
    clientes.add(nuevo);
    if (guardarEnHistorial) {
      Accion acc = new Accion(TipoAccion.AGREGAR_CLIENTE, "Cliente: " + nombre + ", scoring: " + scoring);
      historial.Apilar(acc);
    }
  }

  //chequear si es por separado o todo junto
  public Cliente BuscarPorNombre(String nombre) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getNombre().equalsIgnoreCase(nombre)) {
        return clientes.get(i);
      }
    }
    return null;
  }

  // la busqueda por scoring podria devolver mas de un cliente? ya que no es un numero unico por cliente
  public Cliente BuscarPorScoring(int scoring) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getScoring() == scoring) {
        return clientes.get(i);
      }
    }
    return null;
  }

  public ArrayList<Cliente> ObtenerClientesConScoring(int scoring) {
    ArrayList<Cliente> resultado = new ArrayList<Cliente>();
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getScoring() == scoring) {
        resultado.add(clientes.get(i));
      }
    }
    return resultado;
  }

  public void EnviarSolicitudSeguimiento(String solicitante, String objetivo) {
    SolicitudSeguimiento sol = new SolicitudSeguimiento(solicitante, objetivo);
    solicitudesPendientes.Acolar(sol);
  }

  public SolicitudSeguimiento ProcesarSiguienteSolicitud() {
    if (solicitudesPendientes.ColaVacia()) {
      return null;
    }
    SolicitudSeguimiento sol = solicitudesPendientes.Primero();
    solicitudesPendientes.Desacolar();

    // Al procesar, guardamos el seguimiento confirmado
    seguimientos.add(sol);

    // Registramos en el historial
    Accion acc = new Accion(TipoAccion.SEGUIR_CLIENTE, sol.getSolicitante() + " sigue a " + sol.getObjetivo());
    RegistrarAccion(acc);

    return sol;
  }

  public ArrayList<SolicitudSeguimiento> ObtenerSeguimientos() {
    return seguimientos;
  }

  public ArrayList<String> ObtenerSeguidosPor(String nombreCliente) {
    ArrayList<String> seguidos = new ArrayList<>();
    for (SolicitudSeguimiento sol : seguimientos) {
      if (sol.getSolicitante().equalsIgnoreCase(nombreCliente)) {
        seguidos.add(sol.getObjetivo());
      }
    }
    return seguidos;
  }

  public void RegistrarAccion(Accion a) {
    historial.Apilar(a);
  }

  public Accion DeshacerUltimaAccion() {
    if (historial.PilaVacia()) {
      return null;
    }
    Accion ultima = historial.Tope();
    historial.Desapilar();
    // revertir el efecto segun el tipo de accion
    if (ultima.getTipo().equals(TipoAccion.AGREGAR_CLIENTE) && ultima.getDetalles() != null) {
      String det = ultima.getDetalles();
      if (det.startsWith("Cliente: ")) {
        int coma = det.indexOf(", scoring:");
        if (coma > 0) {
          String nombreCliente = det.substring(9, coma).trim();
          EliminarCliente(nombreCliente);
        }
      }
    }
    return ultima;
  }

  public boolean HayAccionesParaDeshacer() {
    return !historial.PilaVacia();
  }

  public ArrayList<Accion> ObtenerHistorialAcciones() {
    ArrayList<Accion> lista = new ArrayList<Accion>();
    for (int i = 0; i < historial.Cantidad(); i++) {
      lista.add(historial.ObtenerEn(i));
    }
    return lista;
  }

  public void EliminarCliente(String nombre) {
    for (int i = 0; i < clientes.size(); i++) {
      if (clientes.get(i).getNombre().equalsIgnoreCase(nombre)) {
        clientes.remove(i);
        return;
      }
    }
  }

  public int CantidadClientes() {
    return clientes.size();
  }

  public boolean HaySolicitudesPendientes() {
    return !solicitudesPendientes.ColaVacia();
  }
}

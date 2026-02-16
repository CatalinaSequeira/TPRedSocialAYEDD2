package redsocial.sistema;

import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.modelo.TipoAccion;
import redsocial.tads.ColaSolicitudes;
import redsocial.tads.PilaAcciones;

import java.util.*;

public class RedSocialEmpresarial {

  //cambie private ArrayList<Cliente> clientes por dos diccionarias diferentes para almacenar por nombre y por scoring
  private Map<String, Cliente> clientesPorNombre;
  private TreeMap<Integer, ArrayList<Cliente>> clientesPorScoring;

  private PilaAcciones historial;
  private ColaSolicitudes solicitudesPendientes;

  public RedSocialEmpresarial() {

    //cambie clientes = new ArrayList<Cliente>(); por la inicializacion de las listas atributo que almacenan los clientes por nombre y por scoring
    clientesPorNombre = new HashMap<>();
    clientesPorScoring = new TreeMap<>();

    historial = new PilaAcciones();
    historial.inicializarPila();
    solicitudesPendientes = new ColaSolicitudes();
    solicitudesPendientes.inicializarCola();
  }

  public void agregarCliente(String nombre, int scoring) {
    //aca llama a crearYRegistrarCliente que devuelve el cliente que crea y registra, lo guardamos en la variable porque es buena practica pero por ahora no hacemos nada con eso
    Cliente nuevo = crearYRegistrarCliente(nombre, scoring);
    //agrega al historial la accion de agregar cliente y guarda el nombre del cliente agregado en el objeto accion creado
    Accion acc = new Accion(nombre);
    historial.apilar(acc);
    }


  //le cambie el nombre antes era agregarClientesIniciales
  public void cargarClientesJson(List<Cliente> lista) {
    for (Cliente c : lista) {
      crearYRegistrarCliente(c.getNombre(), c.getScoring());
    }
  }

  //le cambie el nombre antes era agregrarClienteInterno
  private Cliente crearYRegistrarCliente(String nombre, int scoring) {
    String key = nombre.toLowerCase();
    //aca en vez de llamar a buscar cliente, pregunta a la lista de clientes por nombre si existe esa llave, entonces es mejor la complejidad de la busqueda (creo)
    if (clientesPorNombre.containsKey(key)) {
      throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
    }
    //crea al cliente y lo guarda tanto por nombre como por scoring en las dos listas diferentes
    Cliente nuevo = new Cliente(nombre, scoring);
    clientesPorNombre.put(key, nuevo);
    clientesPorScoring
            .computeIfAbsent(scoring, k -> new ArrayList<>())
            .add(nuevo);

    return nuevo;
  }

  //lo busca por nombre en el diccionario que almacena a los clientes por nombre
  public Cliente buscarPorNombre(String nombre) {
    return clientesPorNombre.get(nombre.toLowerCase());
  }

  //eliminamos buscarClientePorScoring, dejamos solo obtener clientes con Scoring que devuelte un alista de clientes ya que puede haber mas de uno con ese scoring
  public ArrayList<Cliente> buscarPorScoring(int scoring) {
    return clientesPorScoring.getOrDefault(scoring, new ArrayList<>());
  }

  //eliminar cliente ahora elimina al cliente tanto del diccionario por nombre tanto como del diccionario por scoring
  public void eliminarCliente(String nombre) {
    Cliente c = clientesPorNombre.remove(nombre.toLowerCase());
    if (c != null) {
      ArrayList<Cliente> lista = clientesPorScoring.get(c.getScoring());
      if (lista != null) {
        lista.remove(c);
        if (lista.isEmpty()) {
          clientesPorScoring.remove(c.getScoring());
        }
      }
    }
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
    SolicitudSeguimiento solicitud = solicitudesPendientes.primero();
    solicitudesPendientes.desacolar();

    Cliente seguidor = buscarPorNombre(solicitud.getSeguidor());
    Cliente seguido = buscarPorNombre(solicitud.getSeguido());
    if (seguidor != null && seguido != null) {
      seguidor.seguir(seguido.getNombre());
    }

    Accion acc = new Accion(solicitud.getSeguidor(), solicitud.getSeguido());
    historial.apilar(acc);

    return solicitud;
  }



  public ArrayList<String> obtenerSeguidosPor(String nombreCliente) {
    Cliente c = buscarPorNombre(nombreCliente);
    if (c == null) return new ArrayList<>();
    return new ArrayList<>(c.getSeguidos());
  }


  public boolean haySolicitudesPendientes() {
    return !solicitudesPendientes.colaVacia();
  }











  public Accion deshacerUltimaAccion() {
    if (historial.pilaVacia()) {
      return null;
    }
    Accion ultima = historial.tope();
    historial.desapilar();

    if (ultima.getTipo() == TipoAccion.AGREGAR_CLIENTE) {
      eliminarCliente(ultima.getNombreClienteAgregado());
    }
    if (ultima.getTipo() == TipoAccion.SEGUIR_CLIENTE) {
      Cliente c = buscarPorNombre(ultima.getSeguidor());
      if (c != null) {
        c.dejarDeSeguir(ultima.getSeguido());
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




  //aca tambien, devuelte el tamaño de clientes por nombre
  public int cantidadClientes() {
    return clientesPorNombre.size();
  }

  public Collection<Cliente> obtenerTodosLosClientes() {
    return clientesPorNombre.values();
  }



}

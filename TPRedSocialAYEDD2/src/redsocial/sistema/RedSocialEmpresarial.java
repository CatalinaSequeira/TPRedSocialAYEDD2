package redsocial.sistema;

import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.Relacion;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.modelo.TipoAccion;
import redsocial.tads.ABB;
import redsocial.tads.ColaSolicitudes;
import redsocial.tads.PilaAcciones;

import java.util.*;

public class RedSocialEmpresarial {

  //cambie private ArrayList<Cliente> clientes por dos diccionarias diferentes para almacenar por nombre y por scoring
  private Map<String, Cliente> clientesPorNombre;

  // Sería por cada cliente, un HashSet de los clientes que sigue.
  // HashSet para que no se repitan los clientes que sigue.
  // Relacion es el tipo de relación y el nombre del cliente que tiene la relación.
  private Map<String, HashSet<Relacion>> clientesRelacionados;

  private ABB abbClientesPorScoring;

  private PilaAcciones historial;
  private ColaSolicitudes solicitudesPendientes;

  public RedSocialEmpresarial() {

    //cambie clientes = new ArrayList<Cliente>(); por la inicializacion de las listas atributo que almacenan los clientes por nombre y por scoring
    clientesPorNombre = new HashMap<>();
    abbClientesPorScoring = new ABB();
    clientesRelacionados = new HashMap<>();

    historial = new PilaAcciones();
    historial.inicializarPila();
    solicitudesPendientes = new ColaSolicitudes();
    solicitudesPendientes.inicializarCola();
  }

  /** true si se agrego, false si ya existia */
  public boolean agregarRelacion(String nombreCliente1, String nombreCliente2, String tipoRelacion){
    // Validaciones
    if (nombreCliente1 == null || nombreCliente1.isBlank()) {
      throw new IllegalArgumentException("El nombre del primer cliente no puede ser vacio");
    }
    if (nombreCliente2 == null || nombreCliente2.isBlank()) {
      throw new IllegalArgumentException("El nombre del segundo cliente no puede ser vacio");
    }
    if (tipoRelacion == null || tipoRelacion.isBlank()) {
      throw new IllegalArgumentException("El tipo de relacion no puede ser vacio");
    }

    String cliente1Key = nombreCliente1.trim().toLowerCase();
    String cliente2Key = nombreCliente2.trim().toLowerCase();

    // Valido que los clientes existan
    if (!clientesPorNombre.containsKey(cliente1Key) || !clientesPorNombre.containsKey(cliente2Key)) {
      throw new IllegalArgumentException("Uno o ambos clientes no se encuentran registrados");
    }

    Relacion rel1 = new Relacion(tipoRelacion, cliente2Key);
    Relacion rel2 = new Relacion(tipoRelacion, cliente1Key);

    HashSet<Relacion> set1 = clientesRelacionados.computeIfAbsent(cliente1Key, k -> new HashSet<>());
    if (set1.contains(rel1)) {
      return false;
    }

    set1.add(rel1);
    clientesRelacionados.computeIfAbsent(cliente2Key, k -> new HashSet<>()).add(rel2);
    return true;
  }

  // O(1) Gracias al acceso directo al hashmap
  public Set<Relacion> obtenerRelaciones(String nombreCliente){
    // Obtengo las relaciones dado el nombre de un cliente
    HashSet<Relacion> relaciones = clientesRelacionados.get(nombreCliente);
    // Valido que tenga relaciones
    if (relaciones == null){
      return new HashSet<>();
    }
    // devuelvo las relaciones
    return relaciones;
  }

  public int calcularDistancia(String nombreClienteOrigen, String nombreClienteDestino){
      String keyOrigen = nombreClienteOrigen.toLowerCase();
      String keyDestino = nombreClienteDestino.toLowerCase();
      // Caso base
      if(keyOrigen.equals(keyDestino)){
        return 0;
      }
      // Validar que keyOrigen exista 
      if (!clientesPorNombre.containsKey(keyOrigen) || !clientesPorNombre.containsKey(keyDestino)) {
        return -1;
      }
      // BFS (Breadth First Search)
      Queue<String> cola = new LinkedList<>();
      Map<String, Integer> distancia = new HashMap<>(); // cliente -> cantidad de saltos
      Set<String> visitados = new HashSet<>();

      // Cola para llevar un orden de los clientes a consultar
      cola.add(keyOrigen);
      // Diccionario que guardar la cantidad de saltos por cliente
      distancia.put(keyOrigen, 0);
      // Conjunto que lleva registro de los clientes consultados
      visitados.add(keyOrigen);
      // Bucle mientras haya clientes (nodos) que consultar
      while (!cola.isEmpty()) {
        // Obtengo el nodo actual
        String actual = cola.poll();
        // Calculo la distancia actual
        int distActual = distancia.get(actual);
        // Obtengo sus vecinos (Relaciones)
        HashSet<Relacion> vecinos = clientesRelacionados.get(actual);
        if (vecinos == null)
          continue;
        // Loop por todas sus relaciones
        for (Relacion vecino : vecinos) {
          // Caso 1: Encuentro el cliente destino
          if (vecino.getClienteNombre().equals(keyDestino)) {
            return distActual + 1;
          }
          // Caso 2: No visite al cliente todavia
          if (!visitados.contains(vecino.getClienteNombre())) {
            // Lo marco visitado
            visitados.add(vecino.getClienteNombre());
            // Guardo la distancia
            distancia.put(vecino.getClienteNombre(), distActual + 1);
            // Lo agrego a la cola
            cola.add(vecino.getClienteNombre());
          }
        }
      }

      // Consulte todas las relaciones y no existe relación entre clientes
      return -1; 
    }

  public void agregarCliente(String nombre, int scoring) {
    //aca llama a crearYRegistrarCliente que devuelve el cliente que crea y registra, lo guardamos en la variable porque es buena practica pero por ahora no hacemos nada con eso
    Cliente nuevo = crearYRegistrarCliente(nombre, scoring);

    // Inicializo el HashSet de seguidos para el nuevo cliente
    clientesRelacionados.put(nombre.toLowerCase(), new HashSet<>());

    //agrega al historial la accion de agregar cliente y guarda el nombre del cliente agregado en el objeto accion creado
    Accion acc = new Accion(nombre);
    historial.apilar(acc);
    }


  public void cargarClientesJson(List<Cliente> lista) {
    for (Cliente c : lista) {
      Cliente nuevo = crearYRegistrarCliente(c.getNombre(), c.getScoring());

      // Inicializo el HashSet de seguidos para el nuevo cliente
      clientesRelacionados.put(nuevo.getNombre().toLowerCase(), new HashSet<>());

      // Para que la lista de clientes se guarde con los seguidos que vienen del json,
      // necesitamos pasarselos nuevamente, porque adentro de crearYRegistrarCliente, se recrean
      for (String seguido : c.getSeguidos()) {
        nuevo.seguir(seguido);
      }
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
    abbClientesPorScoring.insertar(nuevo);
    return nuevo;
  }

  //lo busca por nombre en el diccionario que almacena a los clientes por nombre
  public Cliente buscarPorNombre(String nombre) {
    return clientesPorNombre.get(nombre.toLowerCase());
  }

  //eliminamos buscarClientePorScoring, dejamos solo obtener clientes con Scoring que devuelte un alista de clientes ya que puede haber mas de uno con ese scoring
  public ArrayList<Cliente> buscarPorScoring(int scoring) {
    return new ArrayList<>(abbClientesPorScoring.buscarPorScoring(scoring));
  }

  public void eliminarCliente(String nombre) {
    String key = nombre.toLowerCase();
    Cliente c = clientesPorNombre.remove(key);
    if (c != null) {
      abbClientesPorScoring.eliminar(c);
      HashSet<Relacion> relaciones = clientesRelacionados.remove(key);
      if (relaciones != null) {
        for (Relacion relacion : relaciones) {
          HashSet<Relacion> relsDelOtro = clientesRelacionados.get(relacion.getClienteNombre());
          if (relsDelOtro != null) {
            relsDelOtro.removeIf(r -> r.getClienteNombre().equals(key));
          }
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

  //Iteracion 2: o Permitir la consulta de las conexiones de un cliente (a quiénes sigue).
  public ArrayList<String> obtenerSeguidosPor(String nombreCliente) {
    Cliente c = buscarPorNombre(nombreCliente);
    if (c == null) return new ArrayList<>();
    return new ArrayList<>(c.getSeguidos());
  }

  public int contarSeguidores(String nombreCliente) {
    if (nombreCliente == null || nombreCliente.isBlank()) {
      return 0;
    }

    String objetivo = nombreCliente.toLowerCase();
    if (!clientesPorNombre.containsKey(objetivo)) {
      return 0;
    }

    int cantidad = 0;
    for (Cliente cliente : clientesPorNombre.values()) {
      if (cliente.getSeguidos().contains(objetivo)) {
        cantidad++;
      }
    }
    return cantidad;
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

  // Este metodo existe porque es requerimiento de la consigna, se disponibliza aunque no se usa
  public Set<String> obtenerSeguidosDelCliente(String cliente) {
    return buscarPorNombre(cliente).getSeguidos();
  }

  public List<Cliente> obtenerClientesCuartoNivelABB() {
    return abbClientesPorScoring.obtenerNivel(4);
  }

  public Cliente obtenerClienteMasSeguidoresCuartoNivelABB() {
    List<Cliente> nivel4 = abbClientesPorScoring.obtenerNivel(4);
    if (nivel4.isEmpty()) {
      return null;
    }

    Cliente masSeguido = nivel4.get(0);
    int maxSeguidores = contarSeguidores(masSeguido.getNombre());

    for (int i = 1; i < nivel4.size(); i++) {
      Cliente candidato = nivel4.get(i);
      int seguidoresCandidato = contarSeguidores(candidato.getNombre());
      if (seguidoresCandidato > maxSeguidores) {
        masSeguido = candidato;
        maxSeguidores = seguidoresCandidato;
      }
    }

    return masSeguido;
  }

  //aca tambien, devuelte el tamaño de clientes por nombre
  public int cantidadClientes() {
    return clientesPorNombre.size();
  }

  public Collection<Cliente> obtenerTodosLosClientes() {
    return clientesPorNombre.values();
  }

}

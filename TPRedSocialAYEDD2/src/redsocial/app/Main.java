package redsocial.app;

import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.persistencia.CargadorJSON;
import redsocial.sistema.RedSocialEmpresarial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Programa principal - menu para probar el sistema
 */
public class Main {
    public static void main(String[] args) {
        RedSocialEmpresarial red = new RedSocialEmpresarial();
        Scanner sc = new Scanner(System.in);

        // intentar cargar datos iniciales
        try {
            String ruta = "TPRedSocialAYEDD2/TPRedSocialAYEDD2/data/clientes.json";
            red = CargadorJSON.CargarDesdeArchivo(ruta);
            System.out.println("Se cargaron " + red.cantidadClientes() + " clientes desde el archivo.");
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo, empezando con lista vacia.");
        }

        int opcion;
        do {
            System.out.println("\n--- Menu Red Social Empresarial ---");
            System.out.println("1. Agregar cliente");
            System.out.println("2. Buscar cliente por nombre");
            System.out.println("3. Buscar cliente por scoring");
            System.out.println("4. Enviar solicitud de seguimiento");
            System.out.println("5. Procesar siguiente solicitud");
            System.out.println("6. Ver historial de acciones");
            System.out.println("7. Deshacer ultima accion");
            System.out.println("8. Cantidad de clientes");
            System.out.println("9. Listar seguimientos");
            System.out.println("10. Ver a quien sigue un cliente");
            System.out.println("11. Imprimir cuarto nivel de ABB");
            System.out.println("12. Agregar relacion entre clientes");
            System.out.println("13. Calcular distancia entre clientes");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Nombre del cliente: ");
                    String nom = sc.nextLine();
                    System.out.print("Scoring: ");
                    int scorr = sc.nextInt();
                    try {
                        red.agregarCliente(nom, scorr);
                        System.out.println("Cliente agregado.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Nombre a buscar: ");
                    String nombreBusq = sc.nextLine();
                    Cliente c = red.buscarPorNombre(nombreBusq);
                    if (c != null) {
                        System.out.println("Encontrado: " + c);
                    } else {
                        System.out.println("No encontrado.");
                    }
                    break;
                case 3:
                    System.out.print("Scoring a buscar: ");
                    int scoreBusq = sc.nextInt();
                    sc.nextLine();

                    ArrayList<Cliente> lista = red.buscarPorScoring(scoreBusq);

                    if (lista.isEmpty()) {
                        System.out.println("No se encontraron clientes con ese scoring.");
                    } else {
                        System.out.println("Clientes con scoring " + scoreBusq + ":");
                        for (Cliente cli : lista) {
                            System.out.println(" - " + cli);
                        }
                    }
                    break;
                case 4:
                    System.out.print("Quien envia (solicitante): ");
                    String sol = sc.nextLine();
                    System.out.print("A quien quiere seguir: ");
                    String obj = sc.nextLine();
                    red.enviarSolicitudSeguimiento(sol, obj);
                    System.out.println("Solicitud enviada.");
                    break;
                case 5:
                    if (red.haySolicitudesPendientes()) {
                        try {
                            SolicitudSeguimiento s = red.procesarSiguienteSolicitud();
                            System.out.println("Procesada: " + s.getSeguidor() + " -> " + s.getSeguido());
                        } catch (IllegalStateException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("No hay solicitudes pendientes.");
                    }
                    break;
                case 6:
                    ArrayList<Accion> hist = red.obtenerHistorialAcciones();
                    if (hist.isEmpty()) {
                        System.out.println("El historial esta vacio.");
                    } else {
                        for (int i = 0; i < hist.size(); i++) {
                            System.out.println("  " + (i + 1) + ". " + hist.get(i).getTipo() + " - " + hist.get(i).getDetalles());
                        }
                    }
                    break;
                case 7:
                    if (red.hayAccionesParaDeshacer()) {
                        Accion desh = red.deshacerUltimaAccion();
                        System.out.println("Se deshizo: " + desh.getTipo());
                    } else {
                        System.out.println("No hay acciones para deshacer.");
                    }
                    break;
                case 8:
                    System.out.println("Total clientes: " + red.cantidadClientes());
                    break;
                case 9:
                    System.out.println("Seguimientos registrados:");
                    for (Cliente cli : red.obtenerTodosLosClientes()) {
                        for (String seguido : cli.getSeguidos()) {
                            System.out.println(" - " + cli.getNombre() + " -> " + seguido);
                        }
                    }
                    break;
                case 10:
                    System.out.print("Nombre del cliente para ver a quien sigue: ");
                    String nombreSeguidor = sc.nextLine();
                    ArrayList<String> seguidos = red.obtenerSeguidosPor(nombreSeguidor);
                    if (seguidos.isEmpty()) {
                        System.out.println(nombreSeguidor + " no sigue a nadie o no existe.");
                    } else {
                        System.out.println(nombreSeguidor + " sigue a:");
                        for (String seguido : seguidos) {
                            System.out.println("  - " + seguido);
                        }
                    }
                    break;
                case 11:
                    List<Cliente> nivel4 = red.obtenerClientesCuartoNivelABB();

                    if (nivel4.isEmpty()) {
                        System.out.println("No hay clientes en el cuarto nivel del ABB.");
                    } else {
                        System.out.println("Clientes en el cuarto nivel del ABB:");
                        for (Cliente cli : nivel4) {
                            System.out.println(" - " + cli.getNombre() +
                                    " (scoring: " + cli.getScoring() +
                                    ", seguidores: " + red.contarSeguidores(cli.getNombre()) + ")");
                        }

                        Cliente masSeguido = red.obtenerClienteMasSeguidoresCuartoNivelABB();

                        System.out.println("\nEl cliente con mas seguidores en nivel 4 es: "
                                + masSeguido.getNombre() +
                                " (" + red.contarSeguidores(masSeguido.getNombre()) + " seguidores)");
                    }
                    break;
                case 12:
                    System.out.print("Nombre del primer cliente: ");
                    String rel1 = sc.nextLine();
                    System.out.print("Nombre del segundo cliente: ");
                    String rel2 = sc.nextLine();
                    System.out.print("Tipo de relacion (ej: conexion): ");
                    String tipoRel = sc.nextLine();
                    if (tipoRel.isBlank()) tipoRel = "conexion";
                    try {
                        boolean agregada = red.agregarRelacion(rel1, rel2, tipoRel);
                        if (agregada) {
                            System.out.println("Relacion agregada entre " + rel1 + " y " + rel2 + ".");
                        } else {
                            System.out.println("La relacion ya existia (duplicada).");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 13:
                    System.out.print("Cliente origen: ");
                    String origen = sc.nextLine();
                    System.out.print("Cliente destino: ");
                    String destino = sc.nextLine();
                    int distancia = red.calcularDistancia(origen, destino);
                    if (distancia == -1) {
                        System.out.println("No hay camino entre " + origen + " y " + destino + " (o alguno no existe).");
                    } else {
                        System.out.println("Distancia entre " + origen + " y " + destino + ": " + distancia + " salto(s).");
                    }
                    break;
                case 0:
                    System.out.println("Chau!");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}

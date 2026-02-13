package redsocial.app;

import redsocial.modelo.Accion;
import redsocial.modelo.Cliente;
import redsocial.modelo.SolicitudSeguimiento;
import redsocial.persistencia.CargadorJSON;
import redsocial.sistema.RedSocialEmpresarial;

import java.io.IOException;
import java.util.ArrayList;
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
            String ruta = "data/clientes.json";
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
                    Cliente c2 = red.buscarPorScoring(scoreBusq);
                    if (c2 != null) {
                        System.out.println("Encontrado: " + c2);
                    } else {
                        System.out.println("No encontrado.");
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
                        SolicitudSeguimiento s = red.procesarSiguienteSolicitud();
                        System.out.println("Procesada: " + s.getSolicitante() + " -> " + s.getObjetivo());
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
                    ArrayList<SolicitudSeguimiento> segs = red.obtenerSeguimientos();
                    if (segs.isEmpty()) {
                        System.out.println("No hay seguimientos registrados.");
                    } else {
                        System.out.println("Seguimientos confirmados:");
                        for (int i = 0; i < segs.size(); i++) {
                            SolicitudSeguimiento s = segs.get(i);
                            System.out.println("  - " + s.getSolicitante() + " -> " + s.getObjetivo());
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

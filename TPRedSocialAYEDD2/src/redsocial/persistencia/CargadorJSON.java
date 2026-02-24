package redsocial.persistencia;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redsocial.modelo.Cliente;
import redsocial.sistema.RedSocialEmpresarial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga los clientes desde un archivo JSON
 */
public class CargadorJSON {

    public static RedSocialEmpresarial CargarDesdeArchivo(String rutaArchivo) throws IOException {
        String contenido = leerArchivo(rutaArchivo);
        JsonObject root = JsonParser.parseString(contenido).getAsJsonObject();

        List<Cliente> clientes = parsearClientes(root);
        RedSocialEmpresarial sistema = new RedSocialEmpresarial();
        sistema.cargarClientesJson(clientes);

        if (root.has("relaciones")) {
            parsearYCargarRelaciones(root.getAsJsonArray("relaciones"), sistema);
        }

        return sistema;
    }

    private static String leerArchivo(String ruta) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ruta));
        StringBuilder sb = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            sb.append(linea);
        }
        br.close();
        return sb.toString();
    }

    public static List<Cliente> parsearClientes(JsonObject root) {
        List<Cliente> clientes = new ArrayList<>();
        JsonArray arrayClientes = root.getAsJsonArray("clientes");

        for (JsonElement elem : arrayClientes) {

            JsonObject obj = elem.getAsJsonObject();

            String nombre = obj.get("nombre").getAsString();
            int scoring = obj.get("scoring").getAsInt();

            Cliente cliente = new Cliente(nombre, scoring);

            JsonArray siguiendoArray = obj.getAsJsonArray("siguiendo");

            if (siguiendoArray != null) {
                for (JsonElement seg : siguiendoArray) {
                    cliente.seguir(seg.getAsString());
                }
            }

            clientes.add(cliente);
        }

        return clientes;
    }

    private static void parsearYCargarRelaciones(JsonArray arrayRelaciones, RedSocialEmpresarial sistema) {
        if (arrayRelaciones == null) return;
        for (JsonElement elem : arrayRelaciones) {
            JsonObject rel = elem.getAsJsonObject();
            String cliente1 = rel.get("cliente1").getAsString();
            String cliente2 = rel.get("cliente2").getAsString();
            String tipo = rel.has("tipo") ? rel.get("tipo").getAsString() : "conexion";
            sistema.agregarRelacion(cliente1, cliente2, tipo);
        }
    }

}

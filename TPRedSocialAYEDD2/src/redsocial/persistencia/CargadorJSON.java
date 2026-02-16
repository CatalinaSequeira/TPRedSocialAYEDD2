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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Carga los clientes desde un archivo JSON
 */
public class CargadorJSON {

    public static RedSocialEmpresarial CargarDesdeArchivo(String rutaArchivo) throws IOException {
        String contenido = leerArchivo(rutaArchivo);
        List<Cliente> clientes = parsearClientes(contenido);
        RedSocialEmpresarial sistema = new RedSocialEmpresarial();
        sistema.cargarClientesJson(clientes);
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

    public static List<Cliente> parsearClientes(String json) {

        List<Cliente> clientes = new ArrayList<>();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
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
}

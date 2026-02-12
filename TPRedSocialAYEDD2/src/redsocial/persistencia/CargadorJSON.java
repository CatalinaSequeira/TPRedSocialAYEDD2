package redsocial.persistencia;

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
        sistema.cargarClientesIniciales(clientes);
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
        ArrayList<Cliente> clientes = new ArrayList<Cliente>();
        Pattern p = Pattern.compile("\"nombre\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"scoring\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        while (m.find()) {
            String nombre = m.group(1).trim();
            int scoring = Integer.parseInt(m.group(2));
            clientes.add(new Cliente(nombre, scoring));
        }
        return clientes;
    }
}

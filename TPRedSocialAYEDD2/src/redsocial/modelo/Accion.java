package redsocial.modelo;

import java.time.LocalDateTime;

/**
 * Una accion realizada en la plataforma
 */
public class Accion {
    private TipoAccion tipo;

    //no se si detalles lo vamos a usar al final, si no es necesario lo podemos eliminar
    private String detalles;
    private LocalDateTime fechaHora;

    private String nombreClienteAgregado;
    private String seguidor;
    private String seguido;

    //constructor para registrar accion de agregar cliente
    public Accion(String nombreClienteAgregado) {
        this.tipo = TipoAccion.AGREGAR_CLIENTE;
        this.nombreClienteAgregado = nombreClienteAgregado;
        this.fechaHora = LocalDateTime.now();
        this.detalles = "Se agrego cliente: " + nombreClienteAgregado;
    }


    //constructor para registrar accion de seguir cliente
    public Accion(String seguidor, String seguido) {
        this.tipo = TipoAccion.SEGUIR_CLIENTE;
        this.seguidor = seguidor;
        this.seguido = seguido;
        this.fechaHora = LocalDateTime.now();
        this.detalles = seguidor +" siguio a " + seguido;
    }

    public TipoAccion getTipo() {
        return tipo;
    }

    public String getDetalles() {
        return detalles;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getNombreClienteAgregado() {
        return nombreClienteAgregado;
    }

    public String getSeguidor() {
        return seguidor;
    }

    public String getSeguido() {
        return seguido;
    }


}

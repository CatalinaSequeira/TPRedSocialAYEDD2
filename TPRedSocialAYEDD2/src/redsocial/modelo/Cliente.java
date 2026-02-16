package redsocial.modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un cliente de la red social
 */
public class Cliente {
  private String nombre;
  private int scoring;

  private Set<String> seguidos = new HashSet<>();


  public Cliente(String nombre, int scoring) {
    // TODO Posibilidad de agregar tests para estos dos escenarios
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre no puede estar vacio");
    }
    if (scoring < 0) {
      throw new IllegalArgumentException("El scoring no puede ser negativo");
    }
    this.nombre = nombre.trim();
    this.scoring = scoring;
  }

  public String getNombre() {
    return nombre;
  }

  public int getScoring() {
    return scoring;
  }

  public String toString() {
    return nombre + " (scoring: " + scoring + ")";
  }

  public void seguir(String nombreClienteASeguir) {
    String key = nombreClienteASeguir.toLowerCase();

    if (seguidos.contains(key)) {
      throw new IllegalStateException("El cliente ya sigue a " + nombreClienteASeguir);
    }

    if (seguidos.size() >= 2) {
      throw new IllegalStateException("Un cliente solo puede seguir hasta dos clientes");
    }

    seguidos.add(key);
  }

  public void dejarDeSeguir(String nombreClienteADejarDeSeguir) {
    seguidos.remove(nombreClienteADejarDeSeguir.toLowerCase());
  }

  public Set<String> getSeguidos() {
    return seguidos;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cliente)) return false;
    return this.nombre.equals(((Cliente) obj).nombre);
  }

  @Override
  public int hashCode() {
    return this.nombre.hashCode();
  }
}

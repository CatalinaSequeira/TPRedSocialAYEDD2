package redsocial.modelo;

/**
 * Representa un cliente de la red social
 */
public class Cliente {
  private String nombre;
  private int scoring;

  public Cliente(String nombre, int scoring) {
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
}

package sv.edu.ues.ape115.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa un rol de usuario en el sistema.
 * Identificador: UUID (String).
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Role() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    public Role(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return nombre; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        return Objects.equals(id, ((Role) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

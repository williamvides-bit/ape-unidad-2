package sv.edu.ues.ape115.controller;

import sv.edu.ues.ape115.dao.RoleDAO;
import sv.edu.ues.ape115.model.Role;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestion del catalogo de roles de usuario.
 */
public class RoleController {

    private final RoleDAO roleDAO;

    public RoleController(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public List<Role> listarTodos() { return roleDAO.findAll(); }

    public List<Role> listarActivos() { return roleDAO.findAllActivos(); }

    public List<Role> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return listarTodos();
        return roleDAO.search(criterio.trim());
    }

    public Optional<Role> obtenerPorId(String id) { return roleDAO.findById(id); }

    public String guardar(Role role) {
        if (role.getNombre() == null || role.getNombre().trim().isEmpty())
            return "El nombre del rol es requerido";
        if (role.getDescripcion() == null || role.getDescripcion().trim().isEmpty())
            return "La descripcion es requerida";

        boolean existe = roleDAO.findById(role.getId()).isPresent();
        if (existe) {
            return roleDAO.update(role) ? null : "Error al actualizar el rol";
        } else {
            return roleDAO.insert(role) ? null : "Error al crear el rol";
        }
    }

    public boolean eliminar(String id) { return roleDAO.delete(id); }
}

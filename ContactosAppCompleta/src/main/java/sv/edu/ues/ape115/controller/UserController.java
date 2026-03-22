package sv.edu.ues.ape115.controller;

import sv.edu.ues.ape115.dao.UserDAO;
import sv.edu.ues.ape115.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestion del catalogo de usuarios.
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> listarTodos() { return userDAO.findAll(); }

    public List<User> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return listarTodos();
        return userDAO.search(criterio.trim());
    }

    public Optional<User> obtenerPorId(String id) { return userDAO.findById(id); }

    public String guardar(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty())
            return "El nombre de usuario es requerido";
        if (user.getPassword() == null || user.getPassword().trim().isEmpty())
            return "La contrasena es requerida";
        if (user.getNombreCompleto() == null || user.getNombreCompleto().trim().isEmpty())
            return "El nombre completo es requerido";
        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            return "El email es requerido";
        if (user.getRol() == null)
            return "Debe seleccionar un rol";

        boolean existe = userDAO.findById(user.getId()).isPresent();
        if (existe) {
            return userDAO.update(user) ? null : "El nombre de usuario ya existe";
        } else {
            return userDAO.insert(user) ? null : "El nombre de usuario ya existe";
        }
    }

    public boolean eliminar(String id) { return userDAO.delete(id); }

    public int contarUsuarios() { return userDAO.findAll().size(); }
}

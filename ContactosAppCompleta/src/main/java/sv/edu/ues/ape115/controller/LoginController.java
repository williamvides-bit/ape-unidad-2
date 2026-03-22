package sv.edu.ues.ape115.controller;

import sv.edu.ues.ape115.dao.UserDAO;
import sv.edu.ues.ape115.model.Session;
import sv.edu.ues.ape115.model.User;
import java.util.Optional;

/**
 * Controlador para la lógica de inicio de sesión.
 * Valida credenciales y gestiona el inicio/cierre de sesión.
 */
public class LoginController {

    private final UserDAO userDAO;

    public LoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Intenta autenticar al usuario con las credenciales dadas.
     * @return mensaje de error o null si fue exitoso
     */
    public String login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "El nombre de usuario es requerido";
        }
        if (password == null || password.trim().isEmpty()) {
            return "La contraseña es requerida";
        }

        Optional<User> user = userDAO.authenticate(username.trim(), password);
        if (user.isPresent()) {
            Session.getInstance().iniciarSesion(user.get());
            return null; // Login exitoso
        }
        return "Usuario o contraseña incorrectos";
    }

    /** Cierra la sesión activa */
    public void logout() {
        Session.getInstance().cerrarSesion();
    }

    /** Verifica si hay sesión activa */
    public boolean haySesionActiva() {
        return Session.getInstance().isActiva();
    }

    /** Obtiene el usuario de la sesión activa */
    public User getUsuarioActivo() {
        return Session.getInstance().getUsuarioActivo();
    }
}

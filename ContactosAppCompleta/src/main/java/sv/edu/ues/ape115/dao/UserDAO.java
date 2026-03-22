package sv.edu.ues.ape115.dao;

import sv.edu.ues.ape115.model.User;
import sv.edu.ues.ape115.model.Role;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO en memoria para gestion de usuarios. Precarga usuarios por defecto.
 */
public class UserDAO implements GenericDAO<User> {
    
    private final List<User> users = new ArrayList<>();

    public UserDAO(RoleDAO roleDAO) {
        precargarDatos(roleDAO);
    }

    private void precargarDatos(RoleDAO roleDAO) {
        Role admin = roleDAO.getRolInicial(0);
        Role supervisor = roleDAO.getRolInicial(1);
        Role operador = roleDAO.getRolInicial(2);

        users.add(new User("admin", "admin123", "Administrador del Sistema", "admin@ues.edu.sv", admin));
        users.add(new User("jperez", "pass123", "Juan Perez Lopez", "jperez@ues.edu.sv", supervisor));
        users.add(new User("mgarcia", "pass123", "Maria Garcia Hernandez", "mgarcia@ues.edu.sv", operador));
        users.add(new User("clopez", "pass123", "Carlos Lopez Rivera", "clopez@ues.edu.sv", operador));
        users.add(new User("amorales", "pass123", "Ana Morales Vega", "amorales@ues.edu.sv", supervisor));
    }

    @Override
    public List<User> findAll() { return new ArrayList<>(users); }

    @Override
    public Optional<User> findById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) &&
                             u.getPassword().equals(password) &&
                             u.isActivo())
                .findFirst();
    }

    @Override
    public boolean insert(User user) {
        if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())
                && !u.getId().equals(user.getId()))) {
            return false;
        }
        return users.add(user);
    }

    @Override
    public boolean update(User user) {
        boolean duplicado = users.stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername())
                        && !u.getId().equals(user.getId()));
        if (duplicado) return false;
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return users.removeIf(u -> u.getId().equals(id));
    }

    @Override
    public List<User> search(String criteria) {
        String lower = criteria.toLowerCase();
        return users.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(lower) ||
                             u.getNombreCompleto().toLowerCase().contains(lower) ||
                             u.getEmail().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}

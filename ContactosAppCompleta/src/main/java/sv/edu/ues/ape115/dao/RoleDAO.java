package sv.edu.ues.ape115.dao;

import sv.edu.ues.ape115.model.Role;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO en memoria para gestion de roles. Precarga roles por defecto.
 */
public class RoleDAO implements GenericDAO<Role> {
    
    private final List<Role> roles = new ArrayList<>();

    // Guardamos las referencias para que UserDAO pueda obtenerlas por indice
    private final List<Role> rolesIniciales = new ArrayList<>();

    public RoleDAO() {
        precargarDatos();
    }

    private void precargarDatos() {
        Role r1 = new Role("Administrador", "Acceso total al sistema");
        Role r2 = new Role("Supervisor", "Gestion de catalogos y reportes");
        Role r3 = new Role("Operador", "Gestion de contactos unicamente");
        Role r4 = new Role("Consultor", "Solo lectura de informacion");
        Role r5 = new Role("Auditor", "Revision de registros y actividad");
        roles.addAll(List.of(r1, r2, r3, r4, r5));
        rolesIniciales.addAll(roles);
    }

    /** Obtiene un rol precargado por indice (0=Admin, 1=Supervisor, 2=Operador...) */
    public Role getRolInicial(int index) {
        return index >= 0 && index < rolesIniciales.size() ? rolesIniciales.get(index) : null;
    }

    @Override
    public List<Role> findAll() { return new ArrayList<>(roles); }

    public List<Role> findAllActivos() {
        return roles.stream().filter(Role::isActivo).collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findById(String id) {
        return roles.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public boolean insert(Role role) { return roles.add(role); }

    @Override
    public boolean update(Role role) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getId().equals(role.getId())) {
                roles.set(i, role);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return roles.removeIf(r -> r.getId().equals(id));
    }

    @Override
    public List<Role> search(String criteria) {
        String lower = criteria.toLowerCase();
        return roles.stream()
                .filter(r -> r.getNombre().toLowerCase().contains(lower) ||
                             r.getDescripcion().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}

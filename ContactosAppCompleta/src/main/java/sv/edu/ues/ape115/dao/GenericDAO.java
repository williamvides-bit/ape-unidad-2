package sv.edu.ues.ape115.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz generica para operaciones CRUD en memoria.
 * Los identificadores son de tipo String (UUID).
 */
public interface GenericDAO<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    boolean insert(T entity);
    boolean update(T entity);
    boolean delete(String id);
    List<T> search(String criteria);
}

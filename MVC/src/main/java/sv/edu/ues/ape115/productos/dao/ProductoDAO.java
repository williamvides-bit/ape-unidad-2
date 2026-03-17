package sv.edu.ues.ape115.productos.dao;

import sv.edu.ues.ape115.productos.model.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adm0n
 */
public class ProductoDAO {
    private List<Producto> productos = new ArrayList<>();
    private int nextId = 1;

    public ProductoDAO() {
        // Precarga de datos de ejemplo
        agregar(new Producto(0, "Laptop HP Pavilion", 899.99, 15));
        agregar(new Producto(0, "Mouse Logitech G502", 59.99, 45));
        agregar(new Producto(0, "Teclado Corsair K70", 129.99, 30));
    }

    public void agregar(Producto p) {
        p.setId(nextId++);
        productos.add(p);
    }

    public List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    public Producto buscarPorId(int id) {
        return productos.stream()
            .filter(p -> p.getId() == id)
            .findFirst().orElse(null);
    }

    public boolean eliminar(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }
}
package sv.edu.ues.ape115.productos.app;

import sv.edu.ues.ape115.productos.dao.ProductoDAO;
import sv.edu.ues.ape115.productos.ui.ProductoConsoleView;
import sv.edu.ues.ape115.productos.controller.ProductoController;

/**
 * Clase principal del sistema de Gestion de Productos.
 * Punto de entrada de la aplicacion.
 *
 * Patron MVC:
 *   1. Se crea el DAO (capa de datos)
 *   2. Se crea la Vista (capa de presentacion)
 *   3. Se crea el Controller (conecta DAO y Vista)
 *   4. Se inicia la aplicacion
 */
public class App {

    public static void main(String[] args) {
        // 1. Crear la capa de datos (DAO con ArrayList en memoria)
        ProductoDAO dao = new ProductoDAO();

        // 2. Crear la Vista (interfaz de consola)
        ProductoConsoleView vista = new ProductoConsoleView();

        // 3. Crear el Controller (conecta DAO y Vista)
        ProductoController controller = new ProductoController(dao, vista);

        // 4. Iniciar la aplicacion
        controller.iniciar();
    }
}
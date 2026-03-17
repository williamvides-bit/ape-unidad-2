package sv.edu.ues.ape115.productos.controller;

import sv.edu.ues.ape115.productos.model.Producto;
import sv.edu.ues.ape115.productos.dao.ProductoDAO;
import sv.edu.ues.ape115.productos.ui.ProductoConsoleView;

public class ProductoController {
    private ProductoDAO dao;
    private ProductoConsoleView view;

    public ProductoController(ProductoDAO dao, ProductoConsoleView view) {
        this.dao = dao;
        this.view = view;
    }

    public void iniciar() {
        boolean ejecutando = true;
        while (ejecutando) {
            int opcion = view.mostrarMenu();
            switch (opcion) {
                case 1:
                    view.mostrarProductos(dao.listar());
                    break;
                case 2:
                    agregarProducto();
                    break;
                case 3:
                    buscarProducto();
                    break;
                case 4:
                    eliminarProducto();
                    break;
                case 5:
                    ejecutando = false;
                    view.mostrarMensaje("Hasta luego!");
                    break;
                default:
                    view.mostrarError("Opcion invalida.");
            }
        }
    }

    private void agregarProducto() {
        String nombre = view.pedirTexto("Nombre: ");
        if (nombre.length() < 3) {
            view.mostrarError("El nombre debe tener minimo 3 caracteres.");
            return;
        }

        view.mostrarMensaje("Ingrese el precio:");
        double precio = view.leerDecimal();
        if (precio <= 0) {
            view.mostrarError("El precio debe ser mayor a 0.");
            return;
        }

        view.mostrarMensaje("Ingrese el stock:");
        int stock = view.leerEntero();
        if (stock < 0) {
            view.mostrarError("El stock no puede ser negativo.");
            return;
        }

        dao.agregar(new Producto(0, nombre, precio, stock));
        view.mostrarMensaje("Producto agregado exitosamente.");
    }

    private void buscarProducto() {
        view.mostrarMensaje("Ingrese el ID del producto:");
        int id = view.leerEntero();
        Producto p = dao.buscarPorId(id);
        if (p != null) {
            view.mostrarMensaje("Producto encontrado: " + p);
        } else {
            view.mostrarError("Producto con ID " + id + " no encontrado.");
        }
    }

    private void eliminarProducto() {
        view.mostrarMensaje("Ingrese el ID a eliminar:");
        int id = view.leerEntero();
        if (dao.eliminar(id)) {
            view.mostrarMensaje("Producto eliminado.");
        } else {
            view.mostrarError("No se encontro el producto.");
        }
    }
}
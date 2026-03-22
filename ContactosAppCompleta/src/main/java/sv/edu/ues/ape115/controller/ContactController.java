package sv.edu.ues.ape115.controller;

import sv.edu.ues.ape115.dao.ContactDAO;
import sv.edu.ues.ape115.model.Contact;
import sv.edu.ues.ape115.util.VCardUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestion del catalogo de contactos.
 */
public class ContactController {

    private final ContactDAO contactDAO;

    public ContactController(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }

    public List<Contact> listarTodos() { return contactDAO.findAll(); }

    public List<Contact> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return listarTodos();
        return contactDAO.search(criterio.trim());
    }

    public Optional<Contact> obtenerPorId(String id) {
        return contactDAO.findById(id);
    }

    /**
     * Guarda un contacto. Si es nuevo (no existe en DAO) lo inserta; si existe, lo actualiza.
     * El UUID se genera automaticamente en el constructor de Contact.
     */
    public String guardar(Contact contact) {
        if (contact.getNombre() == null || contact.getNombre().trim().isEmpty())
            return "El nombre es requerido";
        if (contact.getApellido() == null || contact.getApellido().trim().isEmpty())
            return "El apellido es requerido";
        if (contact.getEmail() == null || contact.getEmail().trim().isEmpty())
            return "El email es requerido";

        // Si ya existe en el DAO, actualizar; si no, insertar
        boolean existe = contactDAO.findById(contact.getId()).isPresent();
        if (existe) {
            return contactDAO.update(contact) ? null : "Error al actualizar el contacto";
        } else {
            return contactDAO.insert(contact) ? null : "Error al crear el contacto";
        }
    }

    public boolean eliminar(String id) { return contactDAO.delete(id); }

    public String exportarVCard(List<Contact> contactos, File archivo) {
        try {
            VCardUtil.exportar(contactos, archivo);
            return null;
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }

    public String importarVCard(File archivo) {
        try {
            List<Contact> importados = VCardUtil.importar(archivo);
            if (importados.isEmpty()) return "No se encontraron contactos en el archivo";
            for (Contact c : importados) {
                contactDAO.insert(c); // UUID ya generado en constructor
            }
            return null;
        } catch (IOException e) {
            return "Error al importar: " + e.getMessage();
        }
    }

    public int contarContactos() { return contactDAO.findAll().size(); }
}

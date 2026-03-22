package sv.edu.ues.ape115.dao;

import sv.edu.ues.ape115.model.Contact;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO en memoria para gestion de contactos. Precarga 20 contactos.
 */
public class ContactDAO implements GenericDAO<Contact> {
    
    private final List<Contact> contacts = new ArrayList<>();

    public ContactDAO() {
        precargarDatos();
    }

    private void precargarDatos() {
        String[][] datos = {
            {"Roberto", "Martinez", "rmartinez@email.com", "2222-1001", "7000-1001", "Col. Escalon #123", "San Salvador", "El Salvador", "TechSV", "Gerente TI"},
            {"Lucia", "Hernandez", "lhernandez@email.com", "2222-1002", "7000-1002", "Res. San Luis #45", "Santa Ana", "El Salvador", "BancoAgri", "Contadora"},
            {"Fernando", "Lopez", "flopez@email.com", "2222-1003", "7000-1003", "Urb. Jardines #78", "San Miguel", "El Salvador", "Grupo Q", "Director"},
            {"Patricia", "Garcia", "pgarcia@email.com", "2222-1004", "7000-1004", "Av. Olimpica #321", "San Salvador", "El Salvador", "Claro SV", "Analista"},
            {"Miguel", "Rivas", "mrivas@email.com", "2222-1005", "7000-1005", "Blvd. Constitucion", "Soyapango", "El Salvador", "Tigo", "Ingeniero"},
            {"Carmen", "Flores", "cflores@email.com", "2222-1006", "7000-1006", "Col. Medica #56", "San Salvador", "El Salvador", "Hospital Bloom", "Doctora"},
            {"Diego", "Argueta", "dargueta@email.com", "2222-1007", "7000-1007", "Res. Altamira #12", "Antiguo Cuscatlan", "El Salvador", "UES", "Profesor"},
            {"Sofia", "Mejia", "smejia@email.com", "2222-1008", "7000-1008", "Col. Roma #89", "San Salvador", "El Salvador", "PWC SV", "Auditora"},
            {"Andres", "Vasquez", "avasquez@email.com", "2222-1009", "7000-1009", "Urb. Lomas #34", "Santa Tecla", "El Salvador", "Aeroman", "Tecnico"},
            {"Valeria", "Orellana", "vorellana@email.com", "2222-1010", "7000-1010", "Av. Masferrer #67", "San Salvador", "El Salvador", "UTEC", "Decana"},
            {"Hector", "Mendoza", "hmendoza@email.com", "2222-1011", "7000-1011", "Col. Miramonte", "San Salvador", "El Salvador", "AES SV", "Supervisor"},
            {"Gabriela", "Sandoval", "gsandoval@email.com", "2222-1012", "7000-1012", "Res. Pinares #23", "Sonsonate", "El Salvador", "La Constancia", "Jefa RRHH"},
            {"Oscar", "Portillo", "oportillo@email.com", "2222-1013", "7000-1013", "Blvd. Orden Malta", "Antiguo Cuscatlan", "El Salvador", "Microsoft SV", "Dev Lead"},
            {"Daniela", "Chavez", "dchavez@email.com", "2222-1014", "7000-1014", "Col. San Benito", "San Salvador", "El Salvador", "Avianca", "Ejecutiva"},
            {"Raul", "Escobar", "rescobar@email.com", "2222-1015", "7000-1015", "Urb. Satelite #45", "San Salvador", "El Salvador", "Walmart SV", "Logistica"},
            {"Natalia", "Perez", "nperez@email.com", "2222-1016", "7000-1016", "Av. Bernal #89", "San Salvador", "El Salvador", "ONU SV", "Consultora"},
            {"Eduardo", "Molina", "emolina@email.com", "2222-1017", "7000-1017", "Res. Escalon #56", "San Salvador", "El Salvador", "Banco Cuscatlan", "Gerente"},
            {"Alejandra", "Torres", "atorres@email.com", "2222-1018", "7000-1018", "Col. Layco #34", "San Salvador", "El Salvador", "CNN SV", "Periodista"},
            {"Jorge", "Ramirez", "jramirez@email.com", "2222-1019", "7000-1019", "Urb. Montefresco", "San Salvador", "El Salvador", "Holcim SV", "Arquitecto"},
            {"Isabella", "Cruz", "icruz@email.com", "2222-1020", "7000-1020", "Blvd. Los Heroes #12", "San Salvador", "El Salvador", "UCA", "Investigadora"}
        };

        int idx = 1;
        for (String[] d : datos) {
            Contact c = new Contact(d[0], d[1], d[2], d[3], d[4]);
            c.setDireccion(d[5]);
            c.setCiudad(d[6]);
            c.setPais(d[7]);
            c.setOrganizacion(d[8]);
            c.setCargo(d[9]);
            c.setFechaNacimiento(LocalDate.of(1980 + (idx % 20), (idx % 12) + 1, (idx % 28) + 1));
            c.setNotas("Contacto precargado #" + idx);
            contacts.add(c);
            idx++;
        }
    }

    @Override
    public List<Contact> findAll() { return new ArrayList<>(contacts); }

    @Override
    public Optional<Contact> findById(String id) {
        return contacts.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public boolean insert(Contact contact) { return contacts.add(contact); }

    @Override
    public boolean update(Contact contact) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(contact.getId())) {
                contact.setFechaModificacion(java.time.LocalDateTime.now());
                contacts.set(i, contact);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return contacts.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public List<Contact> search(String criteria) {
        String lower = criteria.toLowerCase();
        return contacts.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(lower) ||
                             c.getApellido().toLowerCase().contains(lower) ||
                             c.getEmail().toLowerCase().contains(lower) ||
                             (c.getTelefono() != null && c.getTelefono().contains(lower)) ||
                             (c.getCelular() != null && c.getCelular().contains(lower)) ||
                             (c.getOrganizacion() != null && c.getOrganizacion().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }
}

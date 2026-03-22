package sv.edu.ues.ape115.util;

import sv.edu.ues.ape115.model.Contact;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utilidad para importar y exportar contactos en formato vCard 3.0.
 * Soporta campos estándar incluyendo imagen en Base64 (PHOTO).
 */
public class VCardUtil {

    private static final DateTimeFormatter BDAY_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Exporta una lista de contactos a un archivo .vcf
     */
    public static void exportar(List<Contact> contactos, File archivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            for (Contact c : contactos) {
                writer.write("BEGIN:VCARD\r\n");
                writer.write("VERSION:3.0\r\n");
                writer.write("N:" + esc(c.getApellido()) + ";" + esc(c.getNombre()) + ";;;\r\n");
                writer.write("FN:" + esc(c.getNombreCompleto()) + "\r\n");
                
                if (c.getEmail() != null && !c.getEmail().isEmpty())
                    writer.write("EMAIL;TYPE=INTERNET:" + esc(c.getEmail()) + "\r\n");
                if (c.getTelefono() != null && !c.getTelefono().isEmpty())
                    writer.write("TEL;TYPE=WORK:" + esc(c.getTelefono()) + "\r\n");
                if (c.getCelular() != null && !c.getCelular().isEmpty())
                    writer.write("TEL;TYPE=CELL:" + esc(c.getCelular()) + "\r\n");
                if (c.getDireccion() != null && !c.getDireccion().isEmpty())
                    writer.write("ADR;TYPE=HOME:;;" + esc(c.getDireccion()) + ";" + 
                                 esc(c.getCiudad()) + ";;" + ";" + esc(c.getPais()) + "\r\n");
                if (c.getOrganizacion() != null && !c.getOrganizacion().isEmpty())
                    writer.write("ORG:" + esc(c.getOrganizacion()) + "\r\n");
                if (c.getCargo() != null && !c.getCargo().isEmpty())
                    writer.write("TITLE:" + esc(c.getCargo()) + "\r\n");
                if (c.getFechaNacimiento() != null)
                    writer.write("BDAY:" + c.getFechaNacimiento().format(BDAY_FORMAT) + "\r\n");
                if (c.getNotas() != null && !c.getNotas().isEmpty())
                    writer.write("NOTE:" + esc(c.getNotas()) + "\r\n");
                
                // Imagen en Base64
                if (c.getImagenBase64() != null && !c.getImagenBase64().isEmpty()) {
                    String tipo = "JPEG";
                    if (c.getTipoImagen() != null && c.getTipoImagen().contains("png")) tipo = "PNG";
                    writer.write("PHOTO;ENCODING=b;TYPE=" + tipo + ":" + c.getImagenBase64() + "\r\n");
                }
                
                writer.write("END:VCARD\r\n");
            }
        }
    }

    /**
     * Importa contactos desde un archivo .vcf
     */
    public static List<Contact> importar(File archivo) throws IOException {
        List<Contact> contactos = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String line;
            Contact contacto = null;
            StringBuilder photoBuilder = null;
            
            while ((line = reader.readLine()) != null) {
                // Continuar línea de foto multi-línea
                if (photoBuilder != null && (line.startsWith(" ") || line.startsWith("\t"))) {
                    photoBuilder.append(line.trim());
                    continue;
                } else if (photoBuilder != null) {
                    if (contacto != null) contacto.setImagenBase64(photoBuilder.toString());
                    photoBuilder = null;
                }
                
                if (line.startsWith("BEGIN:VCARD")) {
                    contacto = new Contact();
                } else if (line.startsWith("END:VCARD") && contacto != null) {
                    if (contacto.getNombre() != null) contactos.add(contacto);
                    contacto = null;
                } else if (contacto != null) {
                    parseLine(line, contacto);
                    
                    // Detectar inicio de PHOTO
                    if (line.startsWith("PHOTO")) {
                        int idx = line.indexOf(':');
                        if (idx > 0) {
                            photoBuilder = new StringBuilder(line.substring(idx + 1));
                            if (line.toUpperCase().contains("PNG")) {
                                contacto.setTipoImagen("image/png");
                            } else {
                                contacto.setTipoImagen("image/jpeg");
                            }
                        }
                    }
                }
            }
        }
        return contactos;
    }

    private static void parseLine(String line, Contact c) {
        if (line.startsWith("N:")) {
            String[] parts = line.substring(2).split(";", -1);
            if (parts.length >= 2) {
                c.setApellido(unesc(parts[0]));
                c.setNombre(unesc(parts[1]));
            }
        } else if (line.startsWith("FN:") && c.getNombre() == null) {
            String fn = unesc(line.substring(3));
            String[] parts = fn.split(" ", 2);
            c.setNombre(parts[0]);
            if (parts.length > 1) c.setApellido(parts[1]);
        } else if (line.startsWith("EMAIL")) {
            c.setEmail(extractValue(line));
        } else if (line.contains("TEL") && line.contains("CELL")) {
            c.setCelular(extractValue(line));
        } else if (line.startsWith("TEL")) {
            c.setTelefono(extractValue(line));
        } else if (line.startsWith("ADR")) {
            String val = extractValue(line);
            String[] parts = val.split(";", -1);
            if (parts.length >= 3) c.setDireccion(parts[2]);
            if (parts.length >= 4) c.setCiudad(parts[3]);
            if (parts.length >= 7) c.setPais(parts[6]);
        } else if (line.startsWith("ORG:")) {
            c.setOrganizacion(unesc(line.substring(4)));
        } else if (line.startsWith("TITLE:")) {
            c.setCargo(unesc(line.substring(6)));
        } else if (line.startsWith("BDAY:")) {
            try {
                c.setFechaNacimiento(LocalDate.parse(line.substring(5), BDAY_FORMAT));
            } catch (Exception ignored) {}
        } else if (line.startsWith("NOTE:")) {
            c.setNotas(unesc(line.substring(5)));
        }
    }

    private static String extractValue(String line) {
        int idx = line.indexOf(':');
        return idx >= 0 ? unesc(line.substring(idx + 1)) : "";
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;");
    }

    private static String unesc(String s) {
        return s == null ? "" : s.replace("\\;", ";").replace("\\,", ",").replace("\\\\", "\\");
    }
}

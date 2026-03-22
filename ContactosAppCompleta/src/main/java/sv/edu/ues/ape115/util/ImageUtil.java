package sv.edu.ues.ape115.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * Utilidad para manipulación de imágenes de contactos.
 * Convierte imágenes a/desde Base64 para almacenamiento en memoria.
 *
 * Flujo completo:
 *  1. seleccionarImagen() abre JFileChooser, lee archivo, redimensiona,
 *     convierte a PNG (RGB sin alfa), codifica a Base64 String.
 *  2. base64ToIcon() decodifica Base64 a bytes, lee como BufferedImage,
 *     escala centrada al tamaño solicitado, devuelve ImageIcon.
 *  3. generarAvatarDefault() crea avatar circular con iniciales.
 *
 * NOTA: Siempre se usa PNG como formato intermedio porque JPEG no soporta
 * canal alfa y ImageIO.write("jpg", ...) falla silenciosamente con
 * BufferedImage de tipo TYPE_INT_ARGB, produciendo 0 bytes.
 */
public class ImageUtil {

    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;
    private static final int THUMBNAIL_SIZE = 40;

    /**
     * Abre dialogo para seleccionar imagen y la convierte a Base64 PNG.
     *
     * @param parent componente padre para el dialogo
     * @return String[]{base64, "image/png"} o null si cancelo o hubo error
     */
    public static String[] seleccionarImagen(Component parent) {
        Window window = SwingUtilities.getWindowAncestor(parent);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen de contacto");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Imagenes (JPG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp"));
        chooser.setAcceptAllFileFilterUsed(false);

        int res = chooser.showOpenDialog(window != null ? window : parent);
        if (res != JFileChooser.APPROVE_OPTION) return null;

        File file = chooser.getSelectedFile();
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(parent,
                    "El archivo seleccionado no existe.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            BufferedImage original = ImageIO.read(file);
            if (original == null) {
                JOptionPane.showMessageDialog(parent,
                        "No se pudo leer el archivo como imagen.\nFormatos: JPG, PNG, GIF, BMP",
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // 1. Asegurar tipo ARGB para manipulacion uniforme
            BufferedImage argb = asegurarARGB(original);

            // 2. Redimensionar si excede maximo
            BufferedImage resized = redimensionar(argb, MAX_WIDTH, MAX_HEIGHT);

            // 3. Convertir a RGB (fondo blanco) para que PNG se escriba sin problemas
            BufferedImage rgb = aRGBconFondoBlanco(resized);

            // 4. Escribir como PNG a bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean ok = ImageIO.write(rgb, "png", baos);
            baos.flush();

            if (!ok || baos.size() == 0) {
                JOptionPane.showMessageDialog(parent,
                        "Error al convertir la imagen a formato interno.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // 5. Codificar a Base64
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            System.out.println("[ImageUtil] OK: " + file.getName()
                    + " | " + rgb.getWidth() + "x" + rgb.getHeight()
                    + " | base64.length=" + base64.length());

            return new String[]{base64, "image/png"};

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Error al leer la imagen: " + e.getMessage(),
                    "Error de lectura", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Convierte un String Base64 a ImageIcon escalado al tamaño dado.
     * Centra la imagen manteniendo proporcion dentro de width x height.
     */
    public static ImageIcon base64ToIcon(String base64, int width, int height) {
        if (base64 == null || base64.trim().isEmpty()) return null;

        try {
            // Limpiar posibles saltos de linea/espacios en el base64
            String clean = base64.replaceAll("\\s+", "");
            byte[] bytes = Base64.getDecoder().decode(clean);
            if (bytes.length == 0) return null;

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            if (img == null) {
                System.err.println("[ImageUtil] ImageIO no pudo leer " + bytes.length + " bytes");
                return null;
            }

            // Escalar manteniendo aspecto, centrar en canvas de width x height
            double ratio = Math.min((double) width / img.getWidth(),
                                    (double) height / img.getHeight());
            int sw = Math.max(1, (int) (img.getWidth() * ratio));
            int sh = Math.max(1, (int) (img.getHeight() * ratio));

            BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = canvas.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int ox = (width - sw) / 2;
            int oy = (height - sh) / 2;
            g.drawImage(img, ox, oy, sw, sh, null);
            g.dispose();

            return new ImageIcon(canvas);

        } catch (IllegalArgumentException e) {
            System.err.println("[ImageUtil] Base64 invalido: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[ImageUtil] Error IO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ImageUtil] Error: " + e.getMessage());
        }
        return null;
    }

    /** Genera thumbnail para tablas. */
    public static ImageIcon base64ToThumbnail(String base64) {
        return base64ToIcon(base64, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

    /** Genera avatar circular con iniciales del contacto. */
    public static ImageIcon generarAvatarDefault(String nombre, String apellido, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int hash = ((nombre != null ? nombre : "") + (apellido != null ? apellido : "")).hashCode();
        Color bg = new Color(Math.abs(hash % 180) + 60,
                             Math.abs((hash >> 8) % 180) + 60,
                             Math.abs((hash >> 16) % 180) + 60);
        g.setColor(bg);
        g.fillOval(0, 0, size, size);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, size / 3));
        String n = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1).toUpperCase() : "";
        String a = (apellido != null && !apellido.isEmpty()) ? apellido.substring(0, 1).toUpperCase() : "";
        String initials = n + a;
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(initials)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(initials, x, y);
        g.dispose();

        return new ImageIcon(img);
    }

    // ====== Metodos privados de conversion ======

    /** Convierte a TYPE_INT_ARGB si no lo es ya. */
    private static BufferedImage asegurarARGB(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) return src;
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    /** Convierte a RGB pintando sobre fondo blanco (elimina canal alfa). */
    private static BufferedImage aRGBconFondoBlanco(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    /** Redimensiona manteniendo proporcion si excede maxW x maxH. */
    private static BufferedImage redimensionar(BufferedImage original, int maxW, int maxH) {
        int w = original.getWidth();
        int h = original.getHeight();
        if (w <= maxW && h <= maxH) return original;

        double ratio = Math.min((double) maxW / w, (double) maxH / h);
        int nw = Math.max(1, (int) (w * ratio));
        int nh = Math.max(1, (int) (h * ratio));

        BufferedImage out = new BufferedImage(nw, nh, original.getType());
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(original, 0, 0, nw, nh, null);
        g.dispose();
        return out;
    }
}

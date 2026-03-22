package sv.edu.ues.ape115.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Utilidad para validaciones en tiempo real de campos de formulario.
 * Proporciona validación visual con bordes coloreados y tooltips informativos.
 * 
 * Principios UX aplicados:
 * - Feedback inmediato: el usuario ve el estado de validación mientras escribe
 * - Prevención de errores: guía visual antes de enviar formulario
 * - Consistencia: mismos colores y comportamiento en toda la aplicación
 */
public class Validator {

    private static final Color COLOR_VALIDO = new Color(46, 139, 87);
    private static final Color COLOR_INVALIDO = new Color(220, 53, 69);
    private static final Color COLOR_NEUTRAL = UIManager.getColor("TextField.border") != null 
            ? UIManager.getColor("TextField.border").darker() : Color.GRAY;

    private static final Border BORDER_VALIDO = BorderFactory.createLineBorder(COLOR_VALIDO, 2);
    private static final Border BORDER_INVALIDO = BorderFactory.createLineBorder(COLOR_INVALIDO, 2);
    private static final Border BORDER_NEUTRAL = BorderFactory.createLineBorder(COLOR_NEUTRAL, 1);

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO_PATTERN = 
            Pattern.compile("^\\d{4}-\\d{4}$");
    private static final Pattern USERNAME_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern NOMBRE_PATTERN = 
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]{2,50}$");
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile("^.{6,}$");

    /** Valida que el campo no esté vacío */
    public static boolean validarRequerido(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarInvalido(field, fieldName + " es requerido");
            return false;
        }
        marcarValido(field);
        return true;
    }

    /** Valida formato de email */
    public static boolean validarEmail(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true; // Vacío se valida con validarRequerido
        }
        if (EMAIL_PATTERN.matcher(text).matches()) {
            marcarValido(field);
            return true;
        }
        marcarInvalido(field, "Formato de email inválido (ej: usuario@dominio.com)");
        return false;
    }

    /** Valida formato de teléfono salvadoreño ####-#### */
    public static boolean validarTelefono(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true;
        }
        if (TELEFONO_PATTERN.matcher(text).matches()) {
            marcarValido(field);
            return true;
        }
        marcarInvalido(field, "Formato: ####-#### (ej: 2222-1234)");
        return false;
    }

    /** Valida nombre de usuario alfanumérico 3-20 caracteres */
    public static boolean validarUsername(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true;
        }
        if (USERNAME_PATTERN.matcher(text).matches()) {
            marcarValido(field);
            return true;
        }
        marcarInvalido(field, "3-20 caracteres alfanuméricos o guión bajo");
        return false;
    }

    /** Valida nombre con caracteres latinos y espacios */
    public static boolean validarNombre(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true;
        }
        if (NOMBRE_PATTERN.matcher(text).matches()) {
            marcarValido(field);
            return true;
        }
        marcarInvalido(field, "Solo letras y espacios (2-50 caracteres)");
        return false;
    }

    /** Valida contraseña mínimo 6 caracteres */
    public static boolean validarPassword(JPasswordField field) {
        String text = new String(field.getPassword()).trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true;
        }
        if (PASSWORD_PATTERN.matcher(text).matches()) {
            marcarValido(field);
            return true;
        }
        marcarInvalido(field, "Mínimo 6 caracteres");
        return false;
    }

    /** Valida que dos contraseñas coincidan */
    public static boolean validarPasswordMatch(JPasswordField field1, JPasswordField field2) {
        String p1 = new String(field1.getPassword());
        String p2 = new String(field2.getPassword());
        if (p2.isEmpty()) {
            marcarNeutral(field2);
            return true;
        }
        if (p1.equals(p2)) {
            marcarValido(field2);
            return true;
        }
        marcarInvalido(field2, "Las contraseñas no coinciden");
        return false;
    }

    /** Valida que un JComboBox tenga selección */
    public static boolean validarComboBox(JComboBox<?> combo, String fieldName) {
        if (combo.getSelectedItem() == null || combo.getSelectedIndex() == -1) {
            combo.setBorder(BORDER_INVALIDO);
            combo.setToolTipText("Seleccione " + fieldName);
            return false;
        }
        combo.setBorder(BORDER_VALIDO);
        combo.setToolTipText(null);
        return true;
    }

    /** Valida un número entero dentro de rango */
    public static boolean validarNumeroRango(JTextField field, int min, int max, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            marcarNeutral(field);
            return true;
        }
        try {
            int valor = Integer.parseInt(text);
            if (valor >= min && valor <= max) {
                marcarValido(field);
                return true;
            }
            marcarInvalido(field, fieldName + " debe estar entre " + min + " y " + max);
            return false;
        } catch (NumberFormatException e) {
            marcarInvalido(field, "Ingrese un número válido");
            return false;
        }
    }

    // Métodos auxiliares de marcado visual
    private static void marcarValido(JComponent field) {
        field.setBorder(BORDER_VALIDO);
        field.setToolTipText("✓ Válido");
    }

    private static void marcarInvalido(JComponent field, String mensaje) {
        field.setBorder(BORDER_INVALIDO);
        field.setToolTipText("✗ " + mensaje);
    }

    private static void marcarNeutral(JComponent field) {
        field.setBorder(BORDER_NEUTRAL);
        field.setToolTipText(null);
    }

    /** Resetea el borde de un componente al estado neutral */
    public static void resetBorder(JComponent field) {
        marcarNeutral(field);
    }
}

package sv.edu.ues.ape115.model;

import java.io.Serializable;

/**
 * Modelo que almacena la configuración de la aplicación:
 * tiempo de sesión, tema visual, y otras preferencias.
 */
public class AppConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Theme {
        METAL("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"),
        SYSTEM("Sistema", null),  // Se resuelve en tiempo de ejecución
        NIMBUS("Nimbus", null),   // Se busca dinámicamente
        MOTIF("Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
        FLAT_LIGHT("FlatLaf Light", "com.formdev.flatlaf.FlatLightLaf"),
        FLAT_DARK("FlatLaf Dark", "com.formdev.flatlaf.FlatDarkLaf");

        private final String displayName;
        private final String className;

        Theme(String displayName, String className) {
            this.displayName = displayName;
            this.className = className;
        }

        public String getDisplayName() { return displayName; }
        public String getClassName() { return className; }

        @Override
        public String toString() { return displayName; }
    }

    private int tiempoSesionMinutos;  // Minutos antes de cierre automático
    private Theme temaSeleccionado;
    private boolean mostrarNotificaciones;
    private int maxContactosPorPagina;

    public AppConfig() {
        this.tiempoSesionMinutos = 15;  // 15 minutos por defecto
        this.temaSeleccionado = Theme.METAL;
        this.mostrarNotificaciones = true;
        this.maxContactosPorPagina = 20;
    }

    // Getters y Setters
    public int getTiempoSesionMinutos() { return tiempoSesionMinutos; }
    public void setTiempoSesionMinutos(int tiempoSesionMinutos) {
        this.tiempoSesionMinutos = tiempoSesionMinutos;
    }

    public Theme getTemaSeleccionado() { return temaSeleccionado; }
    public void setTemaSeleccionado(Theme temaSeleccionado) {
        this.temaSeleccionado = temaSeleccionado;
    }

    public boolean isMostrarNotificaciones() { return mostrarNotificaciones; }
    public void setMostrarNotificaciones(boolean mostrarNotificaciones) {
        this.mostrarNotificaciones = mostrarNotificaciones;
    }

    public int getMaxContactosPorPagina() { return maxContactosPorPagina; }
    public void setMaxContactosPorPagina(int maxContactosPorPagina) {
        this.maxContactosPorPagina = maxContactosPorPagina;
    }
}

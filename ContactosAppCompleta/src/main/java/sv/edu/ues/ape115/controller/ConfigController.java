package sv.edu.ues.ape115.controller;

import sv.edu.ues.ape115.model.AppConfig;
import sv.edu.ues.ape115.model.AppConfig.Theme;
import javax.swing.*;
import java.awt.*;

/**
 * Controlador dedicado para el panel de configuración.
 * Gestiona el tema visual, tiempo de sesión y otras preferencias.
 * Este controlador es independiente y maneja su propia lógica.
 */
public class ConfigController {

    private final AppConfig config;
    private Runnable onThemeChanged;
    private Runnable onSessionTimeChanged;

    public ConfigController(AppConfig config) {
        this.config = config;
    }

    /** Registra callback cuando cambia el tema */
    public void setOnThemeChanged(Runnable callback) {
        this.onThemeChanged = callback;
    }

    /** Registra callback cuando cambia el tiempo de sesión */
    public void setOnSessionTimeChanged(Runnable callback) {
        this.onSessionTimeChanged = callback;
    }

    /** Obtiene la configuración actual */
    public AppConfig getConfig() {
        return config;
    }

    /**
     * Cambia el tiempo de sesión.
     * @return mensaje de error o null si fue exitoso
     */
    public String cambiarTiempoSesion(int minutos) {
        if (minutos < 1 || minutos > 120) {
            return "El tiempo debe estar entre 1 y 120 minutos";
        }
        config.setTiempoSesionMinutos(minutos);
        if (onSessionTimeChanged != null) onSessionTimeChanged.run();
        return null;
    }

    /**
     * Cambia el tema visual de la aplicación.
     * @return mensaje de error o null si fue exitoso
     */
    public String cambiarTema(Theme tema) {
        if (tema == null) return "Debe seleccionar un tema";

        try {
            String className;
            switch (tema) {
                case SYSTEM:
                    className = UIManager.getSystemLookAndFeelClassName();
                    break;
                case NIMBUS:
                    className = null;
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            className = info.getClassName();
                            break;
                        }
                    }
                    if (className == null) return "Nimbus no está disponible en este sistema";
                    break;
                default:
                    className = tema.getClassName();
            }

            if (className != null) {
                UIManager.setLookAndFeel(className);
                // Actualizar todos los frames abiertos
                for (Window window : Window.getWindows()) {
                    SwingUtilities.updateComponentTreeUI(window);
                    if (window instanceof JFrame) {
                        ((JFrame) window).pack();
                    }
                }
            }

            config.setTemaSeleccionado(tema);
            if (onThemeChanged != null) onThemeChanged.run();
            return null;
        } catch (Exception e) {
            return "Error al aplicar el tema: " + e.getMessage();
        }
    }

    /** Cambia la preferencia de notificaciones */
    public void cambiarNotificaciones(boolean mostrar) {
        config.setMostrarNotificaciones(mostrar);
    }

    /** Cambia el máximo de contactos por página */
    public String cambiarMaxContactos(int max) {
        if (max < 5 || max > 100) {
            return "El valor debe estar entre 5 y 100";
        }
        config.setMaxContactosPorPagina(max);
        return null;
    }

    /** Obtiene los temas disponibles */
    public Theme[] getTemasDisponibles() {
        return new Theme[]{Theme.METAL, Theme.SYSTEM, Theme.NIMBUS, Theme.MOTIF};
    }

    /** Obtiene el tema actual */
    public Theme getTemaActual() {
        return config.getTemaSeleccionado();
    }

    /** Obtiene el tiempo de sesión actual */
    public int getTiempoSesion() {
        return config.getTiempoSesionMinutos();
    }
}

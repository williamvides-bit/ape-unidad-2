package sv.edu.ues.ape115.util;

import sv.edu.ues.ape115.model.AppConfig;
import sv.edu.ues.ape115.model.Session;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Temporizador de sesión que monitorea la actividad del usuario.
 * Cierra la sesión automáticamente después de N minutos de inactividad.
 * 
 * Registra actividad mediante:
 * - Movimientos de ratón
 * - Pulsaciones de teclado
 * - Clics de ratón
 */
public class SessionTimer {

    private Timer checkTimer;
    private final AppConfig config;
    private final Runnable onSessionExpired;
    private JFrame frameMonitorizado;

    public SessionTimer(AppConfig config, Runnable onSessionExpired) {
        this.config = config;
        this.onSessionExpired = onSessionExpired;
    }

    /** Inicia el monitoreo de sesión sobre el frame dado */
    public void iniciar(JFrame frame) {
        this.frameMonitorizado = frame;
        
        // Listener global de actividad
        AWTEventListener activityListener = event -> {
            if (Session.getInstance().isActiva()) {
                Session.getInstance().registrarActividad();
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(activityListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);

        // Timer que verifica expiración cada 30 segundos
        checkTimer = new Timer(30_000, e -> verificarExpiracion());
        checkTimer.start();
    }

    /** Detiene el monitoreo */
    public void detener() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
    }

    /** Reinicia el temporizador (al cambiar la configuración) */
    public void reiniciar() {
        detener();
        if (frameMonitorizado != null) {
            iniciar(frameMonitorizado);
        }
    }

    private void verificarExpiracion() {
        Session session = Session.getInstance();
        if (session.isActiva() && session.haExpirado(config.getTiempoSesionMinutos())) {
            detener();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frameMonitorizado,
                        "Su sesión ha expirado por inactividad (" + 
                        config.getTiempoSesionMinutos() + " minutos).\nSe cerrará la sesión.",
                        "Sesión Expirada", JOptionPane.WARNING_MESSAGE);
                session.cerrarSesion();
                onSessionExpired.run();
            });
        }
    }

    /** Obtiene los minutos restantes de la sesión */
    public int getMinutosRestantes() {
        Session session = Session.getInstance();
        if (!session.isActiva() || session.getUltimaActividad() == null) return 0;
        
        java.time.LocalDateTime expira = session.getUltimaActividad()
                .plusMinutes(config.getTiempoSesionMinutos());
        long segundos = java.time.Duration.between(java.time.LocalDateTime.now(), expira).getSeconds();
        return Math.max(0, (int) (segundos / 60));
    }
}

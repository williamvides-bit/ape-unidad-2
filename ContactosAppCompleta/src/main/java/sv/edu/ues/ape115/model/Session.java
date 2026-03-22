package sv.edu.ues.ape115.model;

import java.time.LocalDateTime;

/**
 * Modelo Singleton que gestiona la sesión activa del usuario.
 * Controla el usuario autenticado y el tiempo de inactividad.
 */
public class Session {
    private static Session instance;
    
    private User usuarioActivo;
    private LocalDateTime inicioSesion;
    private LocalDateTime ultimaActividad;
    private boolean activa;

    private Session() {
        this.activa = false;
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /** Inicia sesión para el usuario dado */
    public void iniciarSesion(User usuario) {
        this.usuarioActivo = usuario;
        this.inicioSesion = LocalDateTime.now();
        this.ultimaActividad = LocalDateTime.now();
        this.activa = true;
        usuario.setUltimoAcceso(LocalDateTime.now());
    }

    /** Cierra la sesión activa */
    public void cerrarSesion() {
        this.usuarioActivo = null;
        this.inicioSesion = null;
        this.ultimaActividad = null;
        this.activa = false;
    }

    /** Registra actividad del usuario para reiniciar el temporizador */
    public void registrarActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    /** Verifica si la sesión ha expirado según los minutos configurados */
    public boolean haExpirado(int minutosTimeout) {
        if (!activa || ultimaActividad == null) return true;
        LocalDateTime limite = ultimaActividad.plusMinutes(minutosTimeout);
        return LocalDateTime.now().isAfter(limite);
    }

    // Getters
    public User getUsuarioActivo() { return usuarioActivo; }
    public LocalDateTime getInicioSesion() { return inicioSesion; }
    public LocalDateTime getUltimaActividad() { return ultimaActividad; }
    public boolean isActiva() { return activa; }
}

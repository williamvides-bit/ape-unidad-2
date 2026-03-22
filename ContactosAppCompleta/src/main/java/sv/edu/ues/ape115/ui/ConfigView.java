package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.ConfigController;
import sv.edu.ues.ape115.model.AppConfig;
import sv.edu.ues.ape115.model.AppConfig.Theme;
import sv.edu.ues.ape115.util.Validator;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * Panel de configuración gestionado por su propio controlador (ConfigController).
 * Permite configurar:
 * - Tema visual (Metal, System, Nimbus, Motif)
 * - Tiempo de cierre de sesión (configurable en minutos)
 * - Notificaciones
 * - Contactos por página
 * 
 * Principios UX:
 * - Cambios inmediatos: el tema se aplica al seleccionarlo
 * - Slider + campo numérico para el tiempo (doble entrada)
 * - Feedback inmediato en cada cambio
 * - Agrupación lógica: secciones con bordes titulados
 * - Prevención de errores: rangos válidos claramente indicados
 */
public class ConfigView extends JPanel {

    private final ConfigController controller;
    private JComboBox<Theme> cmbTema;
    private JSlider sliderTiempo;
    private JTextField txtTiempo;
    private JCheckBox chkNotificaciones;
    private JTextField txtMaxContactos;
    private JLabel lblEstado;

    public ConfigView(ConfigController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitle = new JLabel("Configuración del Sistema");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // === Sección: Tema Visual ===
        JPanel themePanel = new JPanel(new GridBagLayout());
        themePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tema Visual"));
        themePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        themePanel.setMaximumSize(new Dimension(600, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        themePanel.add(new JLabel("Seleccione tema:"), gbc);

        cmbTema = new JComboBox<>(controller.getTemasDisponibles());
        cmbTema.setSelectedItem(controller.getTemaActual());
        cmbTema.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        themePanel.add(cmbTema, gbc);

        JLabel lblThemeInfo = new JLabel("El tema se aplicará inmediatamente");
        lblThemeInfo.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblThemeInfo.setForeground(Color.GRAY);
        gbc.gridx = 2;
        themePanel.add(lblThemeInfo, gbc);

        mainPanel.add(themePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // === Sección: Sesión ===
        JPanel sessionPanel = new JPanel(new GridBagLayout());
        sessionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Control de Sesión"));
        sessionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sessionPanel.setMaximumSize(new Dimension(600, 130));

        gbc.gridx = 0; gbc.gridy = 0;
        sessionPanel.add(new JLabel("Tiempo de inactividad (minutos):"), gbc);

        txtTiempo = new JTextField(5);
        txtTiempo.setText(String.valueOf(controller.getTiempoSesion()));
        gbc.gridx = 1;
        sessionPanel.add(txtTiempo, gbc);

        sliderTiempo = new JSlider(1, 120, controller.getTiempoSesion());
        sliderTiempo.setMajorTickSpacing(30);
        sliderTiempo.setMinorTickSpacing(5);
        sliderTiempo.setPaintTicks(true);
        sliderTiempo.setPaintLabels(true);
        sliderTiempo.setPreferredSize(new Dimension(300, 50));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        sessionPanel.add(sliderTiempo, gbc);

        JLabel lblSessionInfo = new JLabel("La sesión se cerrará después de este tiempo sin actividad (1-120 min)");
        lblSessionInfo.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblSessionInfo.setForeground(Color.GRAY);
        gbc.gridy = 2;
        sessionPanel.add(lblSessionInfo, gbc);

        mainPanel.add(sessionPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // === Sección: Otras preferencias ===
        JPanel prefsPanel = new JPanel(new GridBagLayout());
        prefsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Otras Preferencias"));
        prefsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        prefsPanel.setMaximumSize(new Dimension(600, 120));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        prefsPanel.add(new JLabel("Mostrar notificaciones:"), gbc);
        chkNotificaciones = new JCheckBox();
        chkNotificaciones.setSelected(controller.getConfig().isMostrarNotificaciones());
        gbc.gridx = 1;
        prefsPanel.add(chkNotificaciones, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        prefsPanel.add(new JLabel("Contactos por página:"), gbc);
        txtMaxContactos = new JTextField(5);
        txtMaxContactos.setText(String.valueOf(controller.getConfig().getMaxContactosPorPagina()));
        gbc.gridx = 1;
        prefsPanel.add(txtMaxContactos, gbc);
        JLabel lblRange = new JLabel("(5-100)");
        lblRange.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblRange.setForeground(Color.GRAY);
        gbc.gridx = 2;
        prefsPanel.add(lblRange, gbc);

        mainPanel.add(prefsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Estado
        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblEstado);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // === Eventos ===
        configurarEventos();
    }

    private void configurarEventos() {
        // Cambio de tema inmediato
        cmbTema.addActionListener(e -> {
            Theme selected = (Theme) cmbTema.getSelectedItem();
            if (selected != null) {
                String error = controller.cambiarTema(selected);
                if (error != null) {
                    mostrarEstado(error, Color.RED);
                } else {
                    mostrarEstado("Tema cambiado a: " + selected.getDisplayName(), 
                            new Color(46, 139, 87));
                }
            }
        });

        // Sincronización slider <-> campo de texto
        sliderTiempo.addChangeListener(e -> {
            int val = sliderTiempo.getValue();
            txtTiempo.setText(String.valueOf(val));
            if (!sliderTiempo.getValueIsAdjusting()) {
                aplicarTiempoSesion(val);
            }
        });

        txtTiempo.getDocument().addDocumentListener(new DocumentListener() {
            private void sync() {
                if (Validator.validarNumeroRango(txtTiempo, 1, 120, "Tiempo")) {
                    try {
                        int val = Integer.parseInt(txtTiempo.getText().trim());
                        if (val >= 1 && val <= 120) {
                            sliderTiempo.setValue(val);
                            aplicarTiempoSesion(val);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            public void insertUpdate(DocumentEvent e) { sync(); }
            public void removeUpdate(DocumentEvent e) { sync(); }
            public void changedUpdate(DocumentEvent e) { sync(); }
        });

        // Notificaciones
        chkNotificaciones.addActionListener(e -> {
            controller.cambiarNotificaciones(chkNotificaciones.isSelected());
            mostrarEstado("Preferencia de notificaciones actualizada", new Color(46, 139, 87));
        });

        // Max contactos con validación en tiempo real
        txtMaxContactos.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                if (Validator.validarNumeroRango(txtMaxContactos, 5, 100, "Contactos")) {
                    try {
                        int val = Integer.parseInt(txtMaxContactos.getText().trim());
                        String error = controller.cambiarMaxContactos(val);
                        if (error == null) {
                            mostrarEstado("Contactos por página: " + val, new Color(46, 139, 87));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            public void insertUpdate(DocumentEvent e) { validate(); }
            public void removeUpdate(DocumentEvent e) { validate(); }
            public void changedUpdate(DocumentEvent e) { validate(); }
        });
    }

    private void aplicarTiempoSesion(int minutos) {
        String error = controller.cambiarTiempoSesion(minutos);
        if (error == null) {
            mostrarEstado("Tiempo de sesión: " + minutos + " minutos", new Color(46, 139, 87));
        } else {
            mostrarEstado(error, Color.RED);
        }
    }

    private void mostrarEstado(String mensaje, Color color) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(color);
        // Auto-limpiar después de 3 segundos
        Timer timer = new Timer(3000, e -> lblEstado.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}

package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.*;
import sv.edu.ues.ape115.model.Session;
import sv.edu.ues.ape115.util.SessionTimer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Frame principal que contiene el menú, la barra de estado y los paneles internos.
 * Después del login muestra el Dashboard a pantalla completa con accesos directos.
 * 
 * Principios UX:
 * - Navegación principal por menú superior (consistente con aplicaciones de escritorio)
 * - Accesos directos en Dashboard para las 3 funciones principales
 * - Barra de estado con información de sesión
 * - Confirmación al cerrar sesión
 * - Indicador visual del usuario activo
 */
public class MainFrame extends JFrame {

    private final ContactController contactController;
    private final UserController userController;
    private final RoleController roleController;
    private final ConfigController configController;
    private final LoginController loginController;
    private final SessionTimer sessionTimer;

    private JPanel contentPanel;
    private JLabel lblStatusUser, lblStatusTime;
    private Timer statusTimer;

    // Vistas
    private DashboardView dashboardView;
    private ContactView contactView;
    private UserView userView;
    private RoleView roleView;
    private ConfigView configView;

    public MainFrame(ContactController contactController, UserController userController,
                     RoleController roleController, ConfigController configController,
                     LoginController loginController, SessionTimer sessionTimer) {
        this.contactController = contactController;
        this.userController = userController;
        this.roleController = roleController;
        this.configController = configController;
        this.loginController = loginController;
        this.sessionTimer = sessionTimer;
        initComponents();
    }

    private void initComponents() {
        setTitle("APE115 - Sistema de Gestión de Contactos | UES");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 700));

        // Confirmar al cerrar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        // Menú
        setJMenuBar(crearMenuBar());

        // Contenido principal
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Barra de estado
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        
        lblStatusUser = new JLabel();
        lblStatusTime = new JLabel();
        lblStatusUser.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusBar.add(lblStatusUser, BorderLayout.WEST);
        statusBar.add(lblStatusTime, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        actualizarStatusBar();

        // Timer para actualizar barra de estado cada minuto
        statusTimer = new Timer(60_000, e -> actualizarStatusBar());
        statusTimer.start();

        // Mostrar Dashboard
        mostrarDashboard();
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Inicio
        JMenu menuInicio = new JMenu("Inicio");
        menuInicio.setMnemonic(KeyEvent.VK_I);
        
        JMenuItem miDashboard = new JMenuItem("Pantalla Principal", KeyEvent.VK_P);
        miDashboard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0));
        miDashboard.addActionListener(e -> mostrarDashboard());
        menuInicio.add(miDashboard);
        
        menuInicio.addSeparator();
        
        JMenuItem miCerrarSesion = new JMenuItem("Cerrar Sesión", KeyEvent.VK_S);
        miCerrarSesion.addActionListener(e -> cerrarSesion());
        menuInicio.add(miCerrarSesion);
        
        JMenuItem miSalir = new JMenuItem("Salir", KeyEvent.VK_X);
        miSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        miSalir.addActionListener(e -> confirmarSalida());
        menuInicio.add(miSalir);
        
        menuBar.add(menuInicio);

        // Menú Catálogos
        JMenu menuCatalogos = new JMenu("Catálogos");
        menuCatalogos.setMnemonic(KeyEvent.VK_C);

        JMenuItem miContactos = new JMenuItem("Contactos", KeyEvent.VK_O);
        miContactos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
        miContactos.addActionListener(e -> mostrarContactos());
        menuCatalogos.add(miContactos);

        menuCatalogos.addSeparator();

        JMenuItem miUsuarios = new JMenuItem("Usuarios", KeyEvent.VK_U);
        miUsuarios.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
        miUsuarios.addActionListener(e -> mostrarUsuarios());
        menuCatalogos.add(miUsuarios);

        JMenuItem miRoles = new JMenuItem("Roles de Usuario", KeyEvent.VK_R);
        miRoles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK));
        miRoles.addActionListener(e -> mostrarRoles());
        menuCatalogos.add(miRoles);

        menuBar.add(menuCatalogos);

        // Menú Configuración
        JMenu menuConfig = new JMenu("Configuración");
        menuConfig.setMnemonic(KeyEvent.VK_F);

        JMenuItem miConfig = new JMenuItem("Panel de Configuración", KeyEvent.VK_C);
        miConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        miConfig.addActionListener(e -> mostrarConfiguracion());
        menuConfig.add(miConfig);

        menuBar.add(menuConfig);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem miAcerca = new JMenuItem("Acerca de...");
        miAcerca.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(miAcerca);
        menuBar.add(menuAyuda);

        return menuBar;
    }

    // === Métodos de navegación ===

    private void mostrarPanel(JPanel panel, String titulo) {
        contentPanel.removeAll();
        
        // Header con breadcrumb
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        JButton btnHome = new JButton("← Inicio");
        btnHome.setBorderPainted(false);
        btnHome.setContentAreaFilled(false);
        btnHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHome.setForeground(new Color(41, 128, 185));
        btnHome.addActionListener(e -> mostrarDashboard());
        header.add(btnHome);
        header.add(new JLabel(" / " + titulo));
        
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void mostrarDashboard() {
        dashboardView = new DashboardView(
                this::mostrarContactos, this::mostrarConfiguracion, this::mostrarCatalogos);
        contentPanel.removeAll();
        contentPanel.add(dashboardView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void mostrarContactos() {
        contactView = new ContactView(contactController);
        mostrarPanel(contactView, "Catálogo de Contactos");
    }

    public void mostrarUsuarios() {
        userView = new UserView(userController, roleController);
        mostrarPanel(userView, "Catálogo de Usuarios");
    }

    public void mostrarRoles() {
        roleView = new RoleView(roleController);
        roleView.setOnRolesChanged(() -> {
            if (userView != null) userView.cargarRoles();
        });
        mostrarPanel(roleView, "Catálogo de Roles de Usuario");
    }

    public void mostrarConfiguracion() {
        configView = new ConfigView(configController);
        mostrarPanel(configView, "Configuración del Sistema");
    }

    /** Muestra catálogos como panel con tabs */
    public void mostrarCatalogos() {
        JPanel catalogPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        
        userView = new UserView(userController, roleController);
        roleView = new RoleView(roleController);
        roleView.setOnRolesChanged(() -> userView.cargarRoles());
        
        tabbedPane.addTab("Usuarios", userView);
        tabbedPane.addTab("Roles de Usuario", roleView);
        
        catalogPanel.add(tabbedPane, BorderLayout.CENTER);
        mostrarPanel(catalogPanel, "Catálogos Generales");
    }

    private void actualizarStatusBar() {
        if (Session.getInstance().isActiva()) {
            String user = Session.getInstance().getUsuarioActivo().getNombreCompleto();
            String rol = Session.getInstance().getUsuarioActivo().getRol() != null 
                    ? Session.getInstance().getUsuarioActivo().getRol().getNombre() : "";
            lblStatusUser.setText("Usuario: " + user + " | Rol: " + rol);
            int mins = sessionTimer.getMinutosRestantes();
            lblStatusTime.setText("Sesión activa | Tiempo restante: ~" + mins + " min");
        }
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar la sesión actual?", "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            statusTimer.stop();
            sessionTimer.detener();
            loginController.logout();
            dispose();
        }
    }

    private void confirmarSalida() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de salir del sistema?", "Confirmar Salida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            statusTimer.stop();
            sessionTimer.detener();
            loginController.logout();
            System.exit(0);
        }
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestión de Contactos\n" +
                "Materia: Aplicaciones de Escritorio (APE115)\n" +
                "Universidad de El Salvador\n\n" +
                "Versión 1.0\nJava Swing - Sin persistencia (datos en memoria)",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
}

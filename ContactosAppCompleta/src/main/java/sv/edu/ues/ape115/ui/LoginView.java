package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.LoginController;
import sv.edu.ues.ape115.util.Validator;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Interfaz de inicio de sesión.
 * 
 * Principios UX:
 * - Foco automático en campo usuario al abrir
 * - Validación en tiempo real con indicadores visuales
 * - Tecla Enter para enviar formulario
 * - Mensajes de error claros y no intrusivos
 * - Diseño centrado y minimalista para reducir carga cognitiva
 */
public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private final LoginController controller;
    private final Runnable onLoginSuccess;

    public LoginView(LoginController controller, Runnable onLoginSuccess) {
        this.controller = controller;
        this.onLoginSuccess = onLoginSuccess;
        initComponents();
    }

    private void initComponents() {
        setTitle("APE115 - Gestor de Contactos | Inicio de Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitle = new JLabel("Gestor de Contactos", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Universidad de El Salvador - APE115", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(lblSubtitle, gbc);

        // Separador
        gbc.gridy = 2; gbc.insets = new Insets(15, 5, 15, 5);
        mainPanel.add(new JSeparator(), gbc);

        // Usuario
        gbc.gridwidth = 1; gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Usuario:"), gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(txtUsername, gbc);

        // Contraseña
        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(new JLabel("Contraseña:"), gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Error label
        lblError = new JLabel(" ");
        lblError.setForeground(new Color(220, 53, 69));
        lblError.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        mainPanel.add(lblError, gbc);

        // Botón login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(200, 38));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; gbc.insets = new Insets(10, 40, 5, 40);
        mainPanel.add(btnLogin, gbc);

        // Credenciales de ayuda
        JLabel lblHelp = new JLabel("Demo: admin / admin123", SwingConstants.CENTER);
        lblHelp.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblHelp.setForeground(Color.GRAY);
        gbc.gridy = 7; gbc.insets = new Insets(15, 5, 0, 5);
        mainPanel.add(lblHelp, gbc);

        add(mainPanel);

        // === Validación en tiempo real ===
        DocumentListener validationListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validarCampos(); }
            public void removeUpdate(DocumentEvent e) { validarCampos(); }
            public void changedUpdate(DocumentEvent e) { validarCampos(); }
        };
        txtUsername.getDocument().addDocumentListener(validationListener);
        txtPassword.getDocument().addDocumentListener(validationListener);

        // === Eventos ===
        btnLogin.addActionListener(e -> intentarLogin());

        // Enter para enviar
        Action loginAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) { intentarLogin(); }
        };
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> intentarLogin());

        // Foco inicial
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) { txtUsername.requestFocusInWindow(); }
        });
    }

    private void validarCampos() {
        boolean userOk = Validator.validarRequerido(txtUsername, "Usuario");
        boolean passOk = txtPassword.getPassword().length > 0;
        if (!passOk && txtPassword.getPassword().length == 0) {
            Validator.resetBorder(txtPassword);
        }
        lblError.setText(" ");
    }

    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            lblError.setText("Ingrese su nombre de usuario");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            lblError.setText("Ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando...");

        // Simulación de proceso con Timer para UX
        Timer timer = new Timer(500, e -> {
            String error = controller.login(username, password);
            if (error == null) {
                dispose();
                onLoginSuccess.run();
            } else {
                lblError.setText(error);
                txtPassword.setText("");
                txtPassword.requestFocus();
                Validator.resetBorder(txtUsername);
                Validator.resetBorder(txtPassword);
            }
            btnLogin.setEnabled(true);
            btnLogin.setText("Iniciar Sesión");
        });
        timer.setRepeats(false);
        timer.start();
    }
}

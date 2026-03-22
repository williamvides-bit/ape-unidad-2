package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.UserController;
import sv.edu.ues.ape115.controller.RoleController;
import sv.edu.ues.ape115.model.User;
import sv.edu.ues.ape115.model.Role;
import sv.edu.ues.ape115.util.Validator;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista del catálogo de usuarios con CRUD completo.
 * El selector de rol se alimenta del catálogo de roles.
 * 
 * Principios UX:
 * - Validación en tiempo real en todos los campos
 * - Selector de rol alimentado dinámicamente desde catálogo
 * - Confirmación antes de eliminar
 * - Contraseña oculta con opción de mostrar
 * - Búsqueda progresiva
 * - Feedback visual consistente
 */
public class UserView extends JPanel {

    private final UserController userController;
    private final RoleController roleController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar, txtUsername, txtNombreCompleto, txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkMostrarPass, chkActivo;
    private JComboBox<Role> cmbRol;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private User usuarioActual;

    public UserView(UserController userController, RoleController roleController) {
        this.userController = userController;
        this.roleController = roleController;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(25);
        searchPanel.add(txtBuscar);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel actionTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo Usuario");
        actionTop.add(btnNuevo);
        topPanel.add(actionTop, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabla
        String[] columns = {"ID", "Usuario", "Nombre Completo", "Email", "Rol", "Activo"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(60);
        table.setAutoCreateRowSorter(true);

        // Formulario
        JPanel formPanel = crearFormulario();
        formPanel.setBorder(BorderFactory.createTitledBorder("Detalle del Usuario"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(table), new JScrollPane(formPanel));
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // Botones inferiores
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnCancelar = new JButton("Cancelar");
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        bottomPanel.add(btnCancelar);
        bottomPanel.add(btnEliminar);
        bottomPanel.add(btnGuardar);
        add(bottomPanel, BorderLayout.SOUTH);

        configurarEventos();
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Usuario *:"), gbc);
        txtUsername = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(txtUsername, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Contraseña *:"), gbc);
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        txtPassword = new JPasswordField(15);
        chkMostrarPass = new JCheckBox("Mostrar");
        chkMostrarPass.setFont(new Font("SansSerif", Font.PLAIN, 10));
        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(chkMostrarPass, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(passPanel, gbc);
        row++;

        // Nombre completo
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Nombre Completo *:"), gbc);
        txtNombreCompleto = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(txtNombreCompleto, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Email *:"), gbc);
        txtEmail = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(txtEmail, gbc);
        row++;

        // Rol (alimentado desde catálogo de roles)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Rol *:"), gbc);
        cmbRol = new JComboBox<>();
        cargarRoles();
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(cmbRol, gbc);
        row++;

        // Activo
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("Estado:"), gbc);
        chkActivo = new JCheckBox("Activo");
        chkActivo.setSelected(true);
        gbc.gridx = 1;
        form.add(chkActivo, gbc);

        return form;
    }

    public void cargarRoles() {
        cmbRol.removeAllItems();
        for (Role r : roleController.listarActivos()) {
            cmbRol.addItem(r);
        }
    }

    private void configurarEventos() {
        // Búsqueda progresiva
        Timer searchTimer = new Timer(300, e -> cargarDatos());
        searchTimer.setRepeats(false);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
        });

        // Selección tabla
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                String id = (String) tableModel.getValueAt(row, 0);
                userController.obtenerPorId(id).ifPresent(this::mostrarUsuario);
                btnGuardar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });

        // Validación en tiempo real
        txtUsername.getDocument().addDocumentListener(simpleDocListener(() -> 
                Validator.validarUsername(txtUsername)));
        txtPassword.getDocument().addDocumentListener(simpleDocListener(() -> 
                Validator.validarPassword(txtPassword)));
        txtNombreCompleto.getDocument().addDocumentListener(simpleDocListener(() -> 
                Validator.validarNombre(txtNombreCompleto)));
        txtEmail.getDocument().addDocumentListener(simpleDocListener(() -> 
                Validator.validarEmail(txtEmail)));

        // Mostrar/ocultar contraseña
        chkMostrarPass.addActionListener(e -> {
            txtPassword.setEchoChar(chkMostrarPass.isSelected() ? (char) 0 : '•');
        });

        // Botones
        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnCancelar.addActionListener(e -> limpiarFormulario());
    }

    private DocumentListener simpleDocListener(Runnable action) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { action.run(); }
            public void removeUpdate(DocumentEvent e) { action.run(); }
            public void changedUpdate(DocumentEvent e) { action.run(); }
        };
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        String criterio = txtBuscar.getText().trim();
        List<User> users = criterio.isEmpty() ? 
                userController.listarTodos() : userController.buscar(criterio);
        for (User u : users) {
            tableModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getNombreCompleto(),
                    u.getEmail(), u.getRol() != null ? u.getRol().getNombre() : "",
                    u.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void mostrarUsuario(User u) {
        usuarioActual = u;
        txtUsername.setText(u.getUsername());
        txtPassword.setText(u.getPassword());
        txtNombreCompleto.setText(u.getNombreCompleto());
        txtEmail.setText(u.getEmail());
        chkActivo.setSelected(u.isActivo());
        if (u.getRol() != null) {
            for (int i = 0; i < cmbRol.getItemCount(); i++) {
                if (cmbRol.getItemAt(i).getId().equals(u.getRol().getId())) {
                    cmbRol.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void nuevoUsuario() {
        table.clearSelection();
        limpiarFormulario();
        usuarioActual = new User();
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(false);
        txtUsername.requestFocus();
    }

    private void guardarUsuario() {
        boolean valid = true;
        valid &= Validator.validarUsername(txtUsername) && Validator.validarRequerido(txtUsername, "Usuario");
        valid &= Validator.validarPassword(txtPassword);
        valid &= Validator.validarNombre(txtNombreCompleto) && Validator.validarRequerido(txtNombreCompleto, "Nombre");
        valid &= Validator.validarEmail(txtEmail) && Validator.validarRequerido(txtEmail, "Email");
        valid &= Validator.validarComboBox(cmbRol, "un rol");

        if (!valid) {
            JOptionPane.showMessageDialog(this, "Corrija los campos marcados en rojo.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuarioActual == null) usuarioActual = new User();
        usuarioActual.setUsername(txtUsername.getText().trim());
        usuarioActual.setPassword(new String(txtPassword.getPassword()));
        usuarioActual.setNombreCompleto(txtNombreCompleto.getText().trim());
        usuarioActual.setEmail(txtEmail.getText().trim());
        usuarioActual.setRol((Role) cmbRol.getSelectedItem());
        usuarioActual.setActivo(chkActivo.isSelected());

        String error = userController.guardar(usuarioActual);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Usuario guardado exitosamente.");
            cargarDatos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        if (usuarioActual == null || usuarioActual.getId() == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario " + usuarioActual.getUsername() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            userController.eliminar(usuarioActual.getId());
            cargarDatos();
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        usuarioActual = null;
        txtUsername.setText(""); txtPassword.setText("");
        txtNombreCompleto.setText(""); txtEmail.setText("");
        chkActivo.setSelected(true);
        if (cmbRol.getItemCount() > 0) cmbRol.setSelectedIndex(0);
        Validator.resetBorder(txtUsername);
        Validator.resetBorder(txtPassword);
        Validator.resetBorder(txtNombreCompleto);
        Validator.resetBorder(txtEmail);
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        table.clearSelection();
    }
}

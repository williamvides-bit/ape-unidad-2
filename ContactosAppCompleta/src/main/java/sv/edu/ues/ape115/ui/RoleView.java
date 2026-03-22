package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.RoleController;
import sv.edu.ues.ape115.model.Role;
import sv.edu.ues.ape115.util.Validator;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista del catálogo de roles de usuario.
 * Los roles creados aquí alimentan el selector de rol en el formulario de usuarios.
 * 
 * Principios UX:
 * - CRUD completo con validación en tiempo real
 * - Búsqueda progresiva
 * - Confirmación antes de eliminar
 * - Diseño consistente con las demás vistas de catálogo
 */
public class RoleView extends JPanel {

    private final RoleController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar, txtNombre, txtDescripcion;
    private JCheckBox chkActivo;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private Role rolActual;
    private Runnable onRolesChanged;

    public RoleView(RoleController controller) {
        this.controller = controller;
        initComponents();
        cargarDatos();
    }

    /** Callback cuando cambian los roles (para actualizar combos en UserView) */
    public void setOnRolesChanged(Runnable callback) {
        this.onRolesChanged = callback;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(25);
        searchPanel.add(txtBuscar);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel actionTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo Rol");
        actionTop.add(btnNuevo);
        topPanel.add(actionTop, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabla
        String[] columns = {"ID", "Nombre", "Descripción", "Activo"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(60);
        table.setAutoCreateRowSorter(true);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Detalle del Rol"));
        formPanel.setPreferredSize(new Dimension(350, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre *:"), gbc);
        txtNombre = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Descripción *:"), gbc);
        txtDescripcion = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        chkActivo = new JCheckBox("Activo");
        chkActivo.setSelected(true);
        gbc.gridx = 1;
        formPanel.add(chkActivo, gbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(table), formPanel);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // Botones
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

    private void configurarEventos() {
        Timer searchTimer = new Timer(300, e -> cargarDatos());
        searchTimer.setRepeats(false);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                String id = (String) tableModel.getValueAt(row, 0);
                controller.obtenerPorId(id).ifPresent(this::mostrarRol);
                btnGuardar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });

        // Validación en tiempo real
        txtNombre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { Validator.validarRequerido(txtNombre, "Nombre"); }
            public void removeUpdate(DocumentEvent e) { Validator.validarRequerido(txtNombre, "Nombre"); }
            public void changedUpdate(DocumentEvent e) { Validator.validarRequerido(txtNombre, "Nombre"); }
        });
        txtDescripcion.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { Validator.validarRequerido(txtDescripcion, "Descripción"); }
            public void removeUpdate(DocumentEvent e) { Validator.validarRequerido(txtDescripcion, "Descripción"); }
            public void changedUpdate(DocumentEvent e) { Validator.validarRequerido(txtDescripcion, "Descripción"); }
        });

        btnNuevo.addActionListener(e -> nuevoRol());
        btnGuardar.addActionListener(e -> guardarRol());
        btnEliminar.addActionListener(e -> eliminarRol());
        btnCancelar.addActionListener(e -> limpiarFormulario());
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        String criterio = txtBuscar.getText().trim();
        List<Role> roles = criterio.isEmpty() ? controller.listarTodos() : controller.buscar(criterio);
        for (Role r : roles) {
            tableModel.addRow(new Object[]{r.getId(), r.getNombre(), r.getDescripcion(),
                    r.isActivo() ? "Sí" : "No"});
        }
    }

    private void mostrarRol(Role r) {
        rolActual = r;
        txtNombre.setText(r.getNombre());
        txtDescripcion.setText(r.getDescripcion());
        chkActivo.setSelected(r.isActivo());
    }

    private void nuevoRol() {
        table.clearSelection();
        limpiarFormulario();
        rolActual = new Role();
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(false);
        txtNombre.requestFocus();
    }

    private void guardarRol() {
        boolean valid = Validator.validarRequerido(txtNombre, "Nombre") &&
                         Validator.validarRequerido(txtDescripcion, "Descripción");
        if (!valid) {
            JOptionPane.showMessageDialog(this, "Corrija los campos marcados en rojo.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (rolActual == null) rolActual = new Role();
        rolActual.setNombre(txtNombre.getText().trim());
        rolActual.setDescripcion(txtDescripcion.getText().trim());
        rolActual.setActivo(chkActivo.isSelected());

        String error = controller.guardar(rolActual);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Rol guardado exitosamente.");
            cargarDatos();
            limpiarFormulario();
            if (onRolesChanged != null) onRolesChanged.run();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRol() {
        if (rolActual == null || rolActual.getId() == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el rol " + rolActual.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.eliminar(rolActual.getId());
            cargarDatos();
            limpiarFormulario();
            if (onRolesChanged != null) onRolesChanged.run();
        }
    }

    private void limpiarFormulario() {
        rolActual = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        Validator.resetBorder(txtNombre);
        Validator.resetBorder(txtDescripcion);
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        table.clearSelection();
    }
}

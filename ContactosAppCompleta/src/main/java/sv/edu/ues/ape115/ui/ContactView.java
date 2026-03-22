package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.controller.ContactController;
import sv.edu.ues.ape115.model.Contact;
import sv.edu.ues.ape115.util.ImageUtil;
import sv.edu.ues.ape115.util.Validator;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista del catálogo de contactos con tabla, búsqueda, formulario CRUD,
 * importación/exportación vCard e imágenes Base64.
 * 
 * Principios UX:
 * - Búsqueda progresiva: filtra mientras se escribe (debounce)
 * - Formulario con validación en tiempo real y feedback visual
 * - Vista previa de imagen del contacto
 * - Acciones contextuales: botones se habilitan según selección
 * - Layout maestro-detalle: tabla a la izquierda, form a la derecha
 * - Atajos de teclado: Ctrl+N nuevo, Ctrl+S guardar, Del eliminar
 * - Consistencia: misma estructura que otros catálogos
 */
public class ContactView extends JPanel {

    private final ContactController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    
    // Campos del formulario
    private JTextField txtNombre, txtApellido, txtEmail, txtTelefono, txtCelular;
    private JTextField txtDireccion, txtCiudad, txtPais, txtOrganizacion, txtCargo;
    private JTextField txtFechaNac;
    private JTextArea txtNotas;
    private JLabel lblImagen;
    private JButton btnSeleccionarImg, btnEliminarImg;
    private JButton btnNuevo, btnGuardar, btnEliminar, btnCancelar;
    private JButton btnExportar, btnImportar;
    
    private Contact contactoActual;
    private String imagenBase64Temp;
    private String tipoImagenTemp;

    public ContactView(ContactController controller) {
        this.controller = controller;
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Panel superior: búsqueda y acciones ===
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(25);
        txtBuscar.setToolTipText("Buscar por nombre, email, teléfono u organización");
        searchPanel.add(txtBuscar);
        topPanel.add(searchPanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnImportar = new JButton("Importar vCard");
        btnExportar = new JButton("Exportar vCard");
        btnNuevo = new JButton("Nuevo Contacto");
        actionPanel.add(btnImportar);
        actionPanel.add(btnExportar);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(btnNuevo);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // === Panel izquierdo: tabla ===
        String[] columns = {"ID", "Nombre", "Apellido", "Email", "Teléfono", "Organización"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        // Ocultar columna UUID (se usa internamente para identificar el registro)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(650, 400));

        // === Panel derecho: formulario ===
        JPanel formPanel = crearFormulario();
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setPreferredSize(new Dimension(380, 400));
        scrollForm.setBorder(BorderFactory.createTitledBorder("Detalle del Contacto"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTable, scrollForm);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // === Panel inferior: botones de acción ===
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

        // === Eventos ===
        configurarEventos();
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Imagen - JLabel con tamaño fijo, opaco, centrado
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lblImagen = new JLabel("Sin foto", SwingConstants.CENTER);
        lblImagen.setPreferredSize(new Dimension(110, 110));
        lblImagen.setMinimumSize(new Dimension(110, 110));
        lblImagen.setMaximumSize(new Dimension(110, 110));
        lblImagen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setVerticalAlignment(SwingConstants.CENTER);
        lblImagen.setHorizontalTextPosition(SwingConstants.CENTER);
        lblImagen.setVerticalTextPosition(SwingConstants.CENTER);
        lblImagen.setOpaque(true);
        lblImagen.setBackground(Color.WHITE);
        imgPanel.add(lblImagen);
        
        JPanel imgBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnSeleccionarImg = new JButton("Seleccionar");
        btnEliminarImg = new JButton("Quitar");
        btnSeleccionarImg.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnEliminarImg.setFont(new Font("SansSerif", Font.PLAIN, 10));
        imgBtns.add(btnSeleccionarImg);
        imgBtns.add(btnEliminarImg);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(imgPanel, gbc);
        row++;
        gbc.gridy = row;
        form.add(imgBtns, gbc);
        row++;

        gbc.gridwidth = 1;

        // Campos
        txtNombre = addField(form, gbc, row++, "Nombre *:");
        txtApellido = addField(form, gbc, row++, "Apellido *:");
        txtEmail = addField(form, gbc, row++, "Email *:");
        txtTelefono = addField(form, gbc, row++, "Teléfono:");
        txtCelular = addField(form, gbc, row++, "Celular:");
        txtOrganizacion = addField(form, gbc, row++, "Organización:");
        txtCargo = addField(form, gbc, row++, "Cargo:");
        txtDireccion = addField(form, gbc, row++, "Dirección:");
        txtCiudad = addField(form, gbc, row++, "Ciudad:");
        txtPais = addField(form, gbc, row++, "País:");
        txtFechaNac = addField(form, gbc, row++, "Nacimiento:");
        txtFechaNac.setToolTipText("Formato: dd/MM/yyyy");

        // Notas
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Notas:"), gbc);
        txtNotas = new JTextArea(3, 20);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(txtNotas), gbc);

        return form;
    }

    private JTextField addField(JPanel form, GridBagConstraints gbc, int row, String label) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        JTextField field = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1.0;
        form.add(field, gbc);
        return field;
    }

    private void configurarEventos() {
        // Búsqueda progresiva con debounce
        Timer searchTimer = new Timer(300, e -> cargarDatos());
        searchTimer.setRepeats(false);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
        });

        // Selección en tabla
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                String id = (String) tableModel.getValueAt(row, 0);
                controller.obtenerPorId(id).ifPresent(this::mostrarContacto);
                btnGuardar.setEnabled(true);
                btnEliminar.setEnabled(true);
            }
        });

        // Validación en tiempo real
        addRealTimeValidation(txtNombre, "nombre");
        addRealTimeValidation(txtApellido, "apellido");
        addRealTimeValidation(txtEmail, "email");
        addRealTimeValidation(txtTelefono, "telefono");
        addRealTimeValidation(txtCelular, "celular");

        // Botones
        btnNuevo.addActionListener(e -> nuevoContacto());
        btnGuardar.addActionListener(e -> guardarContacto());
        btnEliminar.addActionListener(e -> eliminarContacto());
        btnCancelar.addActionListener(e -> limpiarFormulario());
        btnExportar.addActionListener(e -> exportarVCard());
        btnImportar.addActionListener(e -> importarVCard());

        // Imagen
        btnSeleccionarImg.addActionListener(e -> seleccionarImagen());
        btnEliminarImg.addActionListener(e -> {
            imagenBase64Temp = null;
            tipoImagenTemp = null;
            lblImagen.setIcon(null);
            lblImagen.setText("Sin foto");
            lblImagen.revalidate();
            lblImagen.repaint();
        });

        // Atajos de teclado
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "nuevo");
        getActionMap().put("nuevo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { nuevoContacto(); }
        });
    }

    private void addRealTimeValidation(JTextField field, String type) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                switch (type) {
                    case "nombre": case "apellido":
                        Validator.validarNombre(field); break;
                    case "email":
                        Validator.validarEmail(field); break;
                    case "telefono": case "celular":
                        Validator.validarTelefono(field); break;
                }
            }
            public void insertUpdate(DocumentEvent e) { validate(); }
            public void removeUpdate(DocumentEvent e) { validate(); }
            public void changedUpdate(DocumentEvent e) { validate(); }
        });
    }

    public void cargarDatos() {
        tableModel.setRowCount(0);
        String criterio = txtBuscar.getText().trim();
        List<Contact> contacts = criterio.isEmpty() ? 
                controller.listarTodos() : controller.buscar(criterio);
        
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellido(),
                    c.getEmail(), c.getTelefono(), c.getOrganizacion()
            });
        }
    }

    private void mostrarContacto(Contact c) {
        contactoActual = c;
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtEmail.setText(c.getEmail());
        txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
        txtCelular.setText(c.getCelular() != null ? c.getCelular() : "");
        txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
        txtCiudad.setText(c.getCiudad() != null ? c.getCiudad() : "");
        txtPais.setText(c.getPais() != null ? c.getPais() : "");
        txtOrganizacion.setText(c.getOrganizacion() != null ? c.getOrganizacion() : "");
        txtCargo.setText(c.getCargo() != null ? c.getCargo() : "");
        txtNotas.setText(c.getNotas() != null ? c.getNotas() : "");
        
        if (c.getFechaNacimiento() != null) {
            txtFechaNac.setText(c.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            txtFechaNac.setText("");
        }

        // Cargar imagen Base64 del contacto o generar avatar por defecto
        imagenBase64Temp = c.getImagenBase64();
        tipoImagenTemp = c.getTipoImagen();
        
        actualizarPreviewImagen(c.getNombre(), c.getApellido());
    }
    
    /** Actualiza la vista previa de la imagen en el formulario */
    private void actualizarPreviewImagen(String nombre, String apellido) {
        if (imagenBase64Temp != null && !imagenBase64Temp.trim().isEmpty()) {
            ImageIcon icon = ImageUtil.base64ToIcon(imagenBase64Temp, 100, 100);
            if (icon != null) {
                lblImagen.setIcon(icon);
                lblImagen.setText(null);
            } else {
                // Base64 corrupto: mostrar avatar por defecto
                lblImagen.setIcon(ImageUtil.generarAvatarDefault(
                        nombre != null ? nombre : "", 
                        apellido != null ? apellido : "", 100));
                lblImagen.setText(null);
            }
        } else {
            // Sin imagen: avatar con iniciales
            lblImagen.setIcon(ImageUtil.generarAvatarDefault(
                    nombre != null ? nombre : "", 
                    apellido != null ? apellido : "", 100));
            lblImagen.setText(null);
        }
        lblImagen.revalidate();
        lblImagen.repaint();
    }

    private void nuevoContacto() {
        table.clearSelection();
        limpiarFormulario();
        contactoActual = new Contact();
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(false);
        txtNombre.requestFocus();
    }

    private void guardarContacto() {
        // Validar campos requeridos
        boolean valid = true;
        valid &= Validator.validarNombre(txtNombre) && Validator.validarRequerido(txtNombre, "Nombre");
        valid &= Validator.validarNombre(txtApellido) && Validator.validarRequerido(txtApellido, "Apellido");
        valid &= Validator.validarEmail(txtEmail) && Validator.validarRequerido(txtEmail, "Email");
        valid &= Validator.validarTelefono(txtTelefono);
        valid &= Validator.validarTelefono(txtCelular);

        if (!valid) {
            JOptionPane.showMessageDialog(this, 
                    "Corrija los campos marcados en rojo antes de guardar.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (contactoActual == null) contactoActual = new Contact();
        contactoActual.setNombre(txtNombre.getText().trim());
        contactoActual.setApellido(txtApellido.getText().trim());
        contactoActual.setEmail(txtEmail.getText().trim());
        contactoActual.setTelefono(txtTelefono.getText().trim());
        contactoActual.setCelular(txtCelular.getText().trim());
        contactoActual.setDireccion(txtDireccion.getText().trim());
        contactoActual.setCiudad(txtCiudad.getText().trim());
        contactoActual.setPais(txtPais.getText().trim());
        contactoActual.setOrganizacion(txtOrganizacion.getText().trim());
        contactoActual.setCargo(txtCargo.getText().trim());
        contactoActual.setNotas(txtNotas.getText().trim());
        contactoActual.setImagenBase64(imagenBase64Temp);
        contactoActual.setTipoImagen(tipoImagenTemp);

        // Fecha de nacimiento
        String fechaStr = txtFechaNac.getText().trim();
        if (!fechaStr.isEmpty()) {
            try {
                contactoActual.setFechaNacimiento(
                        LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Use dd/MM/yyyy",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String error = controller.guardar(contactoActual);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Contacto guardado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarContacto() {
        if (contactoActual == null || contactoActual.getId() == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar a " + contactoActual.getNombreCompleto() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminar(contactoActual.getId())) {
                cargarDatos();
                limpiarFormulario();
            }
        }
    }

    private void limpiarFormulario() {
        contactoActual = null;
        JTextField[] fields = {txtNombre, txtApellido, txtEmail, txtTelefono, txtCelular,
                txtDireccion, txtCiudad, txtPais, txtOrganizacion, txtCargo, txtFechaNac};
        for (JTextField f : fields) {
            f.setText("");
            Validator.resetBorder(f);
        }
        txtNotas.setText("");
        imagenBase64Temp = null;
        tipoImagenTemp = null;
        lblImagen.setIcon(null);
        lblImagen.setText("Sin foto");
        lblImagen.revalidate();
        lblImagen.repaint();
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        table.clearSelection();
    }

    private void seleccionarImagen() {
        String[] result = ImageUtil.seleccionarImagen(this);
        if (result != null && result[0] != null && !result[0].isEmpty()) {
            imagenBase64Temp = result[0];
            tipoImagenTemp = result[1];
            
            System.out.println("[ContactView] Imagen seleccionada, base64 length: " + imagenBase64Temp.length());
            
            ImageIcon icon = ImageUtil.base64ToIcon(imagenBase64Temp, 100, 100);
            if (icon != null) {
                lblImagen.setIcon(icon);
                lblImagen.setText(null);  // null para que no ocupe espacio
                System.out.println("[ContactView] Preview actualizado con icon " 
                        + icon.getIconWidth() + "x" + icon.getIconHeight());
            } else {
                lblImagen.setIcon(null);
                lblImagen.setText("Error al cargar");
                System.err.println("[ContactView] base64ToIcon retorno null");
            }
            lblImagen.revalidate();
            lblImagen.repaint();
        }
    }

    private void exportarVCard() {
        List<Contact> contacts = controller.listarTodos();
        if (contacts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay contactos para exportar.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("contactos.vcf"));
        chooser.setFileFilter(new FileNameExtensionFilter("vCard (*.vcf)", "vcf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".vcf")) file = new File(file.getPath() + ".vcf");
            String error = controller.exportarVCard(contacts, file);
            if (error == null) {
                JOptionPane.showMessageDialog(this, 
                        contacts.size() + " contactos exportados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarVCard() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("vCard (*.vcf)", "vcf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String error = controller.importarVCard(chooser.getSelectedFile());
            if (error == null) {
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Contactos importados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

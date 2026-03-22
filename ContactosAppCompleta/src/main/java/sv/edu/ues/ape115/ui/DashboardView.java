package sv.edu.ues.ape115.ui;

import sv.edu.ues.ape115.model.Session;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Pantalla principal (Dashboard) que se presenta después del login.
 * Cubre toda la pantalla y presenta 3 iconos de acceso directo:
 * - Contactos
 * - Configuraciones
 * - Catálogos
 * 
 * Principios UX:
 * - Ley de Fitts: iconos grandes y fáciles de alcanzar
 * - Reconocimiento sobre recuerdo: iconos con etiquetas claras
 * - Diseño centrado: reduce tiempo de búsqueda visual
 * - Feedback hover: cambio visual al pasar el ratón
 * - Minimalismo: solo las opciones esenciales visibles
 */
public class DashboardView extends JPanel {

    private final Runnable onContactos;
    private final Runnable onConfiguracion;
    private final Runnable onCatalogos;

    public DashboardView(Runnable onContactos, Runnable onConfiguracion, Runnable onCatalogos) {
        this.onContactos = onContactos;
        this.onConfiguracion = onConfiguracion;
        this.onCatalogos = onCatalogos;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior con bienvenida
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));
        topPanel.setOpaque(false);

        String nombreUsuario = Session.getInstance().getUsuarioActivo() != null 
                ? Session.getInstance().getUsuarioActivo().getNombreCompleto() : "Usuario";

        JLabel lblBienvenida = new JLabel("Bienvenido, " + nombreUsuario);
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 28));
        topPanel.add(lblBienvenida, BorderLayout.WEST);

        JLabel lblSistema = new JLabel("Sistema de Gestión de Contactos - APE115");
        lblSistema.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSistema.setForeground(Color.GRAY);
        topPanel.add(lblSistema, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Panel central con los 3 accesos directos
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        cardsPanel.setOpaque(false);

        cardsPanel.add(crearAccesoDirecto("Contactos", 
                "Gestionar catálogo de contactos", 
                new Color(41, 128, 185), "☎", onContactos));

        cardsPanel.add(crearAccesoDirecto("Configuración", 
                "Tema, sesión y preferencias", 
                new Color(142, 68, 173), "⚙", onConfiguracion));

        cardsPanel.add(crearAccesoDirecto("Catálogos", 
                "Usuarios, roles y más", 
                new Color(39, 174, 96), "📋", onCatalogos));

        centerPanel.add(cardsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con info
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottomPanel.setOpaque(false);

        JLabel lblInfo = new JLabel("Seleccione una opción para comenzar | Use el menú superior para más opciones");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea un panel de acceso directo estilo tarjeta con icono, título y descripción.
     */
    private JPanel crearAccesoDirecto(String titulo, String descripcion, 
                                       Color color, String iconText, Runnable accion) {
        JPanel card = new JPanel() {
            private boolean hover = false;

            {
                setPreferredSize(new Dimension(200, 220));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.brighter(), 2, true),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)));
                setBackground(Color.WHITE);

                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(color, 3, true),
                                BorderFactory.createEmptyBorder(19, 14, 19, 14)));
                        setBackground(new Color(color.getRed(), color.getGreen(), 
                                color.getBlue(), 15));
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(color.brighter(), 2, true),
                                BorderFactory.createEmptyBorder(20, 15, 20, 15)));
                        setBackground(Color.WHITE);
                        repaint();
                    }
                    public void mouseClicked(MouseEvent e) {
                        accion.run();
                    }
                });
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Ícono grande
        JLabel lblIcon = new JLabel(iconText, SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 60));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblIcon);

        card.add(Box.createVerticalStrut(15));

        // Título
        JLabel lblTitle = new JLabel(titulo, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(color.darker());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);

        card.add(Box.createVerticalStrut(8));

        // Descripción
        JLabel lblDesc = new JLabel("<html><center>" + descripcion + "</center></html>", 
                SwingConstants.CENTER);
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblDesc);

        return card;
    }
}

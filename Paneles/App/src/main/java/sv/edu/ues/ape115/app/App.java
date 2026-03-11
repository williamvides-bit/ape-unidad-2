/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.ape115.app;
import javax.swing.*;
import java.awt.*;

public class App extends JPanel {

    public App() {
        setLayout(new BorderLayout());

        // PANEL PRINCIPAL QUE CONTIENE LOS SUBPANELES
        JPanel panelCentral = new JPanel(new GridLayout(3, 1, 10, 10));

        // ------------ PANEL 1: DATOS PERSONALES -------------
        JPanel panelDatosPersonales = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDatosPersonales.setBorder(BorderFactory.createTitledBorder("Datos Personales"));

        panelDatosPersonales.add(new JLabel("Nombre:"));
        panelDatosPersonales.add(new JTextField());

        panelDatosPersonales.add(new JLabel("Apellido:"));
        panelDatosPersonales.add(new JTextField());

        panelDatosPersonales.add(new JLabel("Fecha de nacimiento:"));
        panelDatosPersonales.add(new JTextField("DD/MM/AAAA"));

        // ------------ PANEL 2: DATOS DE CUENTA -------------
        JPanel panelDatosCuenta = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDatosCuenta.setBorder(BorderFactory.createTitledBorder("Datos de Cuenta"));

        panelDatosCuenta.add(new JLabel("Usuario:"));
        panelDatosCuenta.add(new JTextField());

        panelDatosCuenta.add(new JLabel("Contraseña:"));
        panelDatosCuenta.add(new JPasswordField());

        panelDatosCuenta.add(new JLabel("Confirmar contraseña:"));
        panelDatosCuenta.add(new JPasswordField());

        // ------------ PANEL 3: CONTACTO -------------
        JPanel panelContacto = new JPanel(new GridLayout(2, 2, 5, 5));
        panelContacto.setBorder(BorderFactory.createTitledBorder("Información de Contacto"));

        panelContacto.add(new JLabel("Correo electrónico:"));
        panelContacto.add(new JTextField());

        panelContacto.add(new JLabel("Número de teléfono:"));
        panelContacto.add(new JTextField());

        // Añadir subpaneles al panel central
        panelCentral.add(panelDatosPersonales);
        panelCentral.add(panelDatosCuenta);
        panelCentral.add(panelContacto);

        // ------------ PANEL 4: BOTONES -------------
        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);

        // Agregar a la vista principal
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // ---------- EJECUTAR APLICACIÓN ----------
    public static void main(String[] args) {
        JFrame ventana = new JFrame("Registro de Usuario");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(450, 500);
        ventana.setLocationRelativeTo(null);
        ventana.add(new App()); // Se usa la clase App
        ventana.setVisible(true);
    }
}
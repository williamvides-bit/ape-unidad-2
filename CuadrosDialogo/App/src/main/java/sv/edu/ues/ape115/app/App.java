
package sv.edu.ues.ape115.app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame implements ActionListener {

    private JButton btnMessage, btnConfirm, btnInput, btnOption;

    public App() {
        super("Ejemplo Cuadros de Dialogo");

        // Crear botones
        btnMessage = new JButton("showMessageDialog");
        btnConfirm = new JButton("showConfirmDialog");
        btnInput = new JButton("showInputDialog");
        btnOption = new JButton("showOptionDialog");

        // Registrar listeners
        btnMessage.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnInput.addActionListener(this);
        btnOption.addActionListener(this);

        // Layout
        setLayout(new GridLayout(4, 1, 10, 10));
        add(btnMessage);
        add(btnConfirm);
        add(btnInput);
        add(btnOption);

        // Configuración básica
        setSize(300, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnMessage) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este es un mensaje simple.",
                    "showMessageDialog",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        if (e.getSource() == btnConfirm) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas continuar?",
                    "showConfirmDialog",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            System.out.println("Respuesta: " + res);
        }

        if (e.getSource() == btnInput) {
            String nombre = JOptionPane.showInputDialog(
                    this,
                    "¿Cuál es tu nombre?",
                    "showInputDialog",
                    JOptionPane.QUESTION_MESSAGE
            );

            System.out.println("Nombre ingresado: " + nombre);
        }

        if (e.getSource() == btnOption) {
            String[] opciones = {"Rojo", "Verde", "Azul"};

            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Elige un color:",
                    "showOptionDialog",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            System.out.println("Opción seleccionada: " + seleccion);
        }
    }

    public static void main(String[] args) {
        new App();
    }
}
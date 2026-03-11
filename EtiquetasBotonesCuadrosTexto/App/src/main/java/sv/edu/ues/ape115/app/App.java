/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.ape115.app;
import javax.swing.*;

/**
 * Etiquetas, Botones y Cuadros de Texto
 */
public class App {

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Ejemplo Swing");

        JLabel etiqueta = new JLabel("Nombre:");
        JTextField campo = new JTextField(15);
        JButton boton = new JButton("Saludar");
        boton.addActionListener(e -> {
            String nombre = campo.getText();
            JOptionPane.showMessageDialog(null, "Hola " + nombre);
        });

        JPanel panel = new JPanel();

        panel.add(etiqueta);
        panel.add(campo);
        panel.add(boton);

        ventana.add(panel);

        ventana.setSize(300,150);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}

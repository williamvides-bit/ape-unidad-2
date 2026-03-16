package ejercicios;
import javax.swing.*;
import java.awt.*;

public class EjercicioContador extends JFrame {
    private JButton btnContar;
    private JButton btnReiniciar;
    private JLabel lblCuenta;
    private int contador = 0;

    public EjercicioContador() {
        setLayout(new FlowLayout());
        btnContar = new JButton("Contar");
        btnReiniciar = new JButton("Reiniciar");
        lblCuenta = new JLabel("Clics: 0");

        btnContar.addActionListener(e -> {
            contador++;
            lblCuenta.setText("Clics: " + contador);
        });

        btnReiniciar.addActionListener(e -> {
            contador = 0;
            lblCuenta.setText("Clics: " + contador);
        });

        add(btnContar);
        add(btnReiniciar);
        add(lblCuenta);

        setSize(250, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EjercicioContador().setVisible(true));
    }
}
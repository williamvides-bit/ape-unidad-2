package ejercicios;
import javax.swing.*;
import java.awt.*;

public class EjercicioSuma extends JFrame {
    private JTextField txtNum1;
    private JTextField txtNum2;
    private JButton btnSumar;
    private JLabel lblResultado;

    public EjercicioSuma() {
        setLayout(new FlowLayout());
        txtNum1 = new JTextField(10);
        txtNum2 = new JTextField(10);
        btnSumar = new JButton("Sumar");
        lblResultado = new JLabel("Resultado: ");

        btnSumar.addActionListener(e -> {
            try {
                double n1 = Double.parseDouble(txtNum1.getText());
                double n2 = Double.parseDouble(txtNum2.getText());
                lblResultado.setText("Resultado: " + (n1 + n2));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingrese solo números", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(new JLabel("Número 1:"));
        add(txtNum1);
        add(new JLabel("Número 2:"));
        add(txtNum2);
        add(btnSumar);
        add(lblResultado);

        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EjercicioSuma().setVisible(true));
    }
}
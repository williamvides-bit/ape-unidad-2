/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.ape115.app;
import javax.swing.*;

/**
 *
 * @author adm0n
 */
public class App {

    public static void main(String[] args) {

        JFrame ventana = new JFrame("Ejemplo Controles y listas");
        
        JCheckBox aceptar = new JCheckBox("Aceptar términos");
        JRadioButton masculino = new JRadioButton("Masculino");
        JRadioButton femenino = new JRadioButton("Femenino");
        
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(masculino);
        grupo.add(femenino);
        
        String[] paises = {"Belice: Belmopán", "Costa Rica: San José", "El Salvador: San Salvador", 
            "Guatemala: Ciudad de Guatemala", "Honduras: Tegucigalpa", "Nicaragua: Managua",
            "Panamá: Ciudad de Panamá "};
        JList lista = new JList(paises);
        JScrollPane scroll = new JScrollPane(lista);

        JPanel panel = new JPanel();

        panel.add(masculino);
        panel.add(femenino);
        panel.add(aceptar);
        panel.add(scroll);

        ventana.add(panel);

        ventana.setSize(300,150);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}

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
        JFrame ventana = new JFrame("Ejemplo Menu");

        ventana.setJMenuBar(crearMenu());

        //ventana.add(panel);

        ventana.setSize(300,150);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
    
    private static JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        // --- Menú Archivo ---
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem nuevo = new JMenuItem("Nuevo");
        JMenuItem abrir = new JMenuItem("Abrir...");
        JMenuItem guardar = new JMenuItem("Guardar...");
        JMenuItem salir = new JMenuItem("Salir");
/*
        oculto para futuros ejercicios
        nuevo.addActionListener(e -> areaTexto.setText(""));
        abrir.addActionListener(e -> abrirArchivo());
        guardar.addActionListener(e -> guardarArchivo());
        salir.addActionListener(e -> System.exit(0));
*/
        menuArchivo.add(nuevo);
        menuArchivo.add(abrir);
        menuArchivo.add(guardar);
        menuArchivo.addSeparator();
        menuArchivo.add(salir);

        // --- Menú Edición ---
        JMenu menuEdicion = new JMenu("Edición");

        JMenuItem copiar = new JMenuItem("Copiar");
        JMenuItem cortar = new JMenuItem("Cortar");
        JMenuItem pegar = new JMenuItem("Pegar");

        menuEdicion.add(copiar);
        menuEdicion.add(cortar);
        menuEdicion.add(pegar);

        // Agregar menús a la barra
        barra.add(menuArchivo);
        barra.add(menuEdicion);

        return barra;
    }
}

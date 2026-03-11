/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.ape115.app;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.tree.DefaultMutableTreeNode;
// Se agrega la librería org.json usando el gestor maven adhiriendo la dependencia en el archivo pom.xml
import org.json.*;          

public class Main extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    public Main() {
        setTitle("Personajes de Star Wars (SWAPI)");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] columnas = {"Nombre", "Edad", "Especie", "Planeta"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Sistema");
        DefaultMutableTreeNode doc = new DefaultMutableTreeNode("Documentos");
        DefaultMutableTreeNode img = new DefaultMutableTreeNode("Imágenes");
        
        raiz.add(doc);
        raiz.add(img);
        JTree arbol = new JTree(raiz);

        add(new JScrollPane(tabla));
        //add(arbol);
        setLocationRelativeTo(null);

        cargarPersonajes();

        setVisible(true);
    }

    private void cargarPersonajes() {
        try {
            String json = httpGet("https://swapi.py4e.com/api/people/");
            JSONObject root = new JSONObject(json);
            JSONArray results = root.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject p = results.getJSONObject(i);

                String nombre = p.getString("name");
                String edad = p.optString("birth_year", "N/A");

                // Obtener planeta
                String planetaUrl = p.getString("homeworld");
                JSONObject planetaObj = new JSONObject(httpGet(planetaUrl));
                String planeta = planetaObj.getString("name");

                // Obtener raza (species)
                String raza = "Humano"; // por defecto
                JSONArray speciesArr = p.getJSONArray("species");
                if (speciesArr.length() > 0) {
                    String especieUrl = speciesArr.getString(0);
                    JSONObject especieObj = new JSONObject(httpGet(especieUrl));
                    raza = especieObj.getString("name");
                }

                modelo.addRow(new Object[]{nombre, edad, raza, planeta});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error consultando SWAPI");
        }
    }

    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
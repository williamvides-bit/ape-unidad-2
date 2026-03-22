/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.ape115.app;

/**
 *
 * @author adm0n
 */

import sv.edu.ues.ape115.controller.*;
import sv.edu.ues.ape115.dao.*;
import sv.edu.ues.ape115.model.AppConfig;
import sv.edu.ues.ape115.ui.LoginView;
import sv.edu.ues.ape115.ui.MainFrame;
import sv.edu.ues.ape115.util.SessionTimer;
import javax.swing.*;

/**
 * Clase principal de la aplicación.
 * Inicializa los DAOs, controladores, y lanza la interfaz de login.
 * 
 * Flujo de la aplicación:
 * 1. Se crean los DAOs (datos en memoria, sin persistencia)
 * 2. Se crean los controladores
 * 3. Se muestra el LoginView
 * 4. Al autenticarse, se muestra el MainFrame con el Dashboard
 * 5. El SessionTimer monitorea la inactividad
 * 6. Al expirar la sesión, se vuelve al LoginView
 */
public class App {

    // DAOs (capa de datos en memoria)
    private static RoleDAO roleDAO;
    private static UserDAO userDAO;
    private static ContactDAO contactDAO;

    // Controladores
    private static LoginController loginController;
    private static ContactController contactController;
    private static UserController userController;
    private static RoleController roleController;
    private static ConfigController configController;

    // Configuración y sesión
    private static AppConfig appConfig;
    private static SessionTimer sessionTimer;

    public static void main(String[] args) {
        // Aplicar Look and Feel inicial
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            inicializarCapas();
            mostrarLogin();
        });
    }

    /** Inicializa DAOs, configuración y controladores */
    private static void inicializarCapas() {
        // DAOs
        roleDAO = new RoleDAO();
        userDAO = new UserDAO(roleDAO);
        contactDAO = new ContactDAO();

        // Configuración
        appConfig = new AppConfig();

        // Controladores
        loginController = new LoginController(userDAO);
        contactController = new ContactController(contactDAO);
        userController = new UserController(userDAO);
        roleController = new RoleController(roleDAO);
        configController = new ConfigController(appConfig);

        // Session timer
        sessionTimer = new SessionTimer(appConfig, App::onSessionExpired);

        // Callback: cuando cambie el tiempo de sesión, reiniciar timer
        configController.setOnSessionTimeChanged(sessionTimer::reiniciar);
    }

    /** Muestra la ventana de login */
    private static void mostrarLogin() {
        LoginView loginView = new LoginView(loginController, App::onLoginSuccess);
        loginView.setVisible(true);
    }

    /** Callback exitoso de login: muestra el frame principal */
    private static void onLoginSuccess() {
        MainFrame mainFrame = new MainFrame(
                contactController, userController, roleController,
                configController, loginController, sessionTimer);
        mainFrame.setVisible(true);

        // Iniciar monitoreo de sesión
        sessionTimer.iniciar(mainFrame);
    }

    /** Callback de sesión expirada: vuelve al login */
    private static void onSessionExpired() {
        // Cerrar todas las ventanas
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            window.dispose();
        }
        // Mostrar login nuevamente
        mostrarLogin();
    }
}
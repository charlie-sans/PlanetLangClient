/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.finite.planetlangclient;

/**
 *  
 * @author GAMER
 */
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.finite.planetlangclient.ui.ThemeManager;
import org.finite.planetlangclient.ui.ModernMainWindow;

public class PlanetLangClient {

    public static void main(String[] args) {
        // Initialize modern theme system
        SwingUtilities.invokeLater(() -> {
            ThemeManager.initialize();
            
            // Create and show the modern main window
            ModernMainWindow mainWindow = new ModernMainWindow();
            mainWindow.setVisible(true);
        });
    }
}

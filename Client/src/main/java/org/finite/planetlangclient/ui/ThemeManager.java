package org.finite.planetlangclient.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Modern theme manager for PlanetLang Client UI
 */
public class ThemeManager {
    private static final String THEME_PREFERENCE_KEY = "theme";
    private static final String DEFAULT_THEME = "dark";
    private static Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    public enum Theme {
        LIGHT("Light", FlatLightLaf.class),
        DARK("Dark", FlatDarkLaf.class);
        
        private final String displayName;
        private final Class<? extends LookAndFeel> lafClass;
        
        Theme(String displayName, Class<? extends LookAndFeel> lafClass) {
            this.displayName = displayName;
            this.lafClass = lafClass;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Class<? extends LookAndFeel> getLafClass() {
            return lafClass;
        }
    }
    
    /**
     * Initialize the theme system and apply the saved theme
     */
    public static void initialize() {
        // Set system properties for better UI experience
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        
        // Load and apply saved theme
        String savedTheme = prefs.get(THEME_PREFERENCE_KEY, DEFAULT_THEME);
        Theme theme = Theme.valueOf(savedTheme.toUpperCase());
        applyTheme(theme, false);
    }
    
    /**
     * Apply the specified theme
     */
    public static void applyTheme(Theme theme, boolean animate) {
        try {
            if (animate) {
                FlatAnimatedLafChange.showSnapshot();
            }
            
            UIManager.setLookAndFeel(theme.getLafClass().getDeclaredConstructor().newInstance());
            
            // Custom UI properties for modern appearance
            setupCustomProperties();
            
            if (animate) {
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            }
            
            // Update all windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            
            // Save theme preference
            prefs.put(THEME_PREFERENCE_KEY, theme.name().toLowerCase());
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the current theme
     */
    public static Theme getCurrentTheme() {
        String currentTheme = prefs.get(THEME_PREFERENCE_KEY, DEFAULT_THEME);
        return Theme.valueOf(currentTheme.toUpperCase());
    }
    
    /**
     * Setup custom UI properties for modern appearance
     */
    private static void setupCustomProperties() {
        // Button styling
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        
        // Panel styling
        UIManager.put("Panel.arc", 8);
        
        // TextField styling
        UIManager.put("TextField.arc", 6);
        UIManager.put("TextArea.arc", 6);
        
        // ScrollPane styling
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 10);
        
        // Table styling
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        
        // Menu styling
        UIManager.put("MenuBar.borderColor", Color.LIGHT_GRAY);
        
        // Custom colors for PlanetLang theme
        if (getCurrentTheme() == Theme.DARK) {
            setupDarkThemeColors();
        } else {
            setupLightThemeColors();
        }
    }
    
    private static void setupDarkThemeColors() {
        // Accent colors for dark theme
        UIManager.put("Component.accentColor", new Color(0, 123, 255));
        UIManager.put("Component.borderColor", new Color(70, 70, 70));
        UIManager.put("Panel.background", new Color(43, 43, 43));
        UIManager.put("TextField.background", new Color(60, 60, 60));
    }
    
    private static void setupLightThemeColors() {
        // Accent colors for light theme
        UIManager.put("Component.accentColor", new Color(0, 123, 255));
        UIManager.put("Component.borderColor", new Color(220, 220, 220));
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
    }
    
    /**
     * Create a theme switcher component
     */
    public static JComboBox<Theme> createThemeSwitcher() {
        JComboBox<Theme> themeSwitcher = new JComboBox<>(Theme.values());
        themeSwitcher.setSelectedItem(getCurrentTheme());
        themeSwitcher.addActionListener(e -> {
            Theme selectedTheme = (Theme) themeSwitcher.getSelectedItem();
            if (selectedTheme != null && selectedTheme != getCurrentTheme()) {
                applyTheme(selectedTheme, true);
            }
        });
        return themeSwitcher;
    }
}
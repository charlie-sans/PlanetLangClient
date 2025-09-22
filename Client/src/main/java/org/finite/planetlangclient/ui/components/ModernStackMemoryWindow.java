package org.finite.planetlangclient.ui.components;

import org.finite.planetlangclient.ClientConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Modern Stack/Memory monitor window with enhanced visual display
 * Shows User Stack, Global Stack, User Memory, and Global Memory in separate panels
 */
public class ModernStackMemoryWindow extends JFrame {
    
    private static final Color ACCENT_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color PANEL_COLOR = new Color(45, 45, 45);
    
    // Core components
    private ClientConnection clientConnection;
    private boolean connected = false;
    
    // UI Components
    private JTextArea userStackArea;
    private JTextArea globalStackArea;
    private JTextArea userMemoryArea;
    private JTextArea globalMemoryArea;
    
    // Control components
    private JButton refreshButton;
    private JCheckBox autoRefreshCheckBox;
    private JLabel connectionStatusLabel;
    private JLabel lastUpdateLabel;
    
    // Background tasks
    private ScheduledExecutorService scheduler;
    private Timer updateTimer;
    
    public ModernStackMemoryWindow(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        this.connected = clientConnection != null && clientConnection.isConnected();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupTimer();
        setupWindowProperties();
        
        // Initial update
        updateDisplays();
    }
    
    private void initializeComponents() {
        // Create text areas for each display type
        userStackArea = createDisplayArea("User Stack");
        globalStackArea = createDisplayArea("Global Stack");
        userMemoryArea = createDisplayArea("User Memory");
        globalMemoryArea = createDisplayArea("Global Memory");
        
        // Control components
        refreshButton = createButton("🔄 Refresh Now", ACCENT_COLOR);
        refreshButton.addActionListener(e -> updateDisplays());
        
        autoRefreshCheckBox = new JCheckBox("Auto Refresh", true);
        autoRefreshCheckBox.setForeground(Color.WHITE);
        autoRefreshCheckBox.setBackground(PANEL_COLOR);
        autoRefreshCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        connectionStatusLabel = new JLabel("Status: Disconnected");
        connectionStatusLabel.setForeground(DANGER_COLOR);
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        lastUpdateLabel = new JLabel("Last Update: Never");
        lastUpdateLabel.setForeground(Color.GRAY);
        lastUpdateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
    }
    
    private JTextArea createDisplayArea(String title) {
        JTextArea area = new JTextArea(12, 30);
        area.setEditable(false);
        area.setFont(new Font("JetBrains Mono", Font.PLAIN, 11));
        area.setBackground(PANEL_COLOR);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        area.setText("Not connected to server");
        return area;
    }
    
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main panel with grid layout for the 4 display areas
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        // Create titled panels for each display area
        mainPanel.add(createTitledPanel("📊 User Stack", userStackArea));
        mainPanel.add(createTitledPanel("🌐 Global Stack", globalStackArea));
        mainPanel.add(createTitledPanel("💾 User Memory", userMemoryArea));
        mainPanel.add(createTitledPanel("🗄️ Global Memory", globalMemoryArea));
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Control panel at bottom
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        
        // Left side: Status information
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.add(connectionStatusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(lastUpdateLabel);
        
        // Right side: Controls
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(autoRefreshCheckBox);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(refreshButton);
        
        controlPanel.add(statusPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitledPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(35, 35, 35));
        
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected() && connected) {
                updateTimer.start();
            } else {
                updateTimer.stop();
            }
        });
    }
    
    private void setupTimer() {
        updateTimer = new Timer(2000, e -> updateDisplays()); // Update every 2 seconds
        if (autoRefreshCheckBox.isSelected() && connected) {
            updateTimer.start();
        }
    }
    
    private void setupWindowProperties() {
        setTitle("PlanetLang Stack & Memory Monitor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (updateTimer != null && updateTimer.isRunning()) {
                    updateTimer.stop();
                }
                if (scheduler != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                }
            }
        });
        
        // Set window icon (same as main window)
        try {
            setIconImage(createWindowIcon());
        } catch (Exception e) {
            System.err.println("Could not set window icon: " + e.getMessage());
        }
    }
    
    private Image createWindowIcon() {
        // Create a simple colored square icon
        java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ACCENT_COLOR);
        g2d.fillRoundRect(4, 4, 24, 24, 6, 6);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawString("SM", 8, 22);
        g2d.dispose();
        return icon;
    }
    
    private void updateDisplays() {
        if (clientConnection == null) {
            updateConnectionStatus(false);
            return;
        }
        
        boolean isConnected = clientConnection.isConnected();
        updateConnectionStatus(isConnected);
        
        if (!isConnected) {
            SwingUtilities.invokeLater(() -> {
                userStackArea.setText("Not connected to server");
                globalStackArea.setText("Not connected to server");
                userMemoryArea.setText("Not connected to server");
                globalMemoryArea.setText("Not connected to server");
            });
            return;
        }
        
        // Update in background thread to avoid blocking UI
        SwingUtilities.invokeLater(() -> {
            try {
                String userStack = clientConnection.getUserStack();
                String globalStack = clientConnection.getGlobalStack();
                String userMemory = clientConnection.getUserMemory();
                String globalMemory = clientConnection.getGlobalMemory();
                
                userStackArea.setText(userStack != null ? userStack : "No data available");
                globalStackArea.setText(globalStack != null ? globalStack : "No data available");
                userMemoryArea.setText(userMemory != null ? userMemory : "No data available");
                globalMemoryArea.setText(globalMemory != null ? globalMemory : "No data available");
                
                // Auto-scroll to top for better visibility
                userStackArea.setCaretPosition(0);
                globalStackArea.setCaretPosition(0);
                userMemoryArea.setCaretPosition(0);
                globalMemoryArea.setCaretPosition(0);
                
                // Update last update time
                lastUpdateLabel.setText("Last Update: " + java.time.LocalTime.now().toString().substring(0, 8));
                
            } catch (Exception e) {
                System.err.println("Failed to update stack/memory displays: " + e.getMessage());
                // Show error in status
                SwingUtilities.invokeLater(() -> {
                    connectionStatusLabel.setText("Status: Update Error");
                    connectionStatusLabel.setForeground(DANGER_COLOR);
                });
            }
        });
    }
    
    private void updateConnectionStatus(boolean isConnected) {
        this.connected = isConnected;
        
        SwingUtilities.invokeLater(() -> {
            if (isConnected) {
                connectionStatusLabel.setText("Status: Connected");
                connectionStatusLabel.setForeground(SUCCESS_COLOR);
                if (autoRefreshCheckBox.isSelected()) {
                    updateTimer.start();
                }
            } else {
                connectionStatusLabel.setText("Status: Disconnected");
                connectionStatusLabel.setForeground(DANGER_COLOR);
                if (updateTimer.isRunning()) {
                    updateTimer.stop();
                }
            }
        });
    }
    
    /**
     * Update connection status from external source
     */
    public void setConnected(boolean connected) {
        updateConnectionStatus(connected);
    }
    
    /**
     * Force refresh of all displays
     */
    public void refresh() {
        updateDisplays();
    }
}
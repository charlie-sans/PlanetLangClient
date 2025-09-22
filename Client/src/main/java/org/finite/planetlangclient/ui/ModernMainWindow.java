package org.finite.planetlangclient.ui;

import org.finite.planetlangclient.ClientConnection;
import org.finite.planetlangclient.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Modern main window for PlanetLang Client with contemporary UI design
 */
public class ModernMainWindow extends JFrame {
    
    private static final Color ACCENT_COLOR = new Color(0, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    // Core components
    private ClientConnection clientConnection;
    private boolean connected = false;
    
    // UI Components
    private ModernConnectionPanel connectionPanel;
    private ModernCodeEditor codeEditor;
    private ModernStackMonitor stackMonitor;
    private ModernChatPanel chatPanel;
    private ModernStatusBar statusBar;
    private ModernToolBar toolBar;
    private ModernStackMemoryWindow stackMemoryWindow;
    
    // Timers and background tasks
    private ScheduledExecutorService scheduler;
    private Timer uiUpdateTimer;
    
    public ModernMainWindow() {
        initializeClient();
        initializeUI();
        setupLayout();
        setupEventHandlers();
        setupBackgroundTasks();
        
        setLocationRelativeTo(null);
    }
    
    private void initializeClient() {
        clientConnection = new ClientConnection();
        scheduler = Executors.newScheduledThreadPool(2);
    }
    
    private void initializeUI() {
        // Window properties
        setTitle("PlanetLang Client - Modern Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1000, 700));
        
        // Set window icon
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            System.err.println("Could not set window icon: " + e.getMessage());
        }
        
        // Initialize components
        connectionPanel = new ModernConnectionPanel();
        codeEditor = new ModernCodeEditor();
        stackMonitor = new ModernStackMonitor();
        chatPanel = new ModernChatPanel();
        statusBar = new ModernStatusBar();
        toolBar = new ModernToolBar();
        
        // Setup handlers after component creation
        connectionPanel.setConnectionCallback(this::onConnectionToggle);
        chatPanel.setMessageHandler(this::sendChatMessage);
        toolBar.setActionHandler(this::onToolBarAction);
        
        // Setup initial state
        updateConnectionState(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        
        // Top area - toolbar and connection
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolBar, BorderLayout.NORTH);
        topPanel.add(connectionPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Main content area with split panes
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(800);
        mainSplitPane.setResizeWeight(0.7);
        mainSplitPane.setBorder(null);
        
        // Left side - code editor and execution
        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Right side - stack monitor and chat
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(400);
        rightSplitPane.setResizeWeight(0.6);
        rightSplitPane.setBorder(null);
        
        rightSplitPane.setTopComponent(stackMonitor);
        rightSplitPane.setBottomComponent(chatPanel);
        
        mainSplitPane.setRightComponent(rightSplitPane);
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Bottom status bar
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Code editor with title
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "PlanetLang Code Editor"));
        editorPanel.add(codeEditor, BorderLayout.CENTER);
        
        // Execution buttons panel
        JPanel buttonPanel = createExecutionButtonPanel();
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        leftPanel.add(editorPanel, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    private JPanel createExecutionButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton executeLocalBtn = new JButton("Execute Local");
        executeLocalBtn.setPreferredSize(new Dimension(130, 35));
        executeLocalBtn.setBackground(ACCENT_COLOR);
        executeLocalBtn.setForeground(Color.WHITE);
        executeLocalBtn.setFocusPainted(false);
        executeLocalBtn.addActionListener(e -> executeCode(false));
        
        JButton executeGlobalBtn = new JButton("Execute Global");
        executeGlobalBtn.setPreferredSize(new Dimension(130, 35));
        executeGlobalBtn.setBackground(SUCCESS_COLOR);
        executeGlobalBtn.setForeground(Color.WHITE);
        executeGlobalBtn.setFocusPainted(false);
        executeGlobalBtn.addActionListener(e -> executeCode(true));
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(80, 35));
        clearBtn.addActionListener(e -> codeEditor.clear());
        
        JButton shareBtn = new JButton("Share Code");
        shareBtn.setPreferredSize(new Dimension(100, 35));
        shareBtn.addActionListener(e -> shareCodeToChat());
        
        buttonPanel.add(executeLocalBtn);
        buttonPanel.add(executeGlobalBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(clearBtn);
        buttonPanel.add(shareBtn);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        // Ctrl+Enter for local execution
        KeyStroke ctrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlEnter, "executeLocal");
        getRootPane().getActionMap().put("executeLocal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode(false);
            }
        });
        
        // Ctrl+Shift+Enter for global execution
        KeyStroke ctrlShiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlShiftEnter, "executeGlobal");
        getRootPane().getActionMap().put("executeGlobal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode(true);
            }
        });
    }
    
    private void setupBackgroundTasks() {
        // UI update timer for real-time data
        uiUpdateTimer = new Timer(1000, e -> {
            if (connected) {
                updateStackMonitor();
                updateChatMessages();
            }
        });
        uiUpdateTimer.start();
        
        // Connection health check
        scheduler.scheduleAtFixedRate(this::checkConnectionHealth, 5, 5, TimeUnit.SECONDS);
    }
    
    // Event handlers
    private void onConnectionToggle(String host, int port, boolean connect) {
        if (connect) {
            statusBar.setActivityStatus("Connecting to " + host + ":" + port + "...", new Color(255, 193, 7));
            
            // Connect in background thread
            SwingUtilities.invokeLater(() -> {
                boolean success = clientConnection.connect(host, port);
                updateConnectionState(success);
                
                if (success) {
                    statusBar.setActivityStatus("Connected to " + host + ":" + port, new Color(40, 167, 69));
                } else {
                    statusBar.setActivityStatus("Failed to connect to " + host + ":" + port, new Color(220, 53, 69));
                }
            });
        } else {
            clientConnection.disconnect();
            updateConnectionState(false);
            statusBar.setActivityStatus("Disconnected", new Color(255, 193, 7));
        }
    }
    
    private void onToolBarAction(String action) {
        switch (action) {
            case "new":
                codeEditor.clear();
                break;
            case "open":
                // TODO: Implement file opening
                break;
            case "save":
                // TODO: Implement file saving
                break;
            case "stack_monitor":
                // Toggle stack monitor visibility
                break;
            case "stack_memory":
                openStackMemoryWindow();
                break;
            case "settings":
                showSettingsDialog();
                break;
        }
    }
    
    private void executeCode(boolean global) {
        String code = codeEditor.getText().trim();
        if (code.isEmpty()) {
            statusBar.setActivityStatus("No code to execute", new Color(255, 193, 7));
            return;
        }
        
        if (!connected) {
            statusBar.setActivityStatus("Not connected to server", new Color(220, 53, 69));
            return;
        }
        
        // Execute in background thread
        SwingUtilities.invokeLater(() -> {
            try {
                String result;
                if (global) {
                    result = clientConnection.executeGlobal(code);
                    statusBar.setActivityStatus("Executed on global stack", new Color(40, 167, 69));
                } else {
                    result = clientConnection.executeLocal(code);
                    statusBar.setActivityStatus("Executed on local stack", new Color(40, 167, 69));
                }
                
                // Show result in a modern dialog
                showExecutionResult(result, global);
                
            } catch (Exception e) {
                statusBar.setActivityStatus("Execution error: " + e.getMessage(), new Color(220, 53, 69));
            }
        });
    }
    
    private void shareCodeToChat() {
        String code = codeEditor.getText().trim();
        if (code.isEmpty()) {
            statusBar.setActivityStatus("No code to share", new Color(255, 193, 7));
            return;
        }
        
        String message = "📝 Shared code:\n```\n" + code + "\n```";
        sendChatMessage(message);
    }
    
    private void sendChatMessage(String message) {
        if (!connected) {
            statusBar.setActivityStatus("Cannot send message: not connected", new Color(220, 53, 69));
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                String result = clientConnection.sendChatMessage(message);
                if (result.startsWith("ERROR")) {
                    statusBar.setActivityStatus("Failed to send message", new Color(220, 53, 69));
                }
            } catch (Exception e) {
                statusBar.setActivityStatus("Chat error: " + e.getMessage(), new Color(220, 53, 69));
            }
        });
    }
    
    // UI update methods
    private void updateConnectionState(boolean connected) {
        this.connected = connected;
        connectionPanel.setConnected(connected);
        
        // Update component states
        SwingUtilities.invokeLater(() -> {
            codeEditor.setEnabled(connected);
            chatPanel.setEnabled(connected);
            toolBar.setConnected(connected);
            statusBar.setConnectionStatus(connected);
            
            // Update stack memory window if it's open
            if (stackMemoryWindow != null && stackMemoryWindow.isDisplayable()) {
                stackMemoryWindow.setConnected(connected);
            }
        });
    }
    
    private void updateStackMonitor() {
        if (!connected) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                // For now, just refresh the stack monitor display
                stackMonitor.refreshStack();
            } catch (Exception e) {
                // Silently handle update errors
            }
        });
    }
    
    private void updateChatMessages() {
        if (!connected) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                // For now, just append a system message about updates
                chatPanel.appendSystemMessage("Messages updated");
            } catch (Exception e) {
                // Silently handle update errors
            }
        });
    }
    
    private void checkConnectionHealth() {
        if (connected && !clientConnection.isConnected()) {
            SwingUtilities.invokeLater(() -> {
                updateConnectionState(false);
                statusBar.setActivityStatus("Connection lost", new Color(220, 53, 69));
            });
        }
    }
    
    // Helper methods
    private Image createAppIcon() {
        // Create a simple application icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple planet-like icon
        g2.setColor(ACCENT_COLOR);
        g2.fillOval(4, 4, 24, 24);
        g2.setColor(Color.WHITE);
        g2.fillOval(8, 8, 16, 16);
        g2.setColor(ACCENT_COLOR);
        g2.fillOval(12, 12, 8, 8);
        
        g2.dispose();
        return icon;
    }
    
    private void showExecutionResult(String result, boolean global) {
        JDialog resultDialog = new JDialog(this, "Execution Result", true);
        resultDialog.setSize(400, 300);
        resultDialog.setLocationRelativeTo(this);
        
        JTextArea resultArea = new JTextArea(result);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultDialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> resultDialog.dispose());
        buttonPanel.add(closeBtn);
        
        resultDialog.add(buttonPanel, BorderLayout.SOUTH);
        resultDialog.setVisible(true);
    }
    
    private void openStackMemoryWindow() {
        if (stackMemoryWindow == null || !stackMemoryWindow.isDisplayable()) {
            stackMemoryWindow = new ModernStackMemoryWindow(clientConnection);
            stackMemoryWindow.setConnected(connected);
        }
        stackMemoryWindow.setVisible(true);
        stackMemoryWindow.toFront();
        stackMemoryWindow.requestFocus();
    }
    
    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(500, 400);
        settingsDialog.setLocationRelativeTo(this);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Theme settings
        JPanel themePanel = new JPanel(new FlowLayout());
        themePanel.add(new JLabel("Theme:"));
        themePanel.add(ThemeManager.createThemeSwitcher());
        tabbedPane.addTab("Appearance", themePanel);
        
        // Connection settings
        JPanel connectionPanel = new JPanel(new GridBagLayout());
        // Add connection settings here
        tabbedPane.addTab("Connection", connectionPanel);
        
        settingsDialog.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> settingsDialog.dispose());
        buttonPanel.add(closeBtn);
        
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }
    
    private void shutdown() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
        
        if (scheduler != null) {
            scheduler.shutdown();
        }
        
        if (clientConnection != null) {
            clientConnection.shutdown();
        }
        
        System.exit(0);
    }
}
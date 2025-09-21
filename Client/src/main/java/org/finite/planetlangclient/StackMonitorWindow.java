package org.finite.planetlangclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A dedicated window for monitoring PlanetLang stacks and memory in real-time
 */
public class StackMonitorWindow extends JFrame {
    private ClientConnection clientConnection;
    private boolean connected = false;

    // UI Components
    private JTextArea userStackArea;
    private JTextArea globalStackArea;
    private JTextArea globalMemoryArea;
    private JTextArea userMemoryArea;
    private Timer updateTimer;
    private JButton refreshButton;
    private JCheckBox autoRefreshCheckBox;

    public StackMonitorWindow(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        this.connected = clientConnection.isConnected();

        initComponents();
        setupLayout();
        setupTimer();
        setupWindowProperties();

        // Initial update
        updateDisplays();
    }

    private void initComponents() {
        // Stack display areas
        userStackArea = createStackArea("Your Stack");
        globalStackArea = createStackArea("Global Stack");
        globalMemoryArea = createStackArea("Global Memory");
        userMemoryArea = createStackArea("Your Memory");

        // Control buttons
        refreshButton = new JButton("Refresh Now");
        refreshButton.addActionListener(e -> updateDisplays());

        autoRefreshCheckBox = new JCheckBox("Auto Refresh", true);
        autoRefreshCheckBox.addActionListener(e -> {
            if (autoRefreshCheckBox.isSelected()) {
                updateTimer.start();
            } else {
                updateTimer.stop();
            }
        });
    }

    private JTextArea createStackArea(String title) {
        JTextArea area = new JTextArea(8, 20);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setBackground(new Color(245, 245, 245));
        return area;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create main panel with stack displays
        JPanel stacksPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        stacksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        stacksPanel.add(new JScrollPane(userStackArea));
        stacksPanel.add(new JScrollPane(globalStackArea));
        stacksPanel.add(new JScrollPane(userMemoryArea));
        stacksPanel.add(new JScrollPane(globalMemoryArea));

        add(stacksPanel, BorderLayout.CENTER);

        // Control panel at bottom
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(autoRefreshCheckBox);
        controlPanel.add(refreshButton);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        JLabel statusLabel = new JLabel("Status: " + (connected ? "Connected" : "Disconnected"));
        statusLabel.setForeground(connected ? Color.GREEN.darker() : Color.RED);
        statusPanel.add(statusLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupTimer() {
        updateTimer = new Timer(1000, e -> updateDisplays());
        if (autoRefreshCheckBox.isSelected()) {
            updateTimer.start();
        }
    }

    private void setupWindowProperties() {
        setTitle("PlanetLang Stack Monitor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (updateTimer != null) {
                    updateTimer.stop();
                }
            }
        });
    }

    private void updateDisplays() {
        if (!clientConnection.isConnected()) {
            connected = false;
            userStackArea.setText("Not connected to server");
            globalStackArea.setText("Not connected to server");
            globalMemoryArea.setText("Not connected to server");
            userMemoryArea.setText("Not connected to server");
            return;
        }

        connected = true;

        try {
            // Update all displays
            SwingUtilities.invokeLater(() -> {
                userStackArea.setText(clientConnection.getUserStack());
                globalStackArea.setText(clientConnection.getGlobalStack());
                globalMemoryArea.setText(clientConnection.getGlobalMemory());
                userMemoryArea.setText(clientConnection.getUserMemory());

                // Auto-scroll to top for better visibility
                userStackArea.setCaretPosition(0);
                globalStackArea.setCaretPosition(0);
                globalMemoryArea.setCaretPosition(0);
                userMemoryArea.setCaretPosition(0);
            });
        } catch (Exception e) {
            System.err.println("Failed to update stack monitor: " + e.getMessage());
        }
    }

    /**
     * Update connection status
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
        if (!connected && updateTimer != null) {
            updateTimer.stop();
        } else if (connected && autoRefreshCheckBox.isSelected() && updateTimer != null) {
            updateTimer.start();
        }
    }
}
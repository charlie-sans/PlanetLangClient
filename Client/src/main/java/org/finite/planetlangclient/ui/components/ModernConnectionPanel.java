package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Modern connection panel with sleek design
 */
public class ModernConnectionPanel extends JPanel {
    
    @FunctionalInterface
    public interface ConnectionCallback {
        void onConnectionToggle(String host, int port, boolean connect);
    }
    
    private JTextField hostField;
    private JSpinner portSpinner;
    private JButton connectButton;
    private JLabel statusLabel;
    private JProgressBar connectionProgress;
    
    private boolean connected = false;
    private ConnectionCallback connectionCallback;
    
    public ModernConnectionPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    public ModernConnectionPanel(ConnectionCallback connectionCallback) {
        this();
        this.connectionCallback = connectionCallback;
    }
    
    private void initializeComponents() {
        hostField = new JTextField("localhost", 12);
        hostField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        portSpinner = new JSpinner(new SpinnerNumberModel(8080, 1, 65535, 1));
        portSpinner.setPreferredSize(new Dimension(80, 25));
        
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(100, 30));
        connectButton.setBackground(new Color(0, 123, 255));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        
        statusLabel = new JLabel("Disconnected");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        
        connectionProgress = new JProgressBar();
        connectionProgress.setIndeterminate(true);
        connectionProgress.setVisible(false);
        connectionProgress.setPreferredSize(new Dimension(100, 4));
    }
    
    private void setupLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setBackground(UIManager.getColor("Panel.background"));
        
        add(new JLabel("Host:"));
        add(hostField);
        add(new JLabel("Port:"));
        add(portSpinner);
        add(connectButton);
        add(Box.createHorizontalStrut(20));
        add(statusLabel);
        add(connectionProgress);
    }
    
    private void setupEventHandlers() {
        connectButton.addActionListener(e -> toggleConnection());
        
        // Enter key in host field triggers connection
        hostField.addActionListener(e -> toggleConnection());
    }
    
    private void toggleConnection() {
        if (!connected) {
            String host = hostField.getText().trim();
            int port = (Integer) portSpinner.getValue();
            
            if (host.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a host address", 
                    "Invalid Host", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            setConnecting(true);
            
            // Perform connection in background
            SwingUtilities.invokeLater(() -> {
                if (connectionCallback != null) {
                    connectionCallback.onConnectionToggle(host, port, true);
                }
            });
        } else {
            if (connectionCallback != null) {
                connectionCallback.onConnectionToggle("", 0, false);
            }
        }
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
        
        SwingUtilities.invokeLater(() -> {
            connectButton.setText(connected ? "Disconnect" : "Connect");
            connectButton.setBackground(connected ? 
                new Color(220, 53, 69) : new Color(0, 123, 255));
            
            hostField.setEnabled(!connected);
            portSpinner.setEnabled(!connected);
            
            statusLabel.setText(connected ? "Connected" : "Disconnected");
            statusLabel.setForeground(connected ? 
                new Color(40, 167, 69) : Color.GRAY);
            
            connectionProgress.setVisible(false);
        });
    }
    
    private void setConnecting(boolean connecting) {
        SwingUtilities.invokeLater(() -> {
            connectButton.setEnabled(!connecting);
            connectionProgress.setVisible(connecting);
            statusLabel.setText(connecting ? "Connecting..." : "Disconnected");
            statusLabel.setForeground(connecting ? 
                new Color(255, 193, 7) : Color.GRAY);
        });
    }
    
    public void setConnectionCallback(ConnectionCallback callback) {
        this.connectionCallback = callback;
    }
}
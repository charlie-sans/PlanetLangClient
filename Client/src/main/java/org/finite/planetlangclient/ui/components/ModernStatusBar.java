package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Modern status bar with connection and activity indicators
 */
public class ModernStatusBar extends JPanel {
    
    private JLabel connectionStatus;
    private JLabel activityStatus;
    private JLabel timeLabel;
    private JProgressBar progressBar;
    private Timer timeUpdater;
    private boolean isConnected = false;
    
    public ModernStatusBar() {
        initializeComponents();
        setupLayout();
        startTimeUpdater();
    }
    
    private void initializeComponents() {
        connectionStatus = new JLabel("Disconnected");
        connectionStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        connectionStatus.setForeground(new Color(220, 53, 69));
        connectionStatus.setIcon(createStatusIcon(Color.RED));
        
        activityStatus = new JLabel("Ready");
        activityStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        activityStatus.setForeground(Color.GRAY);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setForeground(Color.GRAY);
        updateTime();
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 16));
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setForeground(new Color(0, 123, 255));
        
        // Set panel background
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(connectionStatus);
        leftPanel.add(createSeparator());
        leftPanel.add(activityStatus);
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(progressBar);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(timeLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JLabel createSeparator() {
        JLabel separator = new JLabel("|");
        separator.setForeground(new Color(100, 100, 100));
        separator.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return separator;
    }
    
    private Icon createStatusIcon(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x, y + 2, 8, 8);
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 12;
            }
            
            @Override
            public int getIconHeight() {
                return 12;
            }
        };
    }
    
    private void startTimeUpdater() {
        timeUpdater = new Timer(1000, e -> updateTime());
        timeUpdater.start();
    }
    
    private void updateTime() {
        SwingUtilities.invokeLater(() -> {
            String time = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            timeLabel.setText(time);
        });
    }
    
    public void setConnectionStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            this.isConnected = connected;
            if (connected) {
                connectionStatus.setText("Connected");
                connectionStatus.setForeground(new Color(40, 167, 69));
                connectionStatus.setIcon(createStatusIcon(new Color(40, 167, 69)));
            } else {
                connectionStatus.setText("Disconnected");
                connectionStatus.setForeground(new Color(220, 53, 69));
                connectionStatus.setIcon(createStatusIcon(new Color(220, 53, 69)));
            }
        });
    }
    
    public void setActivityStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            activityStatus.setText(status);
            activityStatus.setForeground(Color.LIGHT_GRAY);
        });
    }
    
    public void setActivityStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            activityStatus.setText(status);
            activityStatus.setForeground(color);
        });
    }
    
    public void showProgress(boolean show) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(show);
            if (show) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
            }
        });
    }
    
    public void setProgress(int value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(false);
            progressBar.setValue(value);
        });
    }
    
    public void hideProgress() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
        });
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (timeUpdater != null) {
            timeUpdater.stop();
        }
    }
}
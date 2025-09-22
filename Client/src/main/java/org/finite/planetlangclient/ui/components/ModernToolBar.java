package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Modern toolbar with styled buttons and actions
 */
public class ModernToolBar extends JToolBar {
    
    private JButton connectButton;
    private JButton executeButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton stackMemoryButton;
    private JButton settingsButton;
    
    private Consumer<String> actionHandler;
    
    public ModernToolBar() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setFloatable(false);
        setRollover(true);
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        connectButton = createToolbarButton("Connect", "🔌", new Color(0, 123, 255));
        executeButton = createToolbarButton("Execute", "▶", new Color(40, 167, 69));
        clearButton = createToolbarButton("Clear", "🗑", new Color(108, 117, 125));
        saveButton = createToolbarButton("Save", "💾", new Color(23, 162, 184));
        loadButton = createToolbarButton("Load", "📁", new Color(255, 193, 7));
        stackMemoryButton = createToolbarButton("Stack/Memory", "📊", new Color(138, 43, 226));
        settingsButton = createToolbarButton("Settings", "⚙", new Color(108, 117, 125));
        
        // Initially disable buttons that require connection
        executeButton.setEnabled(false);
    }
    
    private JButton createToolbarButton(String text, String icon, Color color) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        add(connectButton);
        addSeparator();
        add(executeButton);
        add(clearButton);
        addSeparator();
        add(saveButton);
        add(loadButton);
        addSeparator();
        add(stackMemoryButton);
        addSeparator();
        add(Box.createHorizontalGlue()); // Push settings to the right
        add(settingsButton);
    }
    
    private void setupEventHandlers() {
        connectButton.addActionListener(e -> handleAction("connect"));
        executeButton.addActionListener(e -> handleAction("execute"));
        clearButton.addActionListener(e -> handleAction("clear"));
        saveButton.addActionListener(e -> handleAction("save"));
        loadButton.addActionListener(e -> handleAction("load"));
        stackMemoryButton.addActionListener(e -> handleAction("stack_memory"));
        settingsButton.addActionListener(e -> handleAction("settings"));
    }
    
    private void handleAction(String action) {
        if (actionHandler != null) {
            actionHandler.accept(action);
        }
    }
    
    @Override
    public void addSeparator() {
        add(Box.createHorizontalStrut(10));
        JPanel separator = new JPanel();
        separator.setBackground(new Color(70, 70, 70));
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setMaximumSize(new Dimension(1, 20));
        add(separator);
        add(Box.createHorizontalStrut(10));
    }
    
    public void setConnected(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                connectButton.setText("🔌 Disconnect");
                connectButton.setBackground(new Color(220, 53, 69));
                executeButton.setEnabled(true);
            } else {
                connectButton.setText("🔌 Connect");
                connectButton.setBackground(new Color(0, 123, 255));
                executeButton.setEnabled(false);
            }
        });
    }
    
    public void setExecuteEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            executeButton.setEnabled(enabled);
            if (enabled) {
                executeButton.setBackground(new Color(40, 167, 69));
            } else {
                executeButton.setBackground(new Color(108, 117, 125));
            }
        });
    }
    
    public void setActionHandler(Consumer<String> handler) {
        this.actionHandler = handler;
    }
    
    public void showProgress(String buttonAction, boolean inProgress) {
        SwingUtilities.invokeLater(() -> {
            JButton button = getButtonByAction(buttonAction);
            if (button != null) {
                button.setEnabled(!inProgress);
                if (inProgress) {
                    button.setText(button.getText() + " ...");
                } else {
                    // Reset text (remove " ...")
                    String text = button.getText();
                    if (text.endsWith(" ...")) {
                        button.setText(text.substring(0, text.length() - 4));
                    }
                }
            }
        });
    }
    
    private JButton getButtonByAction(String action) {
        switch (action.toLowerCase()) {
            case "connect": return connectButton;
            case "execute": return executeButton;
            case "clear": return clearButton;
            case "save": return saveButton;
            case "load": return loadButton;
            case "settings": return settingsButton;
            default: return null;
        }
    }
    
    public void setButtonText(String action, String text) {
        SwingUtilities.invokeLater(() -> {
            JButton button = getButtonByAction(action);
            if (button != null) {
                // Preserve the icon if it exists
                String currentText = button.getText();
                if (currentText.contains(" ")) {
                    String icon = currentText.split(" ")[0];
                    button.setText(icon + " " + text);
                } else {
                    button.setText(text);
                }
            }
        });
    }
}
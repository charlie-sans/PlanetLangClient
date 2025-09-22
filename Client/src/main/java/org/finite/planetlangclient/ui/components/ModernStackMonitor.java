package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Modern stack monitor with visual representation
 */
public class ModernStackMonitor extends JPanel {
    
    private DefaultListModel<String> stackModel;
    private JList<String> stackList;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JButton clearButton;
    private JButton refreshButton;
    
    public ModernStackMonitor() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        stackModel = new DefaultListModel<>();
        stackList = new JList<>(stackModel);
        stackList.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        stackList.setBorder(new EmptyBorder(5, 10, 5, 10));
        stackList.setCellRenderer(new ModernStackCellRenderer());
        
        // Set modern colors
        stackList.setBackground(new Color(50, 50, 50));
        stackList.setForeground(Color.WHITE);
        stackList.setSelectionBackground(new Color(0, 123, 255, 100));
        stackList.setSelectionForeground(Color.WHITE);
        
        scrollPane = new JScrollPane(stackList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        
        statusLabel = new JLabel("Stack Size: 0");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GRAY);
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        clearButton.setFocusPainted(false);
        clearButton.setBackground(new Color(220, 53, 69));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);
        
        bottomPanel.add(statusPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        clearButton.addActionListener(e -> clearStack());
        refreshButton.addActionListener(e -> refreshStack());
    }
    
    public void updateStack(List<String> stackItems) {
        SwingUtilities.invokeLater(() -> {
            stackModel.clear();
            for (int i = stackItems.size() - 1; i >= 0; i--) {
                stackModel.addElement("[" + i + "] " + stackItems.get(i));
            }
            statusLabel.setText("Stack Size: " + stackItems.size());
        });
    }
    
    public void clearStack() {
        SwingUtilities.invokeLater(() -> {
            stackModel.clear();
            statusLabel.setText("Stack Size: 0");
        });
    }
    
    public void addToStack(String item) {
        SwingUtilities.invokeLater(() -> {
            int index = stackModel.getSize();
            stackModel.add(0, "[" + index + "] " + item);
            statusLabel.setText("Stack Size: " + stackModel.getSize());
        });
    }
    
    public void removeFromStack() {
        SwingUtilities.invokeLater(() -> {
            if (!stackModel.isEmpty()) {
                stackModel.remove(0);
                // Update indices
                List<String> items = new ArrayList<>();
                for (int i = 0; i < stackModel.getSize(); i++) {
                    String item = stackModel.getElementAt(i);
                    String value = item.substring(item.indexOf("] ") + 2);
                    items.add("[" + (stackModel.getSize() - 1 - i) + "] " + value);
                }
                stackModel.clear();
                for (String item : items) {
                    stackModel.addElement(item);
                }
                statusLabel.setText("Stack Size: " + stackModel.getSize());
            }
        });
    }
    
    public void refreshStack() {
        // This would typically trigger a refresh from the server
        // For now, just update the display
        SwingUtilities.invokeLater(() -> {
            stackList.repaint();
        });
    }
    
    /**
     * Custom cell renderer for stack items
     */
    private static class ModernStackCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (isSelected) {
                setBackground(new Color(0, 123, 255, 100));
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(50, 50, 50));
                if (index == 0) {
                    setForeground(new Color(255, 193, 7)); // Top of stack - yellow
                } else {
                    setForeground(Color.WHITE);
                }
            }
            
            setBorder(new EmptyBorder(3, 10, 3, 10));
            return this;
        }
    }
}
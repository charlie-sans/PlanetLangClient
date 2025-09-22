package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern code editor with syntax highlighting support
 */
public class ModernCodeEditor extends JPanel {
    
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel lineCountLabel;
    
    public ModernCodeEditor() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        textArea = new JTextArea();
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        textArea.setTabSize(4);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Set modern colors
        textArea.setBackground(new Color(43, 43, 43));
        textArea.setForeground(new Color(169, 183, 198));
        textArea.setCaretColor(Color.WHITE);
        textArea.setSelectionColor(new Color(0, 123, 255, 80));
        
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        
        lineCountLabel = new JLabel("Lines: 0 | Characters: 0");
        lineCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lineCountLabel.setForeground(Color.GRAY);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(lineCountLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateLineCount();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateLineCount();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateLineCount();
            }
        });
    }
    
    private void updateLineCount() {
        SwingUtilities.invokeLater(() -> {
            String text = textArea.getText();
            int lines = text.isEmpty() ? 0 : text.split("\n").length;
            int chars = text.length();
            lineCountLabel.setText("Lines: " + lines + " | Characters: " + chars);
        });
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
    }
    
    public void clear() {
        textArea.setText("");
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textArea.setEnabled(enabled);
        if (enabled) {
            textArea.setBackground(new Color(43, 43, 43));
            textArea.setForeground(new Color(169, 183, 198));
        } else {
            textArea.setBackground(new Color(60, 60, 60));
            textArea.setForeground(Color.GRAY);
        }
    }
    
    @Override
    public boolean requestFocusInWindow() {
        return textArea.requestFocusInWindow();
    }
}
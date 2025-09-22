package org.finite.planetlangclient.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Modern chat panel for server communication
 */
public class ModernChatPanel extends JPanel {
    
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane chatScrollPane;
    private JButton clearButton;
    private ChatMessageHandler messageHandler;
    
    public interface ChatMessageHandler {
        void onMessageSent(String message);
    }
    
    public ModernChatPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Set modern colors
        chatArea.setBackground(new Color(45, 45, 45));
        chatArea.setForeground(Color.WHITE);
        
        chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        
        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        inputField.setBorder(new EmptyBorder(8, 10, 8, 10));
        inputField.setBackground(new Color(60, 60, 60));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        sendButton.setFocusPainted(false);
        sendButton.setBackground(new Color(40, 167, 69));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        clearButton.setFocusPainted(false);
        clearButton.setBackground(new Color(108, 117, 125));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel with title and clear button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Communication Log");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        
        JPanel clearPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        clearPanel.add(clearButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(clearPanel, BorderLayout.EAST);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        sendButton.addActionListener(this::sendMessage);
        clearButton.addActionListener(e -> clearChat());
        
        // Send message on Enter
        inputField.addActionListener(this::sendMessage);
        
        // Also allow Ctrl+Enter to send
        inputField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "send");
        inputField.getActionMap().put("send", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e);
            }
        });
    }
    
    private void sendMessage(ActionEvent e) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendMessage("You", message, new Color(0, 123, 255));
            inputField.setText("");
            inputField.requestFocus();
            
            if (messageHandler != null) {
                messageHandler.onMessageSent(message);
            }
        }
    }
    
    public void appendMessage(String sender, String message, Color senderColor) {
        SwingUtilities.invokeLater(() -> {
            if (chatArea.getText().length() > 0) {
                chatArea.append("\n");
            }
            
            String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            chatArea.append("[" + timestamp + "] ");
            chatArea.append(sender + ": " + message);
            
            // Auto-scroll to bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    public void appendSystemMessage(String message) {
        appendMessage("System", message, new Color(255, 193, 7));
    }
    
    public void appendServerMessage(String message) {
        appendMessage("Server", message, new Color(40, 167, 69));
    }
    
    public void appendErrorMessage(String message) {
        appendMessage("Error", message, new Color(220, 53, 69));
    }
    
    public void clearChat() {
        SwingUtilities.invokeLater(() -> {
            chatArea.setText("");
            appendSystemMessage("Chat cleared");
        });
    }
    
    public void setMessageHandler(ChatMessageHandler handler) {
        this.messageHandler = handler;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        
        if (enabled) {
            inputField.setBackground(new Color(60, 60, 60));
            sendButton.setBackground(new Color(40, 167, 69));
        } else {
            inputField.setBackground(new Color(80, 80, 80));
            sendButton.setBackground(new Color(108, 117, 125));
        }
    }
    
    @Override
    public boolean requestFocusInWindow() {
        return inputField.requestFocusInWindow();
    }
}
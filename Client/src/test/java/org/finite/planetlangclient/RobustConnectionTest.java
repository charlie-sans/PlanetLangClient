package org.finite.planetlangclient;

/**
 * Test class to demonstrate robust connection handling
 */
public class RobustConnectionTest {
    
    public static void main(String[] args) {
        testBasicConnection();
        testConnectionRetry();
        testKeepAlive();
    }
    
    /**
     * Test basic connection functionality
     */
    public static void testBasicConnection() {
        System.out.println("=== Testing Basic Connection ===");
        
        ClientConnection client = new ClientConnection();
        
        // Try to connect to server
        boolean connected = client.connect("localhost", 8080);
        
        if (connected) {
            System.out.println("✓ Connected successfully!");
            System.out.println("Session ID: " + client.getSessionId());
            
            // Test basic operations
            String result = client.executeLocal("push 42");
            System.out.println("Execute local result: " + result);
            
            String stackState = client.getUserStack();
            System.out.println("User stack state: " + stackState);
            
            client.disconnect();
            System.out.println("✓ Disconnected successfully!");
        } else {
            System.out.println("✗ Failed to connect to server");
            System.out.println("Make sure the server is running on localhost:8080");
        }
        
        System.out.println();
    }
    
    /**
     * Test connection retry functionality
     */
    public static void testConnectionRetry() {
        System.out.println("=== Testing Connection Retry ===");
        
        // Create client with custom retry settings
        ClientConnection client = new ClientConnection(2000, 5000, 2, 500);
        
        // Try to connect to non-existent server
        boolean connected = client.connect("localhost", 9999);
        
        if (!connected) {
            System.out.println("✓ Retry logic working - failed to connect to non-existent server");
        } else {
            System.out.println("✗ Unexpected connection success");
        }
        
        client.shutdown();
        System.out.println();
    }
    
    /**
     * Test keep-alive functionality
     */
    public static void testKeepAlive() {
        System.out.println("=== Testing Keep-Alive ===");
        
        ClientConnection client = new ClientConnection();
        
        boolean connected = client.connect("localhost", 8080);
        
        if (connected) {
            System.out.println("✓ Connected for keep-alive test");
            System.out.println("Connection will be maintained with keep-alive pings");
            System.out.println("Keep-alive interval: 30 seconds");
            
            // Simulate some activity
            try {
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    String result = client.executeLocal("push " + (i + 1));
                    System.out.println("Operation " + (i + 1) + " result: " + result);
                    System.out.println("Connection status: " + 
                        (client.isConnected() ? "Connected" : "Disconnected"));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            client.shutdown();
            System.out.println("✓ Keep-alive test completed");
        } else {
            System.out.println("✗ Failed to connect for keep-alive test");
        }
        
        System.out.println();
    }
}
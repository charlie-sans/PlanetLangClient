package org.finite.planetlangclient;

/**
 * Simple automated test for PlanetLang client-server connection
 */
public class QuickTest {
    public static void main(String[] args) {
        System.out.println("Testing PlanetLang Client-Server Connection...");

        ClientConnection connection = new ClientConnection();

        // Test connection
        if (!connection.connect("localhost", 8000)) {
            System.err.println("❌ FAILED: Could not connect to server");
            return;
        }

        System.out.println("✅ Connected successfully!");
        System.out.println("Session ID: " + connection.getSessionId());

        // Test local execution
        String result = connection.executeLocal("push 42");
        System.out.println("Local push result: " + result);

        // Test getting user stack
        String stack = connection.getUserStack();
        System.out.println("User stack: " + stack);

        // Test global execution
        result = connection.executeGlobal("push 100");
        System.out.println("Global push result: " + result);

        // Test getting global stack
        String globalStack = connection.getGlobalStack();
        System.out.println("Global stack: " + globalStack);

        // Test sharing
        result = connection.shareToGlobal();
        System.out.println("Share result: " + result);

        // Check stacks after sharing
        stack = connection.getUserStack();
        globalStack = connection.getGlobalStack();
        System.out.println("After sharing - User stack: " + stack + ", Global stack: " + globalStack);

        connection.disconnect();
        System.out.println("✅ Test completed successfully!");
    }
}
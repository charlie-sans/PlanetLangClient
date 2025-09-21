package org.finite.planetlangclient;

import java.util.Scanner;

/**
 * Simple command-line test client for PlanetLang
 */
public class TestClient {
    public static void main(String[] args) {
        ClientConnection connection = new ClientConnection();
        Scanner scanner = new Scanner(System.in);

        System.out.println("PlanetLang Test Client");
        System.out.println("Connecting to localhost:8000...");

        if (!connection.connect("localhost", 8000)) {
            System.err.println("Failed to connect to server");
            return;
        }

        System.out.println("Connected! Session ID: " + connection.getSessionId());
        System.out.println("Commands:");
        System.out.println("  LOCAL:<instruction>  - Execute on local stack");
        System.out.println("  GLOBAL:<instruction> - Execute on global stack");
        System.out.println("  SHARE - Share top value to global");
        System.out.println("  TAKE - Take value from global");
        System.out.println("  STATUS - Show current status");
        System.out.println("  QUIT - Exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("QUIT")) {
                break;
            } else if (input.equalsIgnoreCase("STATUS")) {
                System.out.println("User Stack: " + connection.getUserStack());
                System.out.println("Global Stack: " + connection.getGlobalStack());
                System.out.println("Global Memory: " + connection.getGlobalMemory());
            } else {
                String response = "";
                if (input.startsWith("LOCAL:")) {
                    String cmd = input.substring(6);
                    response = connection.executeLocal(cmd);
                } else if (input.startsWith("GLOBAL:")) {
                    String cmd = input.substring(7);
                    response = connection.executeGlobal(cmd);
                } else if (input.equals("SHARE")) {
                    response = connection.shareToGlobal();
                } else if (input.equals("TAKE")) {
                    response = connection.takeFromGlobal();
                } else {
                    System.out.println("Unknown command. Try STATUS for help.");
                    continue;
                }
                System.out.println("Response: " + response);
            }
        }

        connection.disconnect();
        System.out.println("Disconnected.");
    }
}
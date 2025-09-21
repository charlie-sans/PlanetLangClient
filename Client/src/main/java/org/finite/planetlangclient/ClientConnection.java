package org.finite.planetlangclient;

import java.io.*;
import java.net.*;
import org.finite.planetlangserver.Networking.Request;
import org.finite.planetlangserver.Networking.Auth;
import org.json.JSONObject;

/**
 * Handles client-side network communication with the PlanetLang server
 */
public class ClientConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private String sessionId;
    private final Object lock = new Object();

    /**
     * Connect to the server at the specified host and port
     */
    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;

            // Read session ID from server
            String sessionResponse = in.readLine();
            if (sessionResponse != null && sessionResponse.startsWith("SESSION:")) {
                sessionId = sessionResponse.substring(8);
                System.out.println("Connected to server at " + host + ":" + port + " with session " + sessionId);
            } else {
                System.err.println("Failed to receive session ID from server");
                disconnect();
                return false;
            }

            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute instruction on local (user) stack
     */
    public String executeLocal(String instruction) {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("LOCAL:" + instruction);
            try {
                return in.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Execute instruction on global stack
     */
    public String executeGlobal(String instruction) {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("GLOBAL:" + instruction);
            try {
                return in.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Share value from user stack to global stack
     */
    public String shareToGlobal() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("SHARE");
            try {
                return in.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Take value from global stack to user stack
     */
    public String takeFromGlobal() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("TAKE");
            try {
                return in.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }    /**
     * Get current user stack state
     */
    public String getUserStack() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_USER_STACK");
            try {
                String response = in.readLine();
                if (response != null && response.startsWith("USER_STACK:")) {
                    return response.substring(11);
                }
                return response;
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get current global stack state
     */
    public String getGlobalStack() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_GLOBAL_STACK");
            try {
                String response = in.readLine();
                if (response != null && response.startsWith("GLOBAL_STACK:")) {
                    return response.substring(13);
                }
                return response;
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get global memory state
     */
    public String getGlobalMemory() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_GLOBAL_MEMORY");
            try {
                String response = in.readLine();
                if (response != null && response.startsWith("GLOBAL_MEMORY:")) {
                    return response.substring(14);
                }
                return response;
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get user memory state
     */
    public String getUserMemory() {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_USER_MEMORY");
            try {
                String response = in.readLine();
                if (response != null && response.startsWith("USER_MEMORY:")) {
                    return response.substring(12);
                }
                return response;
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Send a chat message
     */
    public String sendChatMessage(String message) {
        synchronized (lock) {
            if (!connected) {
                return "ERROR: Not connected to server";
            }
            out.println("CHAT:" + message);
            try {
                return in.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get chat messages
     */
    public String getChatMessages() {
        synchronized (lock) {
            if (!connected) {
                return "";
            }
            out.println("GET_CHAT_MESSAGES");
            try {
                String response = in.readLine();
                if (response != null && response.startsWith("CHAT_MESSAGES:")) {
                    return response.substring(14);
                }
                return "";
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }    /**
     * Send a request to the server
     */
    public void sendRequest(Request request) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        out.println(request.toString());
    }

    /**
     * Send a simple text message to the server
     */
    public void sendMessage(String message) {
        if (!connected) {
            System.err.println("Not connected to server");
            return;
        }
        out.println(message);
    }

    /**
     * Read a response from the server
     */
    public String readResponse() throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }
        return in.readLine();
    }

    /**
     * Check if the client is connected
     */
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Get the current session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Disconnect from the server
     */
    public void disconnect() {
        connected = false;
        sessionId = null;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

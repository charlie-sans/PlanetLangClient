package org.finite.planetlangserver.Networking;
import java.io.*;
import java.net.*;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.*;
import PlanetVM.PlanetVMManager;

/**
 *
 * @author GAMER
 */
public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private PlanetVMManager vmManager;

    public void start(int port) throws IOException {
        vmManager = PlanetVMManager.getInstance();
        serverSocket = new ServerSocket(port);
        System.out.println("PlanetLang Server started on port " + port);

        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());

            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public void stop() throws IOException {
        for (ClientHandler client : clients) {
            client.stop();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private class ClientHandler implements Runnable {
        private static final int DEFAULT_READ_TIMEOUT = 60000; // 60 seconds
        
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private String sessionId;
        private long lastActivity = System.currentTimeMillis();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                // Set socket timeout for robustness
                clientSocket.setSoTimeout(DEFAULT_READ_TIMEOUT);
                clientSocket.setKeepAlive(true);
                clientSocket.setTcpNoDelay(true);
                
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Create user session
                sessionId = vmManager.createUserSession(clientSocket.getInetAddress().toString());
                out.println("SESSION:" + sessionId);
                updateActivity();

                String inputLine;
                while (running.get() && (inputLine = readLineWithTimeout()) != null) {
                    System.out.println("Received from " + sessionId + ": " + inputLine);
                    String response = processCommand(inputLine);
                    out.println(response);
                    updateActivity();
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Client " + sessionId + " timed out");
            } catch (IOException e) {
                if (running.get()) {
                    System.out.println("Client " + sessionId + " disconnected: " + e.getMessage());
                }
            } finally {
                stop();
            }
        }
        
        private String readLineWithTimeout() throws IOException {
            try {
                return in.readLine();
            } catch (SocketTimeoutException e) {
                // Check if client has been inactive for too long
                long inactiveTime = System.currentTimeMillis() - lastActivity;
                if (inactiveTime > DEFAULT_READ_TIMEOUT) {
                    throw e; // Re-throw to disconnect inactive clients
                }
                return null; // Continue waiting
            }
        }
        
        private void updateActivity() {
            lastActivity = System.currentTimeMillis();
        }

        private String processCommand(String command) {
            try {
                if (command.startsWith("LOCAL:")) {
                    String instruction = command.substring(6);
                    return vmManager.executeLocalInstruction(sessionId, instruction);
                } else if (command.startsWith("GLOBAL:")) {
                    String instruction = command.substring(7);
                    return vmManager.executeGlobalInstruction(sessionId, instruction);
                } else if (command.equals("SHARE")) {
                    return vmManager.shareToGlobal(sessionId);
                } else if (command.equals("TAKE")) {
                    return vmManager.takeFromGlobal(sessionId);
                } else if (command.equals("GET_USER_STACK")) {
                    return "USER_STACK:" + vmManager.getUserStack(sessionId);
                } else if (command.equals("GET_GLOBAL_STACK")) {
                    return "GLOBAL_STACK:" + vmManager.getGlobalStack();
                } else if (command.equals("GET_GLOBAL_MEMORY")) {
                    return "GLOBAL_MEMORY:" + vmManager.getGlobalMemory();
                } else if (command.equals("GET_USER_MEMORY")) {
                    return "USER_MEMORY:" + vmManager.getUserMemory(sessionId);
                } else if (command.startsWith("CHAT:")) {
                    String message = command.substring(5);
                    return vmManager.addChatMessage(sessionId, message);
                } else if (command.equals("GET_CHAT_MESSAGES")) {
                    return "CHAT_MESSAGES:" + vmManager.getChatMessages();
                } else if (command.equals("PING")) {
                    // Keep-alive response
                    return "PONG";
                } else {
                    return "ERROR: Unknown command: " + command;
                }
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        public void stop() {
            running.set(false);
            try {
                // Note: Add session cleanup if PlanetVMManager supports it
                // if (sessionId != null) {
                //     vmManager.removeUserSession(sessionId);
                // }
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Remove this client from the server's client list
            clients.remove(this);
            System.out.println("Client handler stopped for session: " + sessionId);
        }
    }
}

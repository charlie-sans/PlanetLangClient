package org.finite.planetlangserver.Networking;
import java.io.*;
import java.net.*;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
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
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private boolean running = true;
        private String sessionId;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Create user session
                sessionId = vmManager.createUserSession(clientSocket.getInetAddress().toString());
                out.println("SESSION:" + sessionId);

                String inputLine;
                while (running && (inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    String response = processCommand(inputLine);
                    out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                stop();
            }
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
                } else {
                    return "ERROR: Unknown command: " + command;
                }
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }

        public void stop() {
            running = false;
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

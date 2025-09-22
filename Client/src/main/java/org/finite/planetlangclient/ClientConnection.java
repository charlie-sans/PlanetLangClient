package org.finite.planetlangclient;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.finite.planetlangserver.Networking.Request;
import org.finite.planetlangserver.Networking.Auth;
import org.json.JSONObject;

/**
 * Handles client-side network communication with the PlanetLang server
 * Enhanced with robust connection handling, retry logic, and timeout support
 */
public class ClientConnection {
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000; // 5 seconds
    private static final int DEFAULT_READ_TIMEOUT = 10000; // 10 seconds
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY = 1000; // 1 second
    private static final int DEFAULT_KEEP_ALIVE_INTERVAL = 30000; // 30 seconds
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final AtomicLong lastActivity = new AtomicLong(System.currentTimeMillis());
    private String sessionId;
    private final Object lock = new Object();
    
    // Connection configuration
    private final int connectTimeout;
    private final int readTimeout;
    private final int maxRetries;
    private final long retryDelay;
    
    // Keep-alive functionality
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> keepAliveTask;
    private volatile boolean shouldReconnect = true;
    
    public ClientConnection() {
        this(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY);
    }
    
    public ClientConnection(int connectTimeout, int readTimeout, int maxRetries, long retryDelay) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    /**
     * Connect to the server at the specified host and port with retry logic
     */
    public boolean connect(String host, int port) {
        if (connected.get()) {
            return true;
        }
        
        if (!connecting.compareAndSet(false, true)) {
            // Another thread is already connecting
            while (connecting.get() && !connected.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return connected.get();
        }
        
        try {
            return connectWithRetry(host, port);
        } finally {
            connecting.set(false);
        }
    }
    
    private boolean connectWithRetry(String host, int port) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (attemptConnection(host, port)) {
                    startKeepAlive();
                    System.out.println("Connected to server at " + host + ":" + port + " with session " + sessionId);
                    return true;
                }
            } catch (Exception e) {
                lastException = e;
                System.err.println("Connection attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        System.err.println("Failed to connect after " + maxRetries + " attempts");
        if (lastException != null) {
            System.err.println("Last error: " + lastException.getMessage());
        }
        return false;
    }
    
    private boolean attemptConnection(String host, int port) throws IOException {
        synchronized (lock) {
            // Close any existing connection
            closeConnection();
            
            // Create new socket with timeout
            socket = new Socket();
            socket.setSoTimeout(readTimeout);
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            
            // Connect with timeout
            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            
            // Setup streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Read session ID from server
            String sessionResponse = in.readLine();
            if (sessionResponse != null && sessionResponse.startsWith("SESSION:")) {
                sessionId = sessionResponse.substring(8);
                connected.set(true);
                lastActivity.set(System.currentTimeMillis());
                return true;
            } else {
                System.err.println("Failed to receive session ID from server");
                closeConnection();
                return false;
            }
        }
    }
    
    private void startKeepAlive() {
        if (keepAliveTask != null) {
            keepAliveTask.cancel(false);
        }
        
        keepAliveTask = scheduler.scheduleAtFixedRate(() -> {
            if (connected.get() && 
                System.currentTimeMillis() - lastActivity.get() > DEFAULT_KEEP_ALIVE_INTERVAL) {
                try {
                    synchronized (lock) {
                        if (connected.get()) {
                            out.println("PING");
                            String response = in.readLine();
                            lastActivity.set(System.currentTimeMillis());
                            if (response == null || !response.equals("PONG")) {
                                System.err.println("Keep-alive failed, marking connection as disconnected");
                                connected.set(false);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Keep-alive failed: " + e.getMessage());
                    connected.set(false);
                }
            }
        }, DEFAULT_KEEP_ALIVE_INTERVAL, DEFAULT_KEEP_ALIVE_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    private void closeConnection() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            // Ignore
        }
        
        if (out != null) {
            out.close();
            out = null;
        }
        
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    /**
     * Helper method to execute operations with automatic retry on connection failure
     */
    private String executeWithRetry(java.util.function.Supplier<String> operation) {
        String result = operation.get();
        
        // If operation failed due to connection issues and we should retry
        if (result.startsWith("ERROR:") && shouldReconnect && !connected.get()) {
            // This will be implemented when we add auto-reconnect logic
            // For now, just return the error
        }
        
        return result;
    }

    /**
     * Execute instruction on local (user) stack
     */
    public String executeLocal(String instruction) {
        return executeWithRetry(() -> {
            synchronized (lock) {
                if (!connected.get()) {
                    return "ERROR: Not connected to server";
                }
                out.println("LOCAL:" + instruction);
                try {
                    String response = in.readLine();
                    lastActivity.set(System.currentTimeMillis());
                    return response;
                } catch (IOException e) {
                    connected.set(false);
                    return "ERROR: " + e.getMessage();
                }
            }
        });
    }

    /**
     * Execute instruction on global stack
     */
    public String executeGlobal(String instruction) {
        return executeWithRetry(() -> {
            synchronized (lock) {
                if (!connected.get()) {
                    return "ERROR: Not connected to server";
                }
                out.println("GLOBAL:" + instruction);
                try {
                    String response = in.readLine();
                    lastActivity.set(System.currentTimeMillis());
                    return response;
                } catch (IOException e) {
                    connected.set(false);
                    return "ERROR: " + e.getMessage();
                }
            }
        });
    }

    /**
     * Share value from user stack to global stack
     */
    public String shareToGlobal() {
        return executeWithRetry(() -> {
            synchronized (lock) {
                if (!connected.get()) {
                    return "ERROR: Not connected to server";
                }
                out.println("SHARE");
                try {
                    String response = in.readLine();
                    lastActivity.set(System.currentTimeMillis());
                    return response;
                } catch (IOException e) {
                    connected.set(false);
                    return "ERROR: " + e.getMessage();
                }
            }
        });
    }

    /**
     * Take value from global stack to user stack
     */
    public String takeFromGlobal() {
        return executeWithRetry(() -> {
            synchronized (lock) {
                if (!connected.get()) {
                    return "ERROR: Not connected to server";
                }
                out.println("TAKE");
                try {
                    String response = in.readLine();
                    lastActivity.set(System.currentTimeMillis());
                    return response;
                } catch (IOException e) {
                    connected.set(false);
                    return "ERROR: " + e.getMessage();
                }
            }
        });
    }    /**
     * Get current user stack state
     */
    public String getUserStack() {
        synchronized (lock) {
            if (!connected.get()) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_USER_STACK");
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                if (response != null && response.startsWith("USER_STACK:")) {
                    return response.substring(11);
                }
                return response;
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get current global stack state
     */
    public String getGlobalStack() {
        synchronized (lock) {
            if (!connected.get()) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_GLOBAL_STACK");
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                if (response != null && response.startsWith("GLOBAL_STACK:")) {
                    return response.substring(13);
                }
                return response;
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get global memory state
     */
    public String getGlobalMemory() {
        synchronized (lock) {
            if (!connected.get()) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_GLOBAL_MEMORY");
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                if (response != null && response.startsWith("GLOBAL_MEMORY:")) {
                    return response.substring(14);
                }
                return response;
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get user memory state
     */
    public String getUserMemory() {
        synchronized (lock) {
            if (!connected.get()) {
                return "ERROR: Not connected to server";
            }
            out.println("GET_USER_MEMORY");
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                if (response != null && response.startsWith("USER_MEMORY:")) {
                    return response.substring(12);
                }
                return response;
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Send a chat message
     */
    public String sendChatMessage(String message) {
        synchronized (lock) {
            if (!connected.get()) {
                return "ERROR: Not connected to server";
            }
            out.println("CHAT:" + message);
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                return response;
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }

    /**
     * Get chat messages
     */
    public String getChatMessages() {
        synchronized (lock) {
            if (!connected.get()) {
                return "";
            }
            out.println("GET_CHAT_MESSAGES");
            try {
                String response = in.readLine();
                lastActivity.set(System.currentTimeMillis());
                if (response != null && response.startsWith("CHAT_MESSAGES:")) {
                    return response.substring(14);
                }
                return "";
            } catch (IOException e) {
                connected.set(false);
                return "ERROR: " + e.getMessage();
            }
        }
    }    /**
     * Send a request to the server
     */
    public void sendRequest(Request request) {
        if (!connected.get()) {
            System.err.println("Not connected to server");
            return;
        }
        out.println(request.toString());
        lastActivity.set(System.currentTimeMillis());
    }

    /**
     * Send a simple text message to the server
     */
    public void sendMessage(String message) {
        if (!connected.get()) {
            System.err.println("Not connected to server");
            return;
        }
        out.println(message);
        lastActivity.set(System.currentTimeMillis());
    }

    /**
     * Read a response from the server
     */
    public String readResponse() throws IOException {
        if (!connected.get()) {
            throw new IOException("Not connected to server");
        }
        String response = in.readLine();
        lastActivity.set(System.currentTimeMillis());
        return response;
    }

    /**
     * Check if the client is connected
     */
    public boolean isConnected() {
        return connected.get() && socket != null && !socket.isClosed();
    }

    /**
     * Get the current session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Disconnect from the server and cleanup resources
     */
    public void disconnect() {
        shouldReconnect = false;
        connected.set(false);
        sessionId = null;
        
        if (keepAliveTask != null) {
            keepAliveTask.cancel(false);
        }
        
        closeConnection();
        System.out.println("Disconnected from server");
    }
    
    /**
     * Shutdown the connection and cleanup all resources
     */
    public void shutdown() {
        disconnect();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

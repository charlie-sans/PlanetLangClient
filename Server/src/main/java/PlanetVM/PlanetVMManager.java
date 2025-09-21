package PlanetVM;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the global PlanetVM state and user sessions
 */
public class PlanetVMManager {
    private static PlanetVMManager instance;
    private Runtime globalRuntime;
    private Map<String, Runtime> userRuntimes;
    private Map<String, String> userSessions;
    private List<String> chatMessages;

    private PlanetVMManager() {
        globalRuntime = new Runtime(1024); // 1KB global memory
        userRuntimes = new ConcurrentHashMap<>();
        userSessions = new ConcurrentHashMap<>();
        chatMessages = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized PlanetVMManager getInstance() {
        if (instance == null) {
            instance = new PlanetVMManager();
        }
        return instance;
    }

    /**
     * Create or get a user session
     */
    public String createUserSession(String userId) {
        if (!userRuntimes.containsKey(userId)) {
            userRuntimes.put(userId, new Runtime(256)); // 256 bytes per user
        }
        String sessionId = UUID.randomUUID().toString();
        userSessions.put(sessionId, userId);
        return sessionId;
    }

    /**
     * Execute instruction in user's local runtime
     */
    public String executeLocalInstruction(String sessionId, String instruction) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        Runtime userRuntime = userRuntimes.get(userId);
        if (userRuntime == null) return "ERROR: User runtime not found";

        userRuntime.ExecuteInstruction(instruction);
        return "OK";
    }

    /**
     * Execute instruction in global runtime
     */
    public String executeGlobalInstruction(String sessionId, String instruction) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        globalRuntime.ExecuteInstruction(instruction);
        return "OK";
    }

    /**
     * Share value from user's stack to global stack
     */
    public String shareToGlobal(String sessionId) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        Runtime userRuntime = userRuntimes.get(userId);
        if (userRuntime == null || userRuntime.stack.isEmpty()) {
            return "ERROR: No value to share";
        }

        long value = userRuntime.stack.pop();
        globalRuntime.stack.push(value);
        return "OK";
    }

    /**
     * Take value from global stack to user's stack
     */
    public String takeFromGlobal(String sessionId) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        if (globalRuntime.stack.isEmpty()) {
            return "ERROR: Global stack empty";
        }

        Runtime userRuntime = userRuntimes.get(userId);
        if (userRuntime == null) return "ERROR: User runtime not found";

        long value = globalRuntime.stack.pop();
        userRuntime.stack.push(value);
        return "OK";
    }

    /**
     * Get user's stack state
     */
    public String getUserStack(String sessionId) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        Runtime userRuntime = userRuntimes.get(userId);
        if (userRuntime == null) return "ERROR: User runtime not found";

        return userRuntime.getStackAsString();
    }

    /**
     * Get global stack state
     */
    public String getGlobalStack() {
        return globalRuntime.getStackAsString();
    }

    /**
     * Get global memory state
     */
    public String getGlobalMemory() {
        return globalRuntime.getMemoryAsString();
    }

    /**
     * Get user memory state
     */
    public String getUserMemory(String sessionId) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        Runtime userRuntime = userRuntimes.get(userId);
        if (userRuntime == null) return "ERROR: User runtime not found";

        return userRuntime.getMemoryAsString();
    }

    /**
     * Add a chat message
     */
    public String addChatMessage(String sessionId, String message) {
        String userId = userSessions.get(sessionId);
        if (userId == null) return "ERROR: Invalid session";

        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String formattedMessage = "[" + timestamp + "] " + userId + ": " + message;
        chatMessages.add(formattedMessage);
        
        // Keep only last 100 messages
        if (chatMessages.size() > 100) {
            chatMessages.remove(0);
        }
        
        return "OK";
    }

    /**
     * Get all chat messages
     */
    public String getChatMessages() {
        StringBuilder sb = new StringBuilder();
        for (String message : chatMessages) {
            sb.append(message).append("\n");
        }
        return sb.toString().trim();
    }
}
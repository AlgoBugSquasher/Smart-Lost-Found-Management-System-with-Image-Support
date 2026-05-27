package util;

import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SessionManager handles user session management.
 * Stores the currently logged-in user and provides access control.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    // Flag to track if admin is viewing user dashboard
    private static boolean isAdminViewingUser = false;
    
    // In-memory storage for users
    private static List<User> users = new ArrayList<>();
    
    // Static initializer to create default admin
    static {
        // Create default admin
        users.add(new User(
            UUID.randomUUID().toString(),
            "admin@system.com",
            "admin",
            "System Administrator",
            "ADMIN"
        ));
        
        // Create a sample regular user for testing
        users.add(new User(
            UUID.randomUUID().toString(),
            "user@test.com",
            "user",
            "Test User",
            "USER"
        ));
    }

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    // User management methods
    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static User authenticate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && 
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static boolean addUser(User user) {
        // Check if email already exists
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                return false;
            }
        }
        user.setId(UUID.randomUUID().toString());
        users.add(user);
        return true;
    }

    public static boolean removeUser(String userId) {
        return users.removeIf(u -> u.getId().equals(userId));
    }

    public static User findUserById(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    // Admin viewing user dashboard flag methods
    public static void setAdminViewingUser(boolean value) {
        isAdminViewingUser = value;
    }
    
    public static boolean isAdminViewingUser() {
        return isAdminViewingUser;
    }
}
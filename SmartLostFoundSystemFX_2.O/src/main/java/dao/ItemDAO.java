package dao;

import db.DBConnection;
import model.Item;
import model.User;
import util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemDAO handles all item-related database operations.
 * Uses PreparedStatement to prevent SQL injection.
 */
public class ItemDAO {
    
    /**
     * Add a new item to the database.
     * Automatically assigns user_id from SessionManager.
     * 
     * @param item Item to add
     * @return true if successful, false otherwise
     */
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (item_name, description, location, date, type, image_path, user_id, user_email, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Get current user email from SessionManager
            User currentUser = SessionManager.getInstance().getCurrentUser();
            String userEmail = currentUser != null ? currentUser.getEmail() : item.getUserEmail();
            String userId = currentUser != null ? currentUser.getId() : item.getUserId();
            Integer parsedUserId = null;
            if (userId != null) {
                try {
                    parsedUserId = Integer.parseInt(userId);
                } catch (NumberFormatException e) {
                    parsedUserId = null;
                }
            }
            
            if (userEmail == null || userEmail.isEmpty()) {
                System.err.println("Error adding item: missing user email for item save");
                return false;
            }
            System.err.println("Adding item: name=" + item.getName() + " userId=" + userId + " userEmail=" + userEmail + " type=" + item.getType());
            
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getLocation());
            pstmt.setDate(4, item.getDate() != null ? java.sql.Date.valueOf(item.getDate()) : null);
            pstmt.setString(5, item.getType());
            pstmt.setString(6, item.getImagePath());
            if (parsedUserId != null) {
                pstmt.setInt(7, parsedUserId);
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            pstmt.setString(8, userEmail);
            // Set default status based on type
            String defaultStatus = "Lost".equals(item.getType()) ? "PENDING" : "FOUND";
            pstmt.setString(9, defaultStatus);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            String message = e.getMessage() != null ? e.getMessage() : "";
            if (message.contains("Unknown column 'user_email'") || message.contains("Unknown column: 'user_email'") || e.getErrorCode() == 1054) {
                System.err.println("user_email column missing, trying legacy insert: " + message);
                return addItemWithoutUserEmail(item);
            }
            System.err.println("Error adding item: " + message);
            e.printStackTrace();
            return false;
        }
    }

    private boolean addItemWithoutUserEmail(Item item) {
        String sql = "INSERT INTO items (item_name, description, location, date, type, image_path, user_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            User currentUser = SessionManager.getInstance().getCurrentUser();
            String userId = currentUser != null ? currentUser.getId() : item.getUserId();
            Integer parsedUserId = null;
            if (userId != null) {
                try {
                    parsedUserId = Integer.parseInt(userId);
                } catch (NumberFormatException e) {
                    parsedUserId = null;
                }
            }

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getLocation());
            pstmt.setDate(4, item.getDate() != null ? java.sql.Date.valueOf(item.getDate()) : null);
            pstmt.setString(5, item.getType());
            pstmt.setString(6, item.getImagePath());
            if (parsedUserId != null) {
                pstmt.setInt(7, parsedUserId);
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            String defaultStatus = "Lost".equals(item.getType()) ? "PENDING" : "FOUND";
            pstmt.setString(8, defaultStatus);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Legacy addItemWithoutUserEmail failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all items from database with owner information.
     * Uses JOIN to fetch user name along with items.
     * 
     * @return List of all items with owner names
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        
        // Try with user_email first, fall back to user_id if column doesn't exist
        String sql = "SELECT i.*, u.name AS owner_name " +
                     "FROM items i " +
                     "LEFT JOIN users u ON i.user_id = u.id OR i.user_email = u.email " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("item_id"));
                item.setName(rs.getString("item_name"));
                item.setDescription(rs.getString("description"));
                item.setLocation(rs.getString("location"));
                
                java.sql.Date sqlDate = rs.getDate("date");
                if (sqlDate != null) {
                    item.setDate(sqlDate.toLocalDate());
                }
                
                item.setType(rs.getString("type"));
                item.setImagePath(rs.getString("image_path"));
                item.setUserId(rs.getString("user_id"));
                item.setUserName(rs.getString("owner_name"));
                item.setUserEmail(rs.getString("user_email"));
                item.setStatus(rs.getString("status"));
                
                items.add(item);
            }
            
        } catch (SQLException e) {
            // If user_email column doesn't exist, try fallback query
            System.err.println("Error with user_email query, trying fallback: " + e.getMessage());
            return getAllItemsFallback();
        }
        
        return items;
    }
    
    /**
     * Fallback method when user_email column doesn't exist
     */
    private List<Item> getAllItemsFallback() {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("item_id"));
                item.setName(rs.getString("item_name"));
                item.setDescription(rs.getString("description"));
                item.setLocation(rs.getString("location"));
                
                java.sql.Date sqlDate = rs.getDate("date");
                if (sqlDate != null) {
                    item.setDate(sqlDate.toLocalDate());
                }
                
                item.setType(rs.getString("type"));
                item.setImagePath(rs.getString("image_path"));
                item.setUserId(rs.getString("user_id"));
                item.setUserName(rs.getString("owner_name"));
                item.setUserEmail(rs.getString("owner_email")); // Use email from users table
                item.setStatus(rs.getString("status"));
                
                items.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items (fallback): " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    
    /**
     * Get items by type (LOST or FOUND).
     * 
     * @param type Item type ("Lost" or "Found")
     * @return List of items of the specified type
     */
    public List<Item> getItemsByType(String type) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "LEFT JOIN users u ON i.user_id = u.id OR i.user_email = u.email " +
                     "WHERE i.type = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    try {
                        item.setUserEmail(rs.getString("user_email"));
                    } catch (Exception e) {
                        item.setUserEmail(null);
                    }
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by type: " + e.getMessage());
            // Try fallback
            return getItemsByTypeFallback(type);
        }
        
        return items;
    }
    
    /**
     * Fallback for getItemsByType
     */
    private List<Item> getItemsByTypeFallback(String type) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "WHERE i.type = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    item.setUserEmail(rs.getString("owner_email"));
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by type (fallback): " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Get items for a specific user by email.
     * 
     * @param userEmail User email (foreign key)
     * @return List of items owned by the user
     */
    public List<Item> getItemsByUserEmail(String userEmail) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "LEFT JOIN users u ON i.user_id = u.id OR i.user_email = u.email " +
                     "WHERE i.user_email = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    try {
                        item.setUserEmail(rs.getString("user_email"));
                    } catch (Exception e) {
                        item.setUserEmail(null);
                    }
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by user email: " + e.getMessage());
            // Try fallback
            return getItemsByUserEmailFallback(userEmail);
        }
        
        return items;
    }
    
    /**
     * Fallback for getItemsByUserEmail
     */
    private List<Item> getItemsByUserEmailFallback(String userEmail) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "WHERE u.email = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    item.setUserEmail(rs.getString("owner_email"));
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by user email (fallback): " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Get items for a specific user (legacy method - uses integer user_id).
     * 
     * @param userId User ID
     * @return List of items owned by the user
     */
    public List<Item> getItemsByUser(int userId) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "LEFT JOIN users u ON i.user_id = u.id OR i.user_email = u.email " +
                     "WHERE i.user_id = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    try {
                        item.setUserEmail(rs.getString("user_email"));
                    } catch (Exception e) {
                        item.setUserEmail(null);
                    }
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by user: " + e.getMessage());
            // Try fallback
            return getItemsByUserFallback(userId);
        }
        
        return items;
    }
    
    /**
     * Fallback for getItemsByUser
     */
    private List<Item> getItemsByUserFallback(int userId) {
        List<Item> items = new ArrayList<>();
        
        String sql = "SELECT i.*, u.name AS owner_name, u.email AS owner_email " +
                     "FROM items i " +
                     "JOIN users u ON i.user_id = u.id " +
                     "WHERE i.user_id = ? " +
                     "ORDER BY i.item_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getString("item_id"));
                    item.setName(rs.getString("item_name"));
                    item.setDescription(rs.getString("description"));
                    item.setLocation(rs.getString("location"));
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        item.setDate(sqlDate.toLocalDate());
                    }
                    
                    item.setType(rs.getString("type"));
                    item.setImagePath(rs.getString("image_path"));
                    item.setUserId(rs.getString("user_id"));
                    item.setUserName(rs.getString("owner_name"));
                    item.setUserEmail(rs.getString("owner_email"));
                    item.setStatus(rs.getString("status"));
                    
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching items by user (fallback): " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Delete an item by ID.
     * 
     * @param itemId Item ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing item.
     * 
     * @param item Item with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET item_name = ?, description = ?, location = ?, date = ?, type = ?, image_path = ? WHERE item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getLocation());
            pstmt.setDate(4, item.getDate() != null ? java.sql.Date.valueOf(item.getDate()) : null);
            pstmt.setString(5, item.getType());
            pstmt.setString(6, item.getImagePath());
            pstmt.setInt(7, Integer.parseInt(item.getId()));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update item status (e.g., mark as found).
     * 
     * @param itemId Item ID to update
     * @param status New status value
     * @return true if successful, false otherwise
     */
    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE items SET status = ?, type = ? WHERE item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("[DEBUG-DAO] updateItemStatus called: itemId=" + itemId + ", status=" + status);
            pstmt.setString(1, status);
            // When marking as FOUND, change type to Found; otherwise keep type as Lost
            String newType = "FOUND".equals(status) ? "Found" : "Lost";
            pstmt.setString(2, newType);
            pstmt.setInt(3, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("[DEBUG-DAO] rowsAffected: " + rowsAffected + ", newType: " + newType);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating item status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
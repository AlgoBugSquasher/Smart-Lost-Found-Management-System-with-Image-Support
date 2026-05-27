package util;

import dao.ItemDAO;
import model.Item;
import model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ItemManager handles all item-related operations.
 * Now uses ItemDAO for database operations.
 */
public class ItemManager {
    private static ItemManager instance;
    private static ItemDAO itemDAO = new ItemDAO();

    private ItemManager() {
    }

    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    // Create a new item - uses database
    public static Item addItem(String name, String description, String location,
                               LocalDate date, String imagePath, String type, String userId) {
        // Create item object
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setLocation(location);
        item.setDate(date);
        item.setImagePath(imagePath);
        item.setType(type);
        item.setUserId(userId);
        
        // Save to database
        if (itemDAO.addItem(item)) {
            // Get the saved item with ID from database
            List<Item> items = itemDAO.getAllItems();
            if (!items.isEmpty()) {
                return items.get(0); // Return the most recent
            }
        }
        return null;
    }

    // Get all items from database
    public static List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }

    // Get items by user ID (legacy)
    public static List<Item> getItemsByUserId(String userId) {
        return itemDAO.getItemsByUser(Integer.parseInt(userId));
    }

    // Get items by user email (foreign key)
    public static List<Item> getItemsByUserEmail(String userEmail) {
        return itemDAO.getItemsByUserEmail(userEmail);
    }

    // Get items by type (Lost or Found)
    public static List<Item> getItemsByType(String type) {
        return itemDAO.getItemsByType(type);
    }
    
    // Search lost items by name, description, or location (for matching found items)
    public static List<Item> searchLostItems(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getItemsByType("Lost");
        }
        String lowerSearch = searchText.toLowerCase().trim();
        return getItemsByType("Lost").stream()
            .filter(item -> 
                (item.getName() != null && item.getName().toLowerCase().contains(lowerSearch)) ||
                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerSearch)) ||
                (item.getLocation() != null && item.getLocation().toLowerCase().contains(lowerSearch)))
            .collect(Collectors.toList());
    }

    // Find item by ID
    public static Item findItemById(String itemId) {
        List<Item> items = itemDAO.getAllItems();
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    // Update an item
    public static boolean updateItem(String itemId, String name, String description,
                                     String location, LocalDate date, String imagePath) {
        Item item = findItemById(itemId);
        if (item != null) {
            item.setName(name);
            item.setDescription(description);
            item.setLocation(location);
            item.setDate(date);
            if (imagePath != null && !imagePath.isEmpty()) {
                item.setImagePath(imagePath);
            }
            return itemDAO.updateItem(item);
        }
        return false;
    }

    // Delete an item
    public static boolean deleteItem(String itemId) {
        return itemDAO.deleteItem(Integer.parseInt(itemId));
    }
    
    // Update item status (mark as found)
    public static boolean markAsFound(String itemId) {
        try {
            System.out.println("[DEBUG-IM] markAsFound called with itemId: " + itemId);
            int id = Integer.parseInt(itemId);
            System.out.println("[DEBUG-IM] Parsed ID: " + id);
            boolean result = itemDAO.updateItemStatus(id, "FOUND");
            System.out.println("[DEBUG-IM] updateItemStatus returned: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[ERROR-IM] Exception in markAsFound: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Update item status (mark as pending)
    public static boolean markAsPending(String itemId) {
        return itemDAO.updateItemStatus(Integer.parseInt(itemId), "PENDING");
    }

    // Update item status (Lost to Found)
    public static boolean updateItemStatus(String itemId, String newStatus) {
        try {
            int id = Integer.parseInt(itemId);
            return itemDAO.updateItemStatus(id, newStatus);
        } catch (Exception e) {
            System.err.println("Error updating item status: " + e.getMessage());
            return false;
        }
    }

    // Check if user owns an item
    public static boolean isOwner(String itemId, String userId) {
        Item item = findItemById(itemId);
        return item != null && item.getUserId().equals(userId);
    }

    // Get item count
    public static int getItemCount() {
        return itemDAO.getAllItems().size();
    }

    // Clear all items (for testing)
    public static void clearAll() {
        // Not implemented for safety
    }
}
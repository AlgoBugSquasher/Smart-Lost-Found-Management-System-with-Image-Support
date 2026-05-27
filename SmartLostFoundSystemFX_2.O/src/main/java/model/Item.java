package model;

import java.time.LocalDate;

/**
 * Item model class representing a lost or found item.
 * Contains all details about an item including image path.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private String location;
    private LocalDate date;
    private String imagePath;
    private String type; // "Lost" or "Found"
    private String status; // "LOST", "FOUND", "PENDING"
    private String userId; // Owner of the item
    private String userName; // Owner's name for display
    private String userEmail; // Owner's email (foreign key)

    public Item() {
    }

    public Item(String id, String name, String description, String location, 
                LocalDate date, String imagePath, String type, String userId, String userName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.imagePath = imagePath;
        this.type = type;
        this.userId = userId;
        this.userName = userName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " - " + type;
    }
}
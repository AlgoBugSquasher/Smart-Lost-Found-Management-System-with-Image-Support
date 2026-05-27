package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import util.ItemManager;
import util.SessionManager;
import model.User;
import java.util.List;

/**
 * AdminDashboardController handles the admin dashboard functionality.
 * Provides admin-specific features like user management and system overview.
 */
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Text adminInfoText;

    @FXML
    private Text totalItemsCount;

    @FXML
    private Text totalUsersCount;

    @FXML
    private Text lostItemsCount;

    @FXML
    private Text foundItemsCount;

    @FXML
    public void initialize() {
        // Set welcome message
        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
            adminInfoText.setText("Admin: " + currentUser.getEmail());
        }
        
        // Update statistics
        updateStatistics();
    }

    private void updateStatistics() {
        var items = ItemManager.getAllItems();
        var users = SessionManager.getAllUsers();
        
        long lostCount = items.stream().filter(i -> "Lost".equals(i.getType())).count();
        long foundCount = items.stream().filter(i -> "Found".equals(i.getType())).count();
        
        totalItemsCount.setText(String.valueOf(items.size()));
        totalUsersCount.setText(String.valueOf(users.size()));
        lostItemsCount.setText(String.valueOf(lostCount));
        foundItemsCount.setText(String.valueOf(foundCount));
    }

    @FXML
    private void handleViewAllItems() {
        MainApp.showViewItems();
    }

    @FXML
    private void handleViewUsers() {
        showUsersDialog();
    }

    @FXML
    private void handleAddAdmin() {
        MainApp.showAddAdmin();
    }

    @FXML
    private void handleUserDashboard() {
        SessionManager.setAdminViewingUser(true);
        MainApp.showUserDashboard();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        
        if (alert.showAndWait().isPresent() && alert.getResult().getText().equals("OK")) {
            SessionManager.getInstance().logout();
            MainApp.showLoginScreen();
        }
    }

    private void showUsersDialog() {
        List<User> users = SessionManager.getAllUsers();
        StringBuilder sb = new StringBuilder("All Users:\n\n");
        
        for (User user : users) {
            sb.append("Name: ").append(user.getName()).append("\n");
            sb.append("Email: ").append(user.getEmail()).append("\n");
            sb.append("Role: ").append(user.getRole()).append("\n");
            sb.append("-".repeat(30)).append("\n");
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Users");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
}
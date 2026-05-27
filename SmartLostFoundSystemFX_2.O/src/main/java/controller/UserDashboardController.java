package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import util.SessionManager;

/**
 * UserDashboardController handles the user dashboard functionality.
 * Provides navigation to report lost/found items and view items.
 */
public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Text userInfoText;
    
    @FXML
    private Button backToAdminButton;

    @FXML
    private void initialize() {
        // Set welcome message
        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
            userInfoText.setText("Logged in as: " + currentUser.getEmail() + " (" + currentUser.getRole() + ")");
        }
        
        // Show "Back to Admin Panel" button only if admin is viewing user dashboard
        if (backToAdminButton != null) {
            backToAdminButton.setVisible(SessionManager.isAdminViewingUser());
        }
    }

    @FXML
    private void handleReportLost() {
        MainApp.showLostForm();
    }

    @FXML
    private void handleReportFound() {
        MainApp.showFoundForm();
    }

    @FXML
    private void handleViewItems() {
        MainApp.showViewAllItems();
    }

    @FXML
    private void handleMyItems() {
        MainApp.showViewMyItems();
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
    
    @FXML
    private void handleBackToAdmin() {
        SessionManager.setAdminViewingUser(false);
        MainApp.showAdminDashboard();
    }
}
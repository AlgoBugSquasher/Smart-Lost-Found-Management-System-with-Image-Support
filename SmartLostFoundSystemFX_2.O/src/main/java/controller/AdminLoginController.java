package controller;

import app.MainApp;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import util.SessionManager;

/**
 * AdminLoginController handles admin-specific authentication.
 * Only users with ADMIN role can login through this screen.
 */
public class AdminLoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    
    private UserDAO userDAO;
    
    public AdminLoginController() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleAdminLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter both admin email and password.");
            return;
        }

        // Authenticate user using database
        User user = userDAO.loginUser(email, password);
        
        if (user != null) {
            // Check if user is an admin
            if (user.isAdmin()) {
                // Admin login successful
                SessionManager.getInstance().login(user);
                
                showAlert(Alert.AlertType.INFORMATION, "Admin Login Successful", 
                         "Welcome Admin " + user.getName() + "!");
                
                // Redirect to admin dashboard
                MainApp.showAdminDashboard();
            } else {
                // User is not an admin
                showAlert(Alert.AlertType.ERROR, "Access Denied", 
                         "This login is for administrators only.\n" +
                         "Please use the User Login page.");
                passwordField.clear();
                passwordField.requestFocus();
            }
        } else {
            // Login failed
            showAlert(Alert.AlertType.ERROR, "Login Failed", 
                     "Invalid admin email or password. Please try again.");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    @FXML
    private void handleBackToUserLogin() {
        MainApp.showLoginScreen();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
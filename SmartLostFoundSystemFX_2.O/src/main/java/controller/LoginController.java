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
 * LoginController handles user authentication.
 * Validates credentials and redirects to appropriate dashboard.
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    
    private UserDAO userDAO;
    
    public LoginController() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter both email and password.");
            return;
        }

        // Authenticate user using database
        User user = userDAO.loginUser(email, password);
        
        if (user != null) {
            // Login successful
            SessionManager.getInstance().login(user);
            
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", 
                     "Welcome " + user.getName() + "!");
            
            // Redirect based on role - only allow regular users
            if (user.isAdmin()) {
                showAlert(Alert.AlertType.WARNING, "Admin Access", 
                         "Please use the Admin Login button for administrator access.");
                SessionManager.getInstance().logout();
                return;
            } else {
                MainApp.showUserDashboard();
            }
        } else {
            // Login failed
            showAlert(Alert.AlertType.ERROR, "Login Failed", 
                     "Invalid email or password. Please try again.");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    @FXML
    private void handleUserLogin() {
        // Switch to user login form (already visible)
        emailField.requestFocus();
    }
    
    @FXML
    private void handleAdminLogin() {
        MainApp.showAdminLoginScreen();
    }
    
    @FXML
    private void handleRegisterLink() {
        MainApp.showRegisterScreen();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package controller;

import app.MainApp;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

/**
 * RegisterController handles user registration.
 * Validates input and registers new users in the database.
 */
public class RegisterController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    private UserDAO userDAO;
    
    public RegisterController() {
        userDAO = new UserDAO();
    }
    
    @FXML
    private void handleRegister() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed", 
                     "Email already registered. Please use a different email.");
            emailField.requestFocus();
            return;
        }
        
        // Create new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole("USER"); // Default role
        
        // Register user
        if (userDAO.registerUser(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", 
                     "Your account has been created!\n\n" +
                     "You can now login with your credentials.");
            clearForm();
            MainApp.showLoginScreen();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                     "Unable to create account.\n\n" +
                     "Possible causes:\n" +
                     "- Email already exists\n" +
                     "- Database connection issue\n\n" +
                     "Please try a different email or check database.");
        }
    }
    
    @FXML
    private void handleBack() {
        MainApp.showLoginScreen();
    }
    
    @FXML
    private void handleLoginLink() {
        MainApp.showLoginScreen();
    }
    
    private boolean validateInput() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Check name
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter your name.");
            nameField.requestFocus();
            return false;
        }
        
        // Check email
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter your email.");
            emailField.requestFocus();
            return false;
        }
        
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        // Check password
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please enter a password.");
            passwordField.requestFocus();
            return false;
        }
        
        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Password must be at least 4 characters.");
            passwordField.requestFocus();
            return false;
        }
        
        // Check confirm password
        if (confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Please confirm your password.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Check password match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                     "Passwords do not match.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
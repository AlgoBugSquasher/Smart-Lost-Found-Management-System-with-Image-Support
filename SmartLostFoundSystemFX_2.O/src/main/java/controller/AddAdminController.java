package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import util.SessionManager;

/**
 * AddAdminController handles adding new admin users.
 * Only existing admins can add new admins.
 * SECURITY: Requires current user to be logged in as admin.
 */
public class AddAdminController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void initialize() {
        // Security check: Verify current user is an admin
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", 
                     "You must be logged in as an admin to access this page.");
            MainApp.showLoginScreen();
            return;
        }
    }

    @FXML
    private void handleSubmit() {
        // Double-check security
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", 
                     "Your session has expired. Please login as admin.");
            MainApp.showLoginScreen();
            return;
        }

        // Validation
        if (!validateInput()) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Create new admin user
        User newAdmin = new User();
        newAdmin.setName(name);
        newAdmin.setEmail(email);
        newAdmin.setPassword(password);
        newAdmin.setRole("ADMIN");

        // Add user through SessionManager
        if (SessionManager.addUser(newAdmin)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                     "New admin added successfully!\n\n" +
                     "Email: " + email + "\n" +
                     "Password: " + password + "\n\n" +
                     "Added by: " + currentUser.getName());
            clearForm();
            MainApp.showAdminDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to add admin. Email may already exist.");
            emailField.requestFocus();
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.showAdminDashboard();
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter name.");
            nameField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter email.");
            emailField.requestFocus();
            return false;
        }

        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter password.");
            passwordField.requestFocus();
            return false;
        }

        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 4 characters.");
            passwordField.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
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
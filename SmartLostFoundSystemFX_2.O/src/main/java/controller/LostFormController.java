package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import util.ItemManager;
import util.SessionManager;
import java.io.File;

/**
 * LostFormController handles the lost item report form.
 * Validates input and creates new lost items.
 */
public class LostFormController {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField imagePathField;

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Item Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(MainApp.getPrimaryStage());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleSubmit() {
        // Validation
        if (!validateInput()) {
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String location = locationField.getText().trim();
        java.time.LocalDate date = datePicker.getValue();
        String imagePath = imagePathField.getText().trim();

        // Get current user
        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
            MainApp.showLoginScreen();
            return;
        }

        // Create the item
        try {
            var addedItem = ItemManager.addItem(name, description, location, date, imagePath, "Lost", currentUser.getId());
            if (addedItem == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to report lost item. Please try again.");
                return;
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to report lost item: " + e.getMessage());
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Lost item reported successfully!");
        clearForm();
        
        // Navigate back based on admin viewing flag
        if (SessionManager.isAdminViewingUser()) {
            MainApp.showAdminDashboard();
        } else {
            MainApp.showUserDashboard();
        }
    }

    @FXML
    private void handleCancel() {
        // Always go back to User Dashboard (admin can use "Back to Admin Panel" button to return)
        MainApp.showUserDashboard();
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String location = locationField.getText().trim();
        java.time.LocalDate date = datePicker.getValue();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter item name.");
            nameField.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter description.");
            descriptionField.requestFocus();
            return false;
        }

        if (location.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter location.");
            locationField.requestFocus();
            return false;
        }

        if (date == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select date.");
            datePicker.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        locationField.clear();
        datePicker.setValue(null);
        imagePathField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
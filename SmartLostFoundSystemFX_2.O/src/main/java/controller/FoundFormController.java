package controller;

import app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.Item;
import util.ItemManager;
import util.SessionManager;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FoundFormController handles the found item report form.
 * Validates input and creates new found items.
 * Includes matching lost items functionality.
 */
public class FoundFormController {

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
    private ListView<Item> matchingItemsList;
    
    @FXML
    private Label matchLabel;
    
    @FXML
    private Button searchMatchButton;
    
    @FXML
    private Button linkItemButton;
    
    @FXML
    private Button reportNewButton;
    
    private ObservableList<Item> matchingItems;
    private Item selectedLostItem;

    @FXML
    private void initialize() {
        matchingItems = FXCollections.observableArrayList();
        if (matchingItemsList != null) {
            matchingItemsList.setItems(matchingItems);
            matchingItemsList.setCellFactory(param -> new javafx.scene.control.ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " - " + item.getDescription() + " (Location: " + item.getLocation() + ", Date: " + item.getDate() + ")");
                    }
                }
            });
            matchingItemsList.setOnMouseClicked(event -> {
                if (!matchingItemsList.getSelectionModel().isEmpty()) {
                    selectedLostItem = matchingItemsList.getSelectionModel().getSelectedItem();
                }
            });
        }
    }

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
    private void handleSearchMatching() {
        String searchText = nameField.getText().trim();
        if (searchText.isEmpty()) {
            searchText = descriptionField.getText().trim();
        }
        if (searchText.isEmpty()) {
            searchText = locationField.getText().trim();
        }
        
        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search", "Please enter some details to search for matching lost items.");
            return;
        }
        
        // Search for matching lost items
        List<Item> matched = ItemManager.searchLostItems(searchText);
        
        if (matched.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Matches", "No matching lost items found. You can report this as a new found item.");
            matchingItems.clear();
            if (matchLabel != null) matchLabel.setText("No matching lost items found");
        } else {
            matchingItems.setAll(matched);
            if (matchLabel != null) matchLabel.setText("Found " + matched.size() + " matching lost item(s) - select one if this is the item you found");
            showAlert(Alert.AlertType.INFORMATION, "Matches Found", "Found " + matched.size() + " matching lost item(s). Select one from the list if this is the item you found, or report as new found item.");
        }
    }
    
    @FXML
    private void handleLinkToLostItem() {
        if (selectedLostItem == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a lost item from the matching list.");
            return;
        }
        
        // Update the lost item status to FOUND
        if (ItemManager.markAsFound(selectedLostItem.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Item linked successfully! The lost item '" + selectedLostItem.getName() + "' has been marked as found.\n\n" +
                "Original Reporter: " + selectedLostItem.getUserName() + "\n" +
                "Location Found: " + locationField.getText().trim() + "\n" +
                "Date Found: " + (datePicker.getValue() != null ? datePicker.getValue().toString() : "N/A"));
            
            // Optionally create a found record as well
            var currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                ItemManager.addItem(
                    selectedLostItem.getName(),
                    descriptionField.getText().trim(),
                    locationField.getText().trim(),
                    datePicker.getValue(),
                    imagePathField.getText().trim(),
                    "Found",
                    currentUser.getId()
                );
            }
            
            clearForm();
            MainApp.showUserDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to link item. Please try again.");
        }
    }
    
    @FXML
    private void handleReportNew() {
        // Report as a new found item (not linking to existing lost item)
        handleSubmit();
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
        var addedItem = ItemManager.addItem(name, description, location, date, imagePath, "Found", currentUser.getId());
        if (addedItem == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to report found item. Please try again.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Found item reported successfully!");
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
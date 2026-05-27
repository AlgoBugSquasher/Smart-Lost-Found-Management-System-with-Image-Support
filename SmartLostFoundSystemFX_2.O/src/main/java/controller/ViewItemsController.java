package controller;

import app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;
import model.Item;
import util.ItemManager;
import util.SessionManager;
import java.util.List;
import java.util.stream.Collectors;

public class ViewItemsController {

    @FXML
    private TableView<Item> itemsTable;

    @FXML
    private TextField searchField;
    
    @FXML
    private Button lostItemsButton;
    
    @FXML
    private Button foundItemsButton;
    
    @FXML
    private Button allItemsButton;
    
    private String viewMode = "all"; // "all", "my", "lost", "found"
    private String filterMode = "all"; // "all", "lost", "found"
    
    public void setViewMode(String mode) {
        this.viewMode = mode;
        if (itemsTable != null) {
            loadItems();
        }
    }
    
    public String getViewMode() {
        return viewMode;
    }

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, String> descriptionColumn;

    @FXML
    private TableColumn<Item, String> locationColumn;

    @FXML
    private TableColumn<Item, String> dateColumn;

    @FXML
    private TableColumn<Item, String> typeColumn;

    @FXML
    private TableColumn<Item, String> ownerColumn;

    @FXML
    private TableColumn<Item, String> actionColumn;

    @FXML
    private Text placeholderText;
    
    @FXML
    private GridPane detailGrid;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label typeLabel;
    
    @FXML
    private Label ownerLabel;
    
    @FXML
    private VBox imageSection;
    
    @FXML
    private ImageView itemImageView;
    
    @FXML
    private VBox detailPanel;

    private ObservableList<Item> itemList;
    private ObservableList<Item> filteredList;

    @FXML
    public void initialize() {
        try {
            if (setupTable()) {
                setupSelectionListener();
                loadItems();
            }
        } catch (Exception e) {
            System.err.println("Error in initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean setupTable() {
        try {
            if (itemsTable == null || nameColumn == null) {
                System.err.println("Table or columns not initialized");
                return false;
            }
            
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            ownerColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
            actionColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

            actionColumn.setCellFactory(new Callback<TableColumn<Item, String>, TableCell<Item, String>>() {
                @Override
                public TableCell<Item, String> call(TableColumn<Item, String> param) {
                    return new TableCell<Item, String>() {
                        @Override
                        protected void updateItem(String itemId, boolean empty) {
                            super.updateItem(itemId, empty);
                            if (empty || itemId == null) {
                                setGraphic(null);
                                return;
                            }
                            
                            // Get the item for this row
                            Item item = getTableView().getItems().get(getIndex());
                            HBox buttons = new HBox(5);
                            
                            // Add "Mark Found" button for Lost items (only for owners)
                            if (item != null && "Lost".equalsIgnoreCase(item.getType())) {
                                var currentUser = SessionManager.getInstance().getCurrentUser();
                                if (currentUser != null && item.getUserId().equals(currentUser.getId())) {
                                    Button markFoundBtn = new Button("Mark Found");
                                    markFoundBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                                    markFoundBtn.setOnAction(event -> handleMarkAsFound(itemId));
                                    buttons.getChildren().add(markFoundBtn);
                                }
                            }
                            
                            // Add Delete button (for owners and admins)
                            Button deleteBtn = new Button("Delete");
                            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                            deleteBtn.setOnAction(event -> handleDelete(itemId));
                            buttons.getChildren().add(deleteBtn);
                            
                            setGraphic(buttons);
                        }
                    };
                }
            });
            return true;
        } catch (Exception e) {
            System.err.println("Error setting up table: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void setupSelectionListener() {
        try {
            if (itemsTable != null) {
                itemsTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            showItemDetails(newValue);
                        } else {
                            clearDetails();
                        }
                    }
                );
            }
        } catch (Exception e) {
            System.err.println("Error setting up selection listener: " + e.getMessage());
        }
    }

    @FXML
    public void showItemDetails(Item item) {
        try {
            if (placeholderText != null) placeholderText.setVisible(false);
            if (detailGrid != null) detailGrid.setVisible(true);
            
            if (nameLabel != null) nameLabel.setText(item.getName() != null ? item.getName() : "");
            if (descriptionLabel != null) descriptionLabel.setText(item.getDescription() != null ? item.getDescription() : "");
            if (locationLabel != null) locationLabel.setText(item.getLocation() != null ? item.getLocation() : "");
            if (dateLabel != null) dateLabel.setText(item.getDate() != null ? item.getDate().toString() : "");
            if (typeLabel != null) typeLabel.setText(item.getType() != null ? item.getType() : "");
            if (ownerLabel != null) ownerLabel.setText(item.getUserName() != null ? item.getUserName() : "Unknown");
            
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                try {
                    String imagePath = item.getImagePath();
                    String urlPath = imagePath.replace("\\", "/");
                    if (!urlPath.startsWith("file:/")) {
                        urlPath = "file:///" + urlPath;
                    }
                    Image image = new Image(urlPath, true);
                    if (!image.isError() && itemImageView != null) {
                        itemImageView.setImage(image);
                        if (imageSection != null) imageSection.setVisible(true);
                    } else {
                        showPlaceholderImage();
                    }
                } catch (Exception e) {
                    showPlaceholderImage();
                }
            } else {
                showPlaceholderImage();
            }
        } catch (Exception e) {
            System.err.println("Error showing item details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showPlaceholderImage() {
        try {
            if (itemImageView != null) itemImageView.setImage(null);
            if (imageSection != null) imageSection.setVisible(false);
        } catch (Exception e) {
            System.err.println("Error showing placeholder: " + e.getMessage());
        }
    }

    @FXML
    public void clearDetails() {
        try {
            if (placeholderText != null) placeholderText.setVisible(true);
            if (detailGrid != null) detailGrid.setVisible(false);
            if (imageSection != null) imageSection.setVisible(false);
            
            if (nameLabel != null) nameLabel.setText("");
            if (descriptionLabel != null) descriptionLabel.setText("");
            if (locationLabel != null) locationLabel.setText("");
            if (dateLabel != null) dateLabel.setText("");
            if (typeLabel != null) typeLabel.setText("");
            if (ownerLabel != null) ownerLabel.setText("");
            if (itemImageView != null) itemImageView.setImage(null);
        } catch (Exception e) {
            System.err.println("Error clearing details: " + e.getMessage());
        }
    }

    private void loadItems() {
        try {
            List<Item> items;
            
            if ("my".equals(viewMode)) {
                // "My Items" mode - show only current user's lost items
                var currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "User session not found. Please login again.");
                    MainApp.showLoginScreen();
                    return;
                }
                // Use user's email as foreign key to filter items
                String userEmail = currentUser.getEmail();
                items = ItemManager.getItemsByUserEmail(userEmail).stream()
                    .filter(item -> "Lost".equalsIgnoreCase(item.getType()))
                    .collect(Collectors.toList());
            } else {
                // "View All Items" mode - show all items
                items = ItemManager.getAllItems();
            }
            
            // Apply filter based on filterMode (lost, found, or all)
            if ("lost".equals(filterMode)) {
                items = items.stream()
                    .filter(item -> "Lost".equalsIgnoreCase(item.getType()))
                    .collect(Collectors.toList());
            } else if ("found".equals(filterMode)) {
                items = items.stream()
                    .filter(item -> "Found".equalsIgnoreCase(item.getType()))
                    .collect(Collectors.toList());
            }
            
            itemList = FXCollections.observableArrayList(items);
            filteredList = FXCollections.observableArrayList(items);
            if (itemsTable != null) {
                itemsTable.setItems(filteredList);
            }
        } catch (Exception e) {
            System.err.println("Error loading items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        try {
            if (searchField == null || itemList == null || filteredList == null) {
                return;
            }
            String searchText = searchField.getText().toLowerCase().trim();
            if (searchText.isEmpty()) {
                filteredList.setAll(itemList);
            } else {
                List<Item> filtered = itemList.stream()
                    .filter(item -> 
                        (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText)) ||
                        (item.getLocation() != null && item.getLocation().toLowerCase().contains(searchText)) ||
                        (item.getType() != null && item.getType().toLowerCase().contains(searchText)) ||
                        (item.getUserName() != null && item.getUserName().toLowerCase().contains(searchText)))
                    .collect(Collectors.toList());
                filteredList.setAll(filtered);
            }
        } catch (Exception e) {
            System.err.println("Error in search: " + e.getMessage());
        }
    }

    @FXML
    private void handleMarkAsFound(String itemId) {
        try {
            System.out.println("[DEBUG] handleMarkAsFound called with itemId: " + itemId);
            
            var currentUser = SessionManager.getInstance().getCurrentUser();
            System.out.println("[DEBUG] Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));
            
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User session not found.");
                return;
            }

            // Verify ownership
            boolean isOwner = ItemManager.isOwner(itemId, currentUser.getId());
            System.out.println("[DEBUG] isOwner check: " + isOwner + " (itemId=" + itemId + ", userId=" + currentUser.getId() + ")");
            
            if (!isOwner) {
                showAlert(Alert.AlertType.WARNING, "Permission Denied", 
                         "You can only update your own items.");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Mark Item as Found");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to mark this item as found? This will update the status from Lost to Found.");
            
            var result = confirmAlert.showAndWait();
            System.out.println("[DEBUG] Confirmation dialog result: " + result.orElse(null));
            
            if (result.orElse(null) == ButtonType.OK) {
                System.out.println("[DEBUG] OK button clicked, calling markAsFound()");
                boolean updateSuccess = ItemManager.markAsFound(itemId);
                System.out.println("[DEBUG] markAsFound returned: " + updateSuccess);
                
                if (updateSuccess) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item marked as found successfully!");
                    System.out.println("[DEBUG] About to call loadItems()");
                    loadItems();
                    System.out.println("[DEBUG] loadItems() completed");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update item status.");
                }
            } else {
                System.out.println("[DEBUG] Confirmation dialog cancelled or dismissed");
            }
        } catch (Exception e) {
            System.err.println("Error marking item as found: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to mark item as found: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(String itemId) {
        try {
            var currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User session not found.");
                return;
            }

            boolean isAdmin = currentUser.isAdmin();
            boolean isOwner = ItemManager.isOwner(itemId, currentUser.getId());

            if (!isAdmin && !isOwner) {
                showAlert(Alert.AlertType.WARNING, "Permission Denied", 
                         "You can only delete your own items.");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete this item?");
            
            if (confirmAlert.showAndWait().orElse(null) == ButtonType.OK) {
                if (ItemManager.deleteItem(itemId)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully!");
                    loadItems();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete item.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting item: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete item: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Always go back to User Dashboard (admin can use "Back to Admin Panel" button to return)
            MainApp.showUserDashboard();
        } catch (Exception e) {
            System.err.println("Error in handleBack: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            loadItems();
            if (searchField != null) {
                searchField.clear();
            }
        } catch (Exception e) {
            System.err.println("Error in handleRefresh: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowLostItems() {
        try {
            filterMode = "lost";
            loadItems();
        } catch (Exception e) {
            System.err.println("Error showing lost items: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowFoundItems() {
        try {
            filterMode = "found";
            loadItems();
        } catch (Exception e) {
            System.err.println("Error showing found items: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAllItems() {
        try {
            filterMode = "all";
            loadItems();
        } catch (Exception e) {
            System.err.println("Error showing all items: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing alert: " + e.getMessage());
        }
    }
}
package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.SessionManager;

/**
 * MainApp is the entry point for the JavaFX application.
 * Handles scene switching and stage management.
 */
public class MainApp extends Application {
    
    private static Stage primaryStage;
    private static Scene currentScene;

    /**
     * Helper method to switch scenes while preserving window state.
     */
    private static void switchScene(Parent root, String title) {
        boolean isMaximized = primaryStage.isMaximized();
        
        // Preserve current scene size if a scene exists
        double sceneWidth = 800;
        double sceneHeight = 500;
        if (primaryStage.getScene() != null) {
            sceneWidth = primaryStage.getScene().getWidth();
            sceneHeight = primaryStage.getScene().getHeight();
        }
        
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.getStylesheets().add(
            MainApp.class.getResource("/ui/styles.css").toExternalForm()
        );
        
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setMaximized(isMaximized);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Smart Lost & Found Management System");
        
        // Show login screen first
        showLoginScreen();
        
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/Login.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Smart Lost & Found - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load login screen: " + e.getMessage());
        }
    }
    
    public static void showAdminLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/AdminLogin.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Smart Lost & Found - Admin Login");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load admin login screen: " + e.getMessage());
        }
    }
    
    public static void showRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/Register.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Smart Lost & Found - Register");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load register screen: " + e.getMessage());
        }
    }

    public static void showUserDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/UserDashboard.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Smart Lost & Found - User Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load user dashboard: " + e.getMessage());
        }
    }

    public static void showAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/AdminDashboard.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Smart Lost & Found - Admin Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load admin dashboard: " + e.getMessage());
        }
    }

    public static void showLostForm() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/LostForm.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Report Lost Item");
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(550);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load lost form: " + e.getMessage());
        }
    }

    public static void showFoundForm() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/FoundForm.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Report Found Item");
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(550);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load found form: " + e.getMessage());
        }
    }

    public static void showViewItems() {
        showViewAllItems();
    }
    
    public static void showViewAllItems() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/ViewItems.fxml")
            );
            Parent root = loader.load();
            
            // Set flag to show all items (not just user's)
            controller.ViewItemsController controller = loader.getController();
            controller.setViewMode("all");
            
            switchScene(root, "View All Items");
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load view items: " + e.getMessage());
        }
    }
    
    public static void showViewMyItems() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/ViewItems.fxml")
            );
            Parent root = loader.load();
            
            // Set flag to show only current user's lost items
            controller.ViewItemsController controller = loader.getController();
            controller.setViewMode("my");
            
            switchScene(root, "My Items");
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(600);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load my items: " + e.getMessage());
        }
    }

    public static void showAddAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/AddAdmin.fxml")
            );
            Parent root = loader.load();
            
            switchScene(root, "Add New Admin");
            primaryStage.setMinWidth(500);
            primaryStage.setMinHeight(450);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load add admin screen: " + e.getMessage());
        }
    }

    public static void showInfoAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarningAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
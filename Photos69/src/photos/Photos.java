package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.DataStore;

/**
 * Main application class for the Photos application.
 * Handles the initialization and scene switching for the app.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class Photos extends Application {
    private static Stage primaryStage;

    /**
     * Starts the application by initializing the primary stage and loading the login scene.
     * 
     * @param stage the primary stage for this application
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Load existing users or create defaults (admin + stock)
        DataStore.loadOrInit();

        switchScene("/photos/view/login.fxml", "Photos - Login");
        stage.setResizable(false);

        // Safe quit: save user data on window close
        stage.setOnCloseRequest(e -> DataStore.saveUsers());

        stage.show();
    }

    /**
     * Switches the current scene to the specified FXML file and updates the window title.
     * 
     * @param fxml the path to the FXML file
     * @param title the title of the window
     */
    public static void switchScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Photos.class.getResource(fxml));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the primary stage of the application.
     * 
     * @return the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * The main entry point for the application.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

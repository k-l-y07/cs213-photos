package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.AppState;

/**
 * Main application launcher for the Photos project.
 *
 * This class starts the JavaFX application, ensures persisted state is
 * loaded on startup, and saves state when the application exits.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class Photos extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // load persisted users (or create default stock user)
        AppState.get().load();

        primaryStage = stage;
        switchScene("/photos/view/login.fxml", "Photos - Login");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // persist state on application exit
        AppState.get().save();
        super.stop();
    }

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

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}

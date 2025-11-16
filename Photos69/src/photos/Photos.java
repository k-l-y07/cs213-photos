package photos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import photos.model.DataStore;

public class Photos extends Application {
    private static Stage primaryStage;

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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

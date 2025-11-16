package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import photos.Photos;
import photos.model.AppState;
import photos.model.DataStore;
import photos.model.User;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField; // optional; not used for logic

    @FXML
    private void handleLogin() {
        String u = usernameField.getText().trim();
        if (u.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter a username").showAndWait();
            return;
        }

        // Admin special case
        if (u.equalsIgnoreCase("admin")) {
            Photos.switchScene("/photos/view/admin.fxml", "Photos - Admin");
            return;
        }

        // Find user or create if missing (you can change this later
        // if you want only admin to create users)
        User user = AppState.get().users.stream()
                .filter(x -> x.username.equals(u))
                .findFirst()
                .orElse(null);

        if (user == null) {
            user = new User(u);
            AppState.get().users.add(user);
            DataStore.saveUsers();   // persist new user
        }

        AppState.get().currentUser = user;
        Photos.switchScene("/photos/view/user_home.fxml", "Photos - " + u + " (Albums)");
    }
}

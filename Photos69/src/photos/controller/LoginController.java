package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import photos.Photos;
import photos.model.AppState;
import photos.model.User;

/**
 * Controller for the login view. Handles logging in as a user or entering the
 * admin view.
 *
 * Auto-creates a user if the username does not exist in the saved state.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class LoginController {
    @FXML private TextField usernameField;

    @FXML
    private void handleLogin() {
        String u = usernameField.getText().trim();
        if (u.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter a username").showAndWait();
            return;
        }

        if (u.equalsIgnoreCase("admin")) {
            Photos.switchScene("/photos/view/admin.fxml", "Photos - Admin");
            return;
        }

        // auto-create user if doesn't exist
        User user = AppState.get().users.stream().filter(x -> x.username.equals(u)).findFirst().orElse(null);
        if (user == null) {
            user = new User(u);
            AppState.get().users.add(user);
            AppState.get().save();
        }
        AppState.get().currentUser = user;
        Photos.switchScene("/photos/view/user_home.fxml", "Photos - " + u + " (Albums)");
    }
}

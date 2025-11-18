package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import photos.Photos;
import photos.model.AppState;
import photos.model.User;

/**
 * Controller for the login screen.
 * Handles user authentication and navigation to the appropriate screen.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField; // optional; not used for logic

    /**
     * Handles the login operation.
     * Authenticates the user and navigates to the user home or admin screen.
     */
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

        // Find existing user; do NOT auto-create users on login.
        User user = AppState.get().users.stream()
                .filter(x -> x.username.equals(u))
                .findFirst()
                .orElse(null);

        if (user == null) {
            new Alert(Alert.AlertType.ERROR, "User not found. Ask admin to create the account.").showAndWait();
            return;
        }

        AppState.get().currentUser = user;
        Photos.switchScene("/photos/view/user_home.fxml", "Photos - " + u + " (Albums)");
    }
}

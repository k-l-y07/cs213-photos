package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            messageLabel.setText("Please enter a username.");
        } else {
            messageLabel.setText("JavaFX + FXML are working! Logged in as: " + username);
        }
    }
}

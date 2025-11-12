package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import photos.Photos;
import photos.model.AppState;
import photos.model.User;

public class AdminController {
    @FXML private ListView<User> usersList;
    @FXML private Button addBtn, deleteBtn, backBtn;

    private final ObservableList<User> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        data.setAll(AppState.get().users);
        usersList.setItems(data);
    }

    @FXML
    private void handleAdd() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Create User");
        d.setContentText("Username:");
        d.showAndWait().ifPresent(name -> {
            String u = name.trim();
            if (u.isEmpty()) return;
            if (AppState.get().users.stream().anyMatch(x -> x.username.equals(u))) {
                new Alert(Alert.AlertType.ERROR, "User already exists").showAndWait();
                return;
            }
            User user = new User(u);
            AppState.get().users.add(user);
            data.add(user);
        });
    }

    @FXML
    private void handleDelete() {
        User sel = usersList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (confirm("Delete user '" + sel.username + "'?")) {
            AppState.get().users.remove(sel);
            data.remove(sel);
        }
    }

    @FXML
    private void handleBack() { Photos.switchScene("/photos/view/login.fxml", "Photos - Login"); }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}

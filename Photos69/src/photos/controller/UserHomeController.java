package photos.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import photos.Photos;
import photos.model.Album;
import photos.model.AppState;
import photos.model.DataStore;
import photos.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for the user home screen.
 * Manages the display and operations on the user's albums.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class UserHomeController {
    @FXML private TableView<Album> albumsTable;
    @FXML private TableColumn<Album, String> nameCol;
    @FXML private TableColumn<Album, String> countCol;
    @FXML private TableColumn<Album, String> rangeCol;

    @FXML private Button addBtn;
    @FXML private Button renameBtn;
    @FXML private Button deleteBtn;
    @FXML private Button openBtn;
    @FXML private Button searchBtn;
    @FXML private Button logoutBtn;

    private final ObservableList<Album> data = FXCollections.observableArrayList();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Initializes the controller by populating the album table with the user's albums.
     */
    @FXML
    private void initialize() {
        User user = AppState.get().currentUser;
        data.setAll(user.albums);
        albumsTable.setItems(data);

        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        countCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().size())));
        rangeCol.setCellValueFactory(c -> new SimpleStringProperty(range(c.getValue())));
    }

    /**
     * Computes the date range of an album.
     * 
     * @param a the album
     * @return the date range as a string, or "-" if the album is empty
     */
    private String range(Album a) {
        Date lo = a.earliestDate();
        Date hi = a.latestDate();
        if (lo == null || hi == null) return "-";
        return sdf.format(lo) + "  to  " + sdf.format(hi);
    }

    /**
     * Handles the addition of a new album.
     * Prompts the user for the album name and adds it to the user's albums.
     */
    @FXML
    private void handleAdd() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("New Album");
        d.setContentText("Album name:");
        d.showAndWait().ifPresent(n -> {
            String name = n.trim();
            if (name.isEmpty()) return;

            boolean exists = AppState.get().currentUser.albums.stream()
                    .anyMatch(a -> a.name.equalsIgnoreCase(name));
            if (exists) {
                new Alert(Alert.AlertType.ERROR, "Album already exists").showAndWait();
                return;
            }

            Album a = new Album(name);
            AppState.get().currentUser.albums.add(a);
            data.add(a);
            DataStore.saveUsers();   // persist albums
        });
    }

    /**
     * Handles renaming an existing album.
     * Prompts the user for a new name and updates the album.
     */
    @FXML
    private void handleRename() {
        Album sel = albumsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        TextInputDialog d = new TextInputDialog(sel.name);
        d.setHeaderText("Rename Album");
        d.setContentText("New name:");
        d.showAndWait().ifPresent(n -> {
            String name = n.trim();
            if (name.isEmpty()) return;

            boolean exists = AppState.get().currentUser.albums.stream()
                    .anyMatch(a -> a != sel && a.name.equalsIgnoreCase(name));
            if (exists) {
                new Alert(Alert.AlertType.ERROR, "Album already exists").showAndWait();
                return;
            }

            sel.name = name;
            albumsTable.refresh();
            DataStore.saveUsers();   // persist rename
        });
    }

    /**
     * Handles deleting an album.
     * Prompts the user for confirmation before removing the album.
     */
    @FXML
    private void handleDelete() {
        Album sel = albumsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        if (confirm("Delete album '" + sel.name + "'?")) {
            AppState.get().currentUser.albums.remove(sel);
            data.remove(sel);
            DataStore.saveUsers();   // persist delete
        }
    }

    /**
     * Handles opening an album.
     * Switches to the album view for the selected album.
     */
    @FXML
    private void handleOpen() {
        Album sel = albumsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        AlbumController.setCurrentAlbum(sel);
        Photos.switchScene("/photos/view/album.fxml", "Album - " + sel.name);
    }

    /**
     * Handles the search operation.
     * Switches to the search view.
     */
    @FXML
    private void handleSearch() {
        Photos.switchScene("/photos/view/search.fxml", "Search Photos");
    }

    /**
     * Handles logging out.
     * Clears the current user session and switches to the login view.
     */
    @FXML
    private void handleLogout() {
        AppState.get().currentUser = null;
        Photos.switchScene("/photos/view/login.fxml", "Photos - Login");
    }

    /**
     * Displays a confirmation dialog with the specified message.
     * 
     * @param msg the message to display
     * @return true if the user confirms, false otherwise
     */
    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg,
                ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}

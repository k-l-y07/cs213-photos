package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import photos.Photos;
import photos.model.*;

import java.io.File;
import java.util.Optional;

/**
 * Controller for the album screen.
 * Manages the display and operations on photos within an album.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class AlbumController {
    @FXML private Label albumNameLabel;
    @FXML private ListView<Photo> photosList;
    @FXML private Button addBtn;
    @FXML private Button removeBtn;
    @FXML private Button captionBtn;
    @FXML private Button viewBtn;
    @FXML private Button copyBtn;
    @FXML private Button moveBtn;
    @FXML private Button tagBtn;
    @FXML private Button backBtn;

    private static Album currentAlbum;

    /**
     * Sets the current album to be displayed and managed.
     * 
     * @param a the album to set as the current album
     */
    public static void setCurrentAlbum(Album a) {
        currentAlbum = a;
    }

    /**
     * Initializes the controller by populating the photo list with the current album's photos.
     */
    @FXML
    private void initialize() {
        if (currentAlbum == null) {
            Photos.switchScene("/photos/view/user_home.fxml", "Photos - Albums");
            return;
        }
        albumNameLabel.setText(currentAlbum.name);
        photosList.getItems().setAll(currentAlbum.photos);
        photosList.setCellFactory(list -> new PhotoCell());
    }

    /**
     * Handles adding a new photo to the album.
     * Prompts the user to select a photo file and adds it to the album.
     */
    @FXML
    private void handleAdd() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg","*.gif","*.bmp"));
        File f = fc.showOpenDialog(Photos.getPrimaryStage());
        if (f == null) return;

        String path = f.getAbsolutePath();
        boolean exists = currentAlbum.photos.stream()
                .anyMatch(p -> p.path.equals(path));
        if (exists) {
            new Alert(Alert.AlertType.ERROR, "This photo is already in this album.").showAndWait();
            return;
        }

        Photo p = new Photo(path);
        currentAlbum.photos.add(p);
        photosList.getItems().add(p);
        DataStore.saveUsers();   // persist new photo
    }

    /**
     * Handles removing a photo from the album.
     * Prompts the user for confirmation before removing the photo.
     */
    @FXML
    private void handleRemove() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        if (confirm("Remove this photo from album?")) {
            currentAlbum.photos.remove(sel);
            photosList.getItems().remove(sel);
            DataStore.saveUsers();   // persist removal
        }
    }

    /**
     * Handles setting or updating the caption of a photo.
     * Prompts the user for a new caption and updates the photo.
     */
    @FXML
    private void handleCaption() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        TextInputDialog d = new TextInputDialog(sel.caption);
        d.setHeaderText("Set Caption");
        d.setContentText("Caption:");
        d.showAndWait().ifPresent(c -> {
            sel.caption = c.trim();
            photosList.refresh();
            DataStore.saveUsers();   // persist caption change
        });
    }

    /**
     * Handles viewing detailed information about a photo.
     * Displays the photo's path, date, and tags in an alert dialog.
     */
    @FXML
    private void handleView() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        int idx = photosList.getSelectionModel().getSelectedIndex();
        if (sel == null || idx < 0) return;

        // open dedicated photo viewer with slideshow controls
        PhotoViewerController.setStartIndex(idx);
        Photos.switchScene("/photos/view/viewer.fxml", "Photo Viewer - " + currentAlbum.name);
    }

    /**
     * Gets the current album being shown in this controller.
     * @return the current Album
     */
    public static Album getCurrentAlbum() {
        return currentAlbum;
    }

    /**
     * Handles copying a photo to another album.
     */
    @FXML
    private void handleCopy() {
        moveOrCopy(false);
    }

    /**
     * Handles moving a photo to another album.
     */
    @FXML
    private void handleMove() {
        moveOrCopy(true);
    }

    /**
     * Handles moving or copying a photo to another album.
     * 
     * @param move true if the photo should be moved, false if it should be copied
     */
    private void moveOrCopy(boolean move) {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        ChoiceDialog<Album> dlg = new ChoiceDialog<>();
        dlg.setHeaderText((move ? "Move" : "Copy") + " to album:");
        AppState.get().currentUser.albums.forEach(a -> {
            if (a != currentAlbum) dlg.getItems().add(a);
        });

        Optional<Album> res = dlg.showAndWait();
        if (res.isEmpty()) return;

        Album dest = res.get();
        boolean dup = dest.photos.stream().anyMatch(p -> p.path.equals(sel.path));
        if (dup) {
            new Alert(Alert.AlertType.ERROR, "Destination already contains this photo.").showAndWait();
            return;
        }

        dest.photos.add(sel);
        if (move) {
            currentAlbum.photos.remove(sel);
            photosList.getItems().remove(sel);
        }
        DataStore.saveUsers();   // persist copy/move
    }

    @FXML
    private void handleTags() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        ChoiceDialog<String> choice = new ChoiceDialog<>("Add Tag", "Add Tag", "Delete Tag");
        choice.setHeaderText("Tag Operation");
        String op = choice.showAndWait().orElse(null);
        if (op == null) return;

        if (op.equals("Add Tag")) {
            TextInputDialog t1 = new TextInputDialog();
            t1.setHeaderText("Add Tag");
            t1.setContentText("name=value (e.g., person=alice):");
            t1.showAndWait().ifPresent(s -> {
                String[] parts = s.split("=", 2);
                if (parts.length == 2) {
                    Tag tag = new Tag(parts[0].trim(), parts[1].trim());
                    boolean exists = sel.tags.stream()
                            .anyMatch(x -> x.name.equalsIgnoreCase(tag.name)
                                    && x.value.equalsIgnoreCase(tag.value));
                    if (!exists) {
                        sel.tags.add(tag);
                        DataStore.saveUsers();   // persist new tag
                    }
                }
            });
        } else {
            if (sel.tags.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "This photo has no tags.").showAndWait();
                return;
            }

            ChoiceDialog<Tag> del = new ChoiceDialog<>();
            del.setHeaderText("Delete Tag");
            del.getItems().addAll(sel.tags);
            del.showAndWait().ifPresent(t -> {
                sel.tags.remove(t);
                DataStore.saveUsers();   // persist tag removal
            });
        }
    }

    /**
     * Handles navigating back to the user home screen.
     */
    @FXML
    private void handleBack() {
        Photos.switchScene("/photos/view/user_home.fxml", "Photos - Albums");
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

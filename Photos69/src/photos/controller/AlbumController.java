package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import photos.Photos;
import photos.model.*;

import java.io.File;
import java.util.Optional;

public class AlbumController {
    @FXML private Label albumNameLabel;
    @FXML private ListView<Photo> photosList;
    @FXML private Button addBtn, removeBtn, captionBtn, viewBtn, copyBtn, moveBtn, tagBtn, backBtn;

    private static Album currentAlbum;

    public static void setCurrentAlbum(Album a) { currentAlbum = a; }

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

    @FXML
    private void handleAdd() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg","*.gif","*.bmp"));
        File f = fc.showOpenDialog(Photos.getPrimaryStage());
        if (f == null) return;

        // prevent duplicate of same picture in SAME album
        String path = f.getAbsolutePath();
        boolean exists = currentAlbum.photos.stream().anyMatch(p -> p.path.equals(path));
        if (exists) {
            new Alert(Alert.AlertType.ERROR, "This photo is already in this album.").showAndWait();
            return;
        }
        Photo p = new Photo(path);
        currentAlbum.photos.add(p);
        photosList.getItems().add(p);
    }

    @FXML
    private void handleRemove() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (confirm("Remove this photo from album?")) {
            currentAlbum.photos.remove(sel);
            photosList.getItems().remove(sel);
        }
    }

    @FXML
    private void handleCaption() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        TextInputDialog d = new TextInputDialog(sel.caption);
        d.setHeaderText("Set Caption");
        d.setContentText("Caption:");
        d.showAndWait().ifPresent(c -> { sel.caption = c.trim(); photosList.refresh(); });
    }

    @FXML
    private void handleView() {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Photo Info");
        info.setHeaderText(sel.toString());
        info.setContentText("Path: " + sel.path + "\nDate: " + sel.date + "\nTags: " + sel.tags);
        info.showAndWait();
    }

    @FXML
    private void handleCopy() { moveOrCopy(false); }

    @FXML
    private void handleMove() { moveOrCopy(true); }

    private void moveOrCopy(boolean move) {
        Photo sel = photosList.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        ChoiceDialog<Album> dlg = new ChoiceDialog<>();
        dlg.setHeaderText((move ? "Move" : "Copy") + " to album:");
        AppState.get().currentUser.albums.forEach(a -> { if (a != currentAlbum) dlg.getItems().add(a); });
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
                    // prevent exact duplicate tag on a photo
                    boolean exists = sel.tags.stream().anyMatch(x -> x.name.equalsIgnoreCase(tag.name) && x.value.equalsIgnoreCase(tag.value));
                    if (!exists) sel.tags.add(tag);
                }
            });
        } else {
            ChoiceDialog<Tag> del = new ChoiceDialog<>();
            del.setHeaderText("Delete Tag");
            del.getItems().addAll(sel.tags);
            del.showAndWait().ifPresent(t -> sel.tags.remove(t));
        }
    }

    @FXML
    private void handleBack() { Photos.switchScene("/photos/view/user_home.fxml", "Photos - Albums"); }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}

package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import photos.Photos;
import photos.model.*;
import photos.model.DataStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Controller for the photo viewer scene.
 *
 * <p>Displays a single photo from an album along with its caption, date/time, and tags.
 * Provides manual slideshow controls (Next/Previous) and allows editing captions and tags.
 * Changes are persisted immediately using {@code DataStore}.</p>
 *
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class PhotoViewerController {
    private static int startIndex = 0;

    public static void setStartIndex(int i) { startIndex = i; }

    @FXML private ImageView imageView;
    @FXML private javafx.scene.control.ScrollPane scrollPane;
    @FXML private Label captionLabel;
    @FXML private Label dateLabel;
    @FXML private Label tagsLabel;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Button editCaptionBtn;
    @FXML private Button tagsBtn;
    @FXML private Button closeBtn;

    private Album album;
    private List<Photo> photos;
    private int index;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        album = AlbumController.getCurrentAlbum();
        if (album == null) {
            Photos.switchScene("/photos/view/user_home.fxml", "Photos - Albums");
            return;
        }
        photos = album.photos;
        index = Math.max(0, Math.min(startIndex, photos.size()-1));
        // ensure image scales to viewport while preserving ratio
        imageView.setPreserveRatio(true);
        if (scrollPane != null) {
            scrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
                if (newB != null) {
                    imageView.setFitWidth(newB.getWidth());
                    imageView.setFitHeight(newB.getHeight());
                }
            });
        }

        showPhoto();
    }

    private void showPhoto() {
        if (photos.isEmpty()) return;
        Photo p = photos.get(index);
        try {
            Image img = new Image(new File(p.path).toURI().toString());
            imageView.setImage(img);
        } catch (Exception e) {
            imageView.setImage(null);
        }
        captionLabel.setText(p.caption == null || p.caption.isEmpty() ? new File(p.path).getName() : p.caption);
        dateLabel.setText(sdf.format(p.date));
        tagsLabel.setText(p.tags.isEmpty() ? "No tags" : p.tags.toString());

        prevBtn.setDisable(index <= 0);
        nextBtn.setDisable(index >= photos.size() - 1);
    }

    @FXML
    private void handlePrev() {
        if (index > 0) {
            index--;
            showPhoto();
        }
    }

    @FXML
    private void handleNext() {
        if (index < photos.size() - 1) {
            index++;
            showPhoto();
        }
    }

    @FXML
    private void handleEditCaption() {
        Photo p = photos.get(index);
        TextInputDialog d = new TextInputDialog(p.caption);
        d.setHeaderText("Edit Caption");
        d.setContentText("Caption:");
        d.showAndWait().ifPresent(s -> {
            p.caption = s.trim();
            refresh();
            DataStore.saveUsers();
        });
    }

    // helper to refresh displayed fields
    private void refresh() { showPhoto(); }

    @FXML
    private void handleTags() {
        Photo p = photos.get(index);
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
                    boolean exists = p.tags.stream()
                            .anyMatch(x -> x.name.equalsIgnoreCase(tag.name)
                                    && x.value.equalsIgnoreCase(tag.value));
                    if (!exists) {
                        p.tags.add(tag);
                        DataStore.saveUsers();
                        refresh();
                    }
                }
            });
        } else {
            if (p.tags.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "This photo has no tags.").showAndWait();
                return;
            }
            ChoiceDialog<Tag> del = new ChoiceDialog<>();
            del.setHeaderText("Delete Tag");
            del.getItems().addAll(p.tags);
            del.showAndWait().ifPresent(t -> {
                p.tags.remove(t);
                DataStore.saveUsers();
                refresh();
            });
        }
    }

    @FXML
    private void handleClose() {
        Photos.switchScene("/photos/view/album.fxml", "Album - " + album.name);
    }
}

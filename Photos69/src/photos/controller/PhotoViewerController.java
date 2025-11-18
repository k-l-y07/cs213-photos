package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import photos.Photos;
import photos.model.*;

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
    @FXML private ScrollPane scrollPane;
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
        if (photos.isEmpty()) {
            // nothing to show, just go back
            Photos.switchScene("/photos/view/album.fxml", "Album - " + album.name);
            return;
        }

        index = Math.max(0, Math.min(startIndex, photos.size() - 1));

        // Make sure the image scales nicely
        imageView.setPreserveRatio(true);

        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            // Bind ImageView size to viewport so it never explodes past the window
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
        if (photos == null || photos.isEmpty()) {
            imageView.setImage(null);
            captionLabel.setText("");
            dateLabel.setText("");
            tagsLabel.setText("");
            prevBtn.setDisable(true);
            nextBtn.setDisable(true);
            return;
        }

        Photo p = photos.get(index);
        try {
            // Limit the loaded image size so it doesn't blow up the stage
            Image img = new Image(
                    new File(p.path).toURI().toString(),
                    1600, 900,      // max width / height for display
                    true, true      // preserveRatio, smooth
            );
            imageView.setImage(img);
        } catch (Exception e) {
            imageView.setImage(null);
        }

        captionLabel.setText(
                (p.caption == null || p.caption.isEmpty())
                        ? new File(p.path).getName()
                        : p.caption
        );

        if (p.date != null) {
            dateLabel.setText(sdf.format(p.date));
        } else {
            dateLabel.setText("Unknown date");
        }

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
            showPhoto();
            DataStore.saveUsers();
        });
    }

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
                        showPhoto();
                    }
                }
            });
        } else { // Delete Tag
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
                showPhoto();
            });
        }
    }

    @FXML
    private void handleClose() {
        Photos.switchScene("/photos/view/album.fxml", "Album - " + album.name);
    }
}

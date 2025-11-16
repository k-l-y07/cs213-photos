package photos.controller;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import photos.model.Photo;

/**
 * A custom {@code ListCell} used to render {@code Photo} items in list views.
 *
 * <p>Shows a thumbnail image and a caption or filename next to it.</p>
 *
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class PhotoCell extends ListCell<Photo> {
    private final HBox root = new HBox(10);
    private final ImageView iv = new ImageView();
    private final Label lbl = new Label();

    public PhotoCell() {
        iv.setFitWidth(140);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);
        root.setPadding(new Insets(6));
        root.getChildren().addAll(iv, lbl);
    }

    @Override
    protected void updateItem(Photo item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            iv.setImage(item.loadThumb());
            lbl.setText(item.toString());
            setGraphic(root);
        }
    }
}

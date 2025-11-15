package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a photo referenced by the application.
 *
 * A {@code Photo} stores the path to the image file on disk (photos imported by
 * users must remain outside of the application's workspace; only the path is
 * stored), a caption, the last-modified date (used as the photo's date), and
 * a collection of tags.
 *
 * Instances are serializable so they can be persisted as part of application
 * state.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Absolute file path to the image file. */
    public final String path;

    /** User-provided caption for the photo (may be empty). */
    public String caption = "";

    /** Date used as the photo's capture date (file last-modified). */
    public final Date date;

    /** Tags associated with the photo. */
    public final List<Tag> tags = new ArrayList<>();

    /**
     * Construct a photo object for the given file path.
     *
     * @param path absolute path to the image file
     */
    public Photo(String path) {
        this.path = path;
        File f = new File(path);
        this.date = new Date(f.lastModified());
    }

    /**
     * Loads a thumbnail image for display in the UI.
     * Returns null if the image cannot be loaded.
     */
    public Image loadThumb() {
        try { return new Image(new File(path).toURI().toString(), 140, 140, true, true); }
        catch (Exception e) { return null; }
    }

    @Override public String toString() { return caption == null || caption.isEmpty() ? new File(path).getName() : caption; }
}

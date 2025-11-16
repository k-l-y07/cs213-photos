package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a photo in the Photos application.
 * Each photo has a file path, caption, date, and associated tags.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String path;     // absolute file path
    public String caption = "";
    public final Date date;       // file last-modified as proxy
    public final List<Tag> tags = new ArrayList<>();

    /**
     * Constructs a new Photo with the specified file path.
     * The date is set to the file's last modified date.
     * 
     * @param path the absolute file path of the photo
     */
    public Photo(String path) {
        this.path = path;
        File f = new File(path);
        this.date = new Date(f.lastModified());
    }

    /**
     * Loads a thumbnail image for the photo.
     * 
     * @return the thumbnail image, or null if loading fails
     */
    public Image loadThumb() {
        try {
            return new Image(new File(path).toURI().toString(),
                    140, 140, true, true);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a string representation of the photo.
     * If a caption is set, it is returned; otherwise, the file name is returned.
     * 
     * @return the string representation of the photo
     */
    @Override
    public String toString() {
        if (caption == null || caption.isEmpty()) {
            return new File(path).getName();
        }
        return caption;
    }
}

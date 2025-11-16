package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String path;     // absolute file path
    public String caption = "";
    public final Date date;       // file last-modified as proxy
    public final List<Tag> tags = new ArrayList<>();

    public Photo(String path) {
        this.path = path;
        File f = new File(path);
        this.date = new Date(f.lastModified());
    }

    // Not serialized; computed on demand
    public Image loadThumb() {
        try {
            return new Image(new File(path).toURI().toString(),
                    140, 140, true, true);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        if (caption == null || caption.isEmpty()) {
            return new File(path).getName();
        }
        return caption;
    }
}

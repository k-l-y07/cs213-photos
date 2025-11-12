package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Photo {
    public final String path;       // absolute file path
    public String caption = "";
    public final Date date;         // file last-modified as proxy
    public final List<Tag> tags = new ArrayList<>();

    public Photo(String path) {
        this.path = path;
        File f = new File(path);
        this.date = new Date(f.lastModified());
    }

    public Image loadThumb() {
        try { return new Image(new File(path).toURI().toString(), 140, 140, true, true); }
        catch (Exception e) { return null; }
    }

    @Override public String toString() { return caption == null || caption.isEmpty() ? new File(path).getName() : caption; }
}

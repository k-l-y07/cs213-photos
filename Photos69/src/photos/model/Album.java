package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a photo album containing zero or more {@link Photo} objects.
 *
 * Albums are serializable so they may be persisted as part of the user's
 * application data.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Album name. Album names must be unique per user. */
    public String name;

    /** Photos contained in this album. */
    public final List<Photo> photos = new ArrayList<>();

    public Album(String name) { this.name = name; }

    public int size() { return photos.size(); }

    public Date earliestDate() {
        return photos.stream().map(p -> p.date).min(Date::compareTo).orElse(null);
    }
    public Date latestDate() {
        return photos.stream().map(p -> p.date).max(Date::compareTo).orElse(null);
    }

    @Override public String toString() { return name; }
}

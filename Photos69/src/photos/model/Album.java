package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an album in the Photos application.
 * Each album has a name and a list of photos.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public final List<Photo> photos = new ArrayList<>();

    /**
     * Constructs a new Album with the specified name.
     * 
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
    }

    /**
     * Returns the number of photos in the album.
     * 
     * @return the size of the album
     */
    public int size() {
        return photos.size();
    }

    /**
     * Returns the earliest date of the photos in the album.
     * 
     * @return the earliest date, or null if the album is empty
     */
    public Date earliestDate() {
        return photos.stream()
                .map(p -> p.date)
                .min(Date::compareTo)
                .orElse(null);
    }

    /**
     * Returns the latest date of the photos in the album.
     * 
     * @return the latest date, or null if the album is empty
     */
    public Date latestDate() {
        return photos.stream()
                .map(p -> p.date)
                .max(Date::compareTo)
                .orElse(null);
    }

    /**
     * Returns the string representation of the album, which is its name.
     * 
     * @return the name of the album
     */
    @Override
    public String toString() {
        return name;
    }
}

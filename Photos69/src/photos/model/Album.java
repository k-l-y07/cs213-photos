package photos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album {
    public String name;
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

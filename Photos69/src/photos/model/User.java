package photos.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public final String username;
    public final List<Album> albums = new ArrayList<>();

    public User(String username) { this.username = username; }

    @Override public String toString() { return username; }
}

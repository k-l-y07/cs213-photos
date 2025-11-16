package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String username;
    public final List<Album> albums = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }
}

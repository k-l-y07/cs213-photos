package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the Photos application.
 * Each user has a username and a list of albums.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String username;
    public final List<Album> albums = new ArrayList<>();

    /**
     * Constructs a new User with the specified username.
     * 
     * @param username the username of the user
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username
     */
    @Override
    public String toString() {
        return username;
    }
}

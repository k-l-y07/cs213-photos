package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the photo application.
 *
 * Each {@code User} has a username and a collection of albums. Users are
 * serialized to persist application state between runs.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The user's unique username. */
    public final String username;

    /** The list of albums owned by this user. May be empty. */
    public final List<Album> albums = new ArrayList<>();

    /**
     * Constructs a new user with the given username.
     *
     * @param username the username for this user
     */
    public User(String username) { this.username = username; }

    @Override public String toString() { return username; }
}

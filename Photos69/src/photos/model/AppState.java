package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that represents the application state.
 * Manages the list of users and the current user session.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
public class AppState implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AppState INSTANCE = new AppState();

    /**
     * Gets the singleton instance of the application state.
     * 
     * @return the singleton instance of AppState
     */
    public static AppState get() { return INSTANCE; }

    public List<User> users = new ArrayList<>();

    // currentUser is "session" state; don't serialize it
    public transient User currentUser = null;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private AppState() {}

    /**
     * Sets the list of users in the application state.
     * 
     * @param users the list of users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
}

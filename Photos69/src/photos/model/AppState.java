package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppState implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AppState INSTANCE = new AppState();
    public static AppState get() { return INSTANCE; }

    public List<User> users = new ArrayList<>();

    // currentUser is "session" state; don't serialize it
    public transient User currentUser = null;

    private AppState() {}

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

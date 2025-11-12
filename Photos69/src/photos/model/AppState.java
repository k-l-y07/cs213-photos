package photos.model;

import java.util.ArrayList;
import java.util.List;

public class AppState {
    private static final AppState INSTANCE = new AppState();
    public static AppState get() { return INSTANCE; }

    public final List<User> users = new ArrayList<>();
    public User currentUser = null;

    private AppState() {
        // seed some demo users/albums/photos for GUI testing
        User u1 = new User("alice");
        User u2 = new User("bob");
        users.add(u1);
        users.add(u2);

        Album a1 = new Album("Summer 2024");
        Album a2 = new Album("School");
        u1.albums.add(a1);
        u1.albums.add(a2);

        // sample photos: change paths to real images on your disk if you want to see thumbs
        // a1.photos.add(new Photo("/home/you/Pictures/pic1.jpg"));
        // a1.photos.add(new Photo("/home/you/Pictures/pic2.png"));
    }
}

package photos.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that holds the application state (list of users and the currently
 * logged-in user) and provides simple persistence helpers to save/load the
 * state from disk using Java serialization.
 *
 * Persistence uses {@code data/users.dat} relative to the project root. The
 * loader also ensures a "stock" user and album exist and will load photos
 * present under {@code data/stock} into the stock album.
 *
 * @author Kenneth Yan
 * @author Wilmer Joya
 * @version 1.0
 */
public class AppState {
    private static final AppState INSTANCE = new AppState();
    public static AppState get() { return INSTANCE; }

    private static final Path DATA_FILE = Paths.get("data", "users.dat");

    public final List<User> users = new ArrayList<>();
    public User currentUser = null;

    private AppState() {
        // empty; load() should be called by launcher on startup
    }

    /**
     * Save users to disk (data/users.dat). Creates data/ if missing.
     */
    public void save() {
        try {
            if (DATA_FILE.getParent() != null) Files.createDirectories(DATA_FILE.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE.toFile()))) {
                oos.writeObject(users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load users list from disk. If no file exists, seeds a default "stock" user with an empty "stock" album.
     */
    @SuppressWarnings("unchecked")
    public void load() {
        if (Files.exists(DATA_FILE)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE.toFile()))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    users.clear();
                    users.addAll((List<User>) obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ensure stock user exists as per assignment
        boolean hasStock = users.stream().anyMatch(u -> u.username.equals("stock"));
        if (!hasStock) {
            User stock = new User("stock");
            stock.albums.add(new Album("stock"));
            users.add(stock);
        }

        // If there's a data/stock directory in the workspace, load those files into the stock album (if not already present)
        try {
            Path stockDir = Paths.get("data", "stock");
            if (Files.exists(stockDir) && Files.isDirectory(stockDir)) {
                User stockUser = users.stream().filter(u -> u.username.equals("stock")).findFirst().orElse(null);
                if (stockUser != null) {
                    Album stockAlbum = stockUser.albums.stream().filter(a -> a.name.equals("stock")).findFirst().orElse(null);
                    if (stockAlbum != null) {
                        Files.list(stockDir).filter(p -> {
                            String n = p.getFileName().toString().toLowerCase();
                            return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".gif") || n.endsWith(".bmp");
                        }).forEach(p -> {
                            String abs = p.toAbsolutePath().toString();
                            boolean present = stockAlbum.photos.stream().anyMatch(ph -> ph.path.equals(abs));
                            if (!present) {
                                stockAlbum.photos.add(new Photo(abs));
                            }
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

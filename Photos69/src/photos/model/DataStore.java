package photos.model;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = "users.dat";
    private static final String STOCK_DIR = "stock";

    private static Path dataDir() {
        return Paths.get(DATA_DIR);
    }

    private static Path usersFile() {
        return dataDir().resolve(USERS_FILE);
    }

    private static Path stockDir() {
        return dataDir().resolve(STOCK_DIR);
    }

    /**
     * Call this once at app startup.
     * Loads users from disk if present, otherwise initializes defaults (admin + stock).
     */
    public static void loadOrInit() {
        try {
            Files.createDirectories(dataDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Files.exists(usersFile())) {
            loadUsers();
        } else {
            createDefaultUsers();
            saveUsers();
        }
    }

    public static void saveUsers() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(usersFile()))) {

            oos.writeObject(new ArrayList<>(AppState.get().users));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(usersFile()))) {

            List<User> loaded = (List<User>) ois.readObject();
            AppState.get().setUsers(loaded);

        } catch (Exception e) {
            e.printStackTrace();
            // If something is badly corrupted, fall back to defaults.
            createDefaultUsers();
            saveUsers();
        }
    }

    private static void createDefaultUsers() {
        AppState state = AppState.get();
        state.users.clear();

        // admin user: only manages users, no albums
        User admin = new User("admin");
        state.users.add(admin);

        // stock user with "stock" album and photos from data/stock
        User stockUser = new User("stock");
        Album stockAlbum = new Album("stock");
        stockUser.albums.add(stockAlbum);
        state.users.add(stockUser);

        // load all BMP, GIF, JPEG, PNG from data/stock dir
        try {
            Files.createDirectories(stockDir());
            DirectoryStream.Filter<Path> filter = entry -> {
                String name = entry.getFileName().toString().toLowerCase();
                return Files.isRegularFile(entry) &&
                        (name.endsWith(".png") ||
                         name.endsWith(".jpg") ||
                         name.endsWith(".jpeg") ||
                         name.endsWith(".gif") ||
                         name.endsWith(".bmp"));
            };

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(stockDir(), filter)) {
                for (Path p : stream) {
                    // store absolute path; physical file remains in data/stock in the project
                    stockAlbum.photos.add(new Photo(p.toAbsolutePath().toString()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package photos.model;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles data persistence for the Photos application.
 * Manages saving and loading user data, as well as initializing default data.
 * 
 * <p>Authors: Wilmer Joya, Kenneth Yan</p>
 */
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
     * Initializes the data store by loading users from disk if present,
     * or creating default users and albums if no data exists.
     */
    public static void loadOrInit() {
        try {
            Files.createDirectories(dataDir());
        } catch (IOException e) {
            final String msg = "Failed to create data directory: " + e.getMessage();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
        }

        if (Files.exists(usersFile())) {
            loadUsers();
        } else {
            createDefaultUsers();
            saveUsers();
        }
    }

    /**
     * Saves the current list of users to disk.
     */
    public static void saveUsers() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(usersFile()))) {

            oos.writeObject(new ArrayList<>(AppState.get().users));

        } catch (IOException e) {
            final String msg = "Failed to save user data: " + e.getMessage();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
        }
    }

    /**
     * Loads the list of users from disk.
     * If the data is corrupted, default users are created.
     */
    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(usersFile()))) {

            List<User> loaded = (List<User>) ois.readObject();
            AppState.get().setUsers(loaded);

        } catch (Exception e) {
            final String msg = "Failed to load user data (using defaults): " + e.getMessage();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
            // If something is badly corrupted, fall back to defaults.
            createDefaultUsers();
            saveUsers();
        }
    }

    /**
     * Creates default users and albums for the application.
     * Includes an admin user and a stock user with sample photos.
     */
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
            final String msg = "Error initializing stock photos: " + e.getMessage();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
        }
    }
}

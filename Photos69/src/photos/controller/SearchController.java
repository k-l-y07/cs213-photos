package photos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import photos.Photos;
import photos.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SearchController {
    @FXML private ToggleGroup modeGroup;
    @FXML private RadioButton byDateRadio;
    @FXML private RadioButton byTagRadio;

    // by date
    @FXML private TextField fromField;
    @FXML private TextField toField;

    // by tags
    @FXML private TextField tag1Name;
    @FXML private TextField tag1Value;
    @FXML private TextField tag2Name;
    @FXML private TextField tag2Value;
    @FXML private ChoiceBox<String> opChoice;

    @FXML private ListView<Photo> resultsList;
    @FXML private Button runBtn;
    @FXML private Button createAlbumBtn;
    @FXML private Button backBtn;

    private final ObservableList<Photo> results = FXCollections.observableArrayList();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    private void initialize() {
        opChoice.getItems().addAll("AND", "OR");
        opChoice.setValue("AND");
        resultsList.setItems(results);
        resultsList.setCellFactory(l -> new PhotoCell());
    }

    @FXML
    private void handleSearch() {
        results.clear();
        List<Photo> allPhotos = allUserPhotos();

        if (byDateRadio.isSelected()) {
            Date lo = parseDate(fromField.getText());
            Date hi = parseDate(toField.getText());
            if (lo == null || hi == null) {
                alert("Enter dates as yyyy-MM-dd.");
                return;
            }
            results.addAll(allPhotos.stream()
                    .filter(p -> !p.date.before(lo) && !p.date.after(hi))
                    .collect(Collectors.toList()));
        } else {
            String n1 = tag1Name.getText().trim();
            String v1 = tag1Value.getText().trim();
            String n2 = tag2Name.getText().trim();
            String v2 = tag2Value.getText().trim();
            boolean has1 = !n1.isEmpty() && !v1.isEmpty();
            boolean has2 = !n2.isEmpty() && !v2.isEmpty();

            if (!has1 && !has2) {
                alert("Enter at least one tag name=value.");
                return;
            }

            if (has1 && !has2) {
                results.addAll(allPhotos.stream()
                        .filter(p -> hasTag(p, n1, v1))
                        .collect(Collectors.toList()));
            } else if (!has1) {
                results.addAll(allPhotos.stream()
                        .filter(p -> hasTag(p, n2, v2))
                        .collect(Collectors.toList()));
            } else {
                String op = opChoice.getValue();
                if ("AND".equals(op)) {
                    results.addAll(allPhotos.stream()
                            .filter(p -> hasTag(p, n1, v1) && hasTag(p, n2, v2))
                            .collect(Collectors.toList()));
                } else {
                    results.addAll(allPhotos.stream()
                            .filter(p -> hasTag(p, n1, v1) || hasTag(p, n2, v2))
                            .collect(Collectors.toList()));
                }
            }
        }
    }

    @FXML
    private void handleCreateAlbum() {
        if (results.isEmpty()) {
            alert("No results to save.");
            return;
        }

        TextInputDialog d = new TextInputDialog("Search Results");
        d.setHeaderText("Create Album from Results");
        d.setContentText("Album name:");
        d.showAndWait().ifPresent(name -> {
            String n = name.trim();
            if (n.isEmpty()) return;

            User u = AppState.get().currentUser;
            boolean exists = u.albums.stream()
                    .anyMatch(a -> a.name.equalsIgnoreCase(n));
            if (exists) {
                alert("Album already exists.");
                return;
            }

            Album a = new Album(n);
            a.photos.addAll(results);   // same physical photos
            u.albums.add(a);
            DataStore.saveUsers();      // persist new album
            alert("Created album '" + n + "' with " + results.size() + " photos.");
        });
    }

    @FXML
    private void handleBack() {
        Photos.switchScene("/photos/view/user_home.fxml", "Photos - Albums");
    }

    private boolean hasTag(Photo p, String name, String value) {
        return p.tags.stream()
                .anyMatch(t -> t.name.equalsIgnoreCase(name)
                        && t.value.equalsIgnoreCase(value));
    }

    private Date parseDate(String s) {
        try {
            return sdf.parse(s.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    private List<Photo> allUserPhotos() {
        User u = AppState.get().currentUser;
        Set<Photo> set = new LinkedHashSet<>();
        u.albums.forEach(a -> set.addAll(a.photos));
        return new ArrayList<>(set);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}

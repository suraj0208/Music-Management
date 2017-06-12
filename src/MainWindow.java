import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;


/**
 * Created by suraj on 7/6/17.
 */
public class MainWindow extends Application implements Initializable, MovieEditedCallBack, SongEditedCallBack {
    @FXML
    private Button btnSearch;

    @FXML
    private ComboBox<String> comboBoxSearch;

    @FXML
    private ComboBox<String> searchFieldComboBox;

    @FXML
    private MenuBar menuBar;

    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private ArrayList<String> songNames;
    private HashSet<String> movieNames;
    private ArrayList<String> artistNames;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Music Management");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    public void onSearch() throws Exception {
        if (comboBoxSearch.getSelectionModel().getSelectedItem() == null || comboBoxSearch.getSelectionModel().getSelectedItem().length() == 0) {
            Utils.showError("Invalid Input");
            return;
        }

        String searchName = comboBoxSearch.getSelectionModel().getSelectedItem();

        if (searchName == null || searchName.length() == 0)
            return;

        ArrayList<Song> songs;

        Parent root;
        Stage stage;

        if (searchFieldComboBox.getValue().equals("Song")) {
            songs = databaseHelper.findSong(searchName);

            if (songs == null) {
                Utils.showError("Unable to get songs from database");
                return;
            }

            if (songs.size() == 0) {
                Utils.showInfo("No such song");
                return;
            }

            if (songs.get(0) == null) {
                Utils.showError("Unexpected error occurred");
                return;
            }

            AddSongWIndow.addSongEditedCallBack(this);
            (btnSearch.getScene()).getWindow().setOnCloseRequest(event -> AddSongWIndow.removeSongEditedCallBack(this));

            SongsSearchResultWindow.setSongs(songs);
            SongsSearchResultWindow.setSearchName(searchName);
            root = FXMLLoader.load(getClass().getResource("SongsSearchResultWindow.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.show();

        } else if (searchFieldComboBox.getValue().equals("Movie")) {
            songs = databaseHelper.getSongsForMovie(searchName);

            if (songs == null) {
                Utils.showError("Unable to get songs from database");
                return;
            }

            if (songs.size() == 0) {
                Utils.showInfo("No songs available");
                return;
            }

            if (songs.get(0) == null) {
                Utils.showError("Unexpected error occurred");
                return;
            }

            AddMovieWindow.addMovieEditedCallBack(this);
            (btnSearch.getScene()).getWindow().setOnCloseRequest(event -> AddMovieWindow.removeMovieEditedCallBack(this));

            MovieSongsSearchResultsWindow.setSongs(songs);
            root = FXMLLoader.load(getClass().getResource("MovieSongsSearchResultsWindow.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.show();


        } else {
            songs = databaseHelper.getSongsForArtist(searchName);

            if (songs == null) {
                Utils.showError("Unable to get songs from database");
                return;
            }

            if (songs.size() == 0) {
                Utils.showInfo("No songs available");
                return;
            }

            if (songs.get(0) == null) {
                Utils.showError("Unexpected error occurred");
                return;
            }

            ArtistSongsSearchResults.setSearchName(searchName);
            ArtistSongsSearchResults.setSongs(songs);
            root = FXMLLoader.load(getClass().getResource("ArtistSongsSearchResults.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 750, 500));
            stage.show();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchFieldComboBox.getSelectionModel().selectFirst();
        setUpMenu();
        setUpSuggestions();
    }

    private void setUpSuggestions() {
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearch, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.toLowerCase().equals(typedText.toLowerCase()));

        (new Thread(() -> {
            songNames = databaseHelper.getAllSongs();
            selectionChanged();
        })).start();


        (new Thread(() -> {
            movieNames = databaseHelper.getAllMovies();
            selectionChanged();
        })).start();

        (new Thread(() -> {
            artistNames = databaseHelper.getAllArtists();
            selectionChanged();
        })).start();
    }

    private void setUpMenu() {
        setUpMoviesMenu();
        setUpSongsMenu();
        setUpArtistMenu();
    }

    private void setUpArtistMenu() {
        Menu menuSong = menuBar.getMenus().get(2);

        MenuItem addArtistMenuItem = menuSong.getItems().get(0);

        addArtistMenuItem.setOnAction(e -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AddArtistWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
    }

    private void setUpSongsMenu() {
        Menu menuSong = menuBar.getMenus().get(1);

        MenuItem addSongMenuItem = menuSong.getItems().get(0);

        addSongMenuItem.setOnAction(e -> {
            Parent root = null;
            try {
                AddSongWIndow.setSong(null);
                root = FXMLLoader.load(getClass().getResource("AddSongWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 400));
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
    }

    private void setUpMoviesMenu() {
        Menu menuMovie = menuBar.getMenus().get(0);

        MenuItem addMovieMenuItem = menuMovie.getItems().get(0);

        addMovieMenuItem.setOnAction(e -> {
            Parent root = null;
            try {
                AddMovieWindow.setMovie(null);
                root = FXMLLoader.load(getClass().getResource("AddMovieWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 300));
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public void onKeyPressed(KeyEvent keyEvent) throws Exception {
        Object source = keyEvent.getSource();

        if (source instanceof TextField) {
            if (keyEvent.getCode() == KeyCode.ENTER)
                onSearch();
        } else if (source instanceof Button) {
            if (((Button) keyEvent.getSource()).getId().equals("btnSearch") && keyEvent.getCode() == KeyCode.ENTER) {
                onSearch();
            }
        }
    }

    public void selectionChanged() {
        if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 0) {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();
            comboBoxSearch.getItems().addAll(songNames);
            FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearch, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.toLowerCase().equals(typedText.toLowerCase()));
        } else if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 1) {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();
            comboBoxSearch.getItems().addAll(movieNames);
            FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearch, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.toLowerCase().equals(typedText.toLowerCase()));
        } else {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();
            comboBoxSearch.getItems().addAll(artistNames);
            FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearch, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.toLowerCase().equals(typedText.toLowerCase()));
        }
    }

    @Override
    public void movieEdited(Movie original, Movie edited) {
        movieNames.remove(original.getName());

        if (edited != null)
            movieNames.add(edited.getName());

        selectionChanged();
    }

    @Override
    public void songEdited(Song original, Song edited) {
        songNames.remove(original.getName());

        if (edited != null)
            songNames.add(edited.getName());

        selectionChanged();
    }
}

/*
* Close windows wherever necessary
* */

/* Future
*
* */
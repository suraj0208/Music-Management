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
import java.util.ResourceBundle;


/**
 * Created by suraj on 7/6/17.
 */
public class MainWindow extends Application implements Initializable {
    @FXML
    Button btnSearch;

    @FXML
    ComboBox<String> comboBoxSearch;

    @FXML
    ComboBox<String> searchFieldComboBox;

    @FXML
    MenuBar menuBar;

    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private ArrayList<String> songNames;
    private ArrayList<String> movieNames;

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


        if (comboBoxSearch.getSelectionModel().getSelectedItem()== null || comboBoxSearch.getSelectionModel().getSelectedItem().length() == 0) {
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

            MovieSongsSearchResultsWindow.setSongs(songs);
            root = FXMLLoader.load(getClass().getResource("MovieSongsSearchResultsWindow.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.show();
        } else {
            songs = databaseHelper.getSongsForMovie(searchName);
            MovieSongsSearchResultsWindow.setSongs(songs);
            root = FXMLLoader.load(getClass().getResource("MovieSongsSearchResultsWindow.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
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
            System.out.println("loaded songs");
            selectionChanged();
        })).start();


        (new Thread(() -> {
            movieNames = databaseHelper.getAllMovies();
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

        MenuItem addSongMenuItem = menuSong.getItems().get(0);

        addSongMenuItem.setOnAction(e -> {
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
            comboBoxSearch.getItems().clear();
            comboBoxSearch.getItems().addAll(songNames);
        } else if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 1) {
            comboBoxSearch.getItems().clear();
            comboBoxSearch.getItems().addAll(movieNames);
        }
    }
}

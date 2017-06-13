package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.FxUtilTest;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Movie;
import com.suraj.musicmanagement.data.Song;
import com.suraj.musicmanagement.interfaces.ArtistAddedCallBack;
import com.suraj.musicmanagement.interfaces.MovieEditedCallBack;
import com.suraj.musicmanagement.interfaces.SongEditedCallBack;
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
public class MainWindow extends Application implements Initializable, MovieEditedCallBack, SongEditedCallBack, ArtistAddedCallBack {
    @FXML
    private Button btnSearch;

    @FXML
    private ComboBox<String> comboBoxSearch;

    @FXML
    private ComboBox<String> searchFieldComboBox;

    @FXML
    private MenuBar menuBar;

    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private HashSet<String> songNames;
    private HashSet<String> movieNames;
    private HashSet<String> artistNames;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/MainWindow.fxml"));
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

        if (searchFieldComboBox.getValue().equals("data.Song")) {
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

            setupCloseActions();

            SongsSearchResultWindow.setSongs(songs);
            SongsSearchResultWindow.setSearchName(searchName);
            root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/SongsSearchResultWindow.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 500));
            stage.show();

        } else if (searchFieldComboBox.getValue().equals("data.Movie")) {
            songs = databaseHelper.getSongsForMovie(searchName);

            if (songs == null) {
                Utils.showError("Unable to get songs from database");
                return;
            }

            if (songs.size() == 0) {
                Utils.showInfo("No songs available");
            }

            MovieSongsSearchResultsWindow.setMovie(databaseHelper.findMovie(searchName));
            AddMovieWindow.addMovieEditedCallBack(this);
            setupCloseActions();

            MovieSongsSearchResultsWindow.setSongs(songs);
            root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/MovieSongsSearchResultsWindow.fxml"));
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
            root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/ArtistSongsSearchResults.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(root, 750, 500));
            stage.show();
        }

    }

    private void setupCloseActions() {
        (btnSearch.getScene()).getWindow().setOnCloseRequest(event -> {
            AddSongWindow.removeSongEditedCallBack(this);
            AddMovieWindow.removeMovieEditedCallBack(this);
            AddArtistWindow.removeArtistAddedCallBack(this);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchFieldComboBox.getSelectionModel().selectFirst();
        setUpMenu();
        setUpSuggestions();
        addCallBacks();
    }

    private void addCallBacks() {
        AddMovieWindow.addMovieEditedCallBack(this);
        AddSongWindow.addSongEditedCallBack(this);
        AddArtistWindow.addArtistAddedCallBack(this);
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
                setupCloseActions();
                root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/AddArtistWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        MenuItem editArtistMenuItem = menuSong.getItems().get(1);

        editArtistMenuItem.setOnAction(e -> {
            Parent root = null;
            try {
                setupCloseActions();
                root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/SearchArtistInputWindow.fxml"));
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
                AddSongWindow.setSong(null);
                setupCloseActions();

                root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/AddSongWindow.fxml"));
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
                setupCloseActions();
                root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/AddMovieWindow.fxml"));
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

            if (songNames != null)
                comboBoxSearch.getItems().addAll(songNames);
        } else if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 1) {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();

            if (movieNames != null)
                comboBoxSearch.getItems().addAll(movieNames);
        } else {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();

            if (artistNames != null)
                comboBoxSearch.getItems().addAll(artistNames);
        }
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearch, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.toLowerCase().equals(typedText.toLowerCase()));
    }

    @Override
    public void movieEdited(Movie original, Movie edited) {
        if (original != null) {
            movieNames.remove(original.getName());
            selectionChanged();
        }

        if (edited != null) {
            movieNames.add(edited.getName());
            selectionChanged();
        } else {
            (new Thread(() -> {
                songNames = databaseHelper.getAllSongs();
                selectionChanged();
            })).start();
        }

    }

    @Override
    public void songEdited(Song original, Song edited) {
        if (original != null)
            songNames.remove(original.getName());

        if (edited != null)
            songNames.add(edited.getName());

        selectionChanged();
    }

    @Override
    public void artistAdded() {
        (new Thread(() -> {
            artistNames = databaseHelper.getAllArtists();
            songNames=databaseHelper.getAllSongs();
            selectionChanged();
        })).start();
    }
}

/*
* Close windows wherever necessary
* */

/* Future
*
* */
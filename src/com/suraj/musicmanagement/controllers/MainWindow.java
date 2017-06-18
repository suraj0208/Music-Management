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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.rmi.CORBA.Util;
import java.io.File;
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
    private HashSet<String> recordNumbers;
    private String searchName;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/suraj/musicmanagement/ui/MainWindow.fxml"));
        primaryStage.setTitle("Music Management");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    public void onSearch() throws Exception {
        if (searchName == null || searchName.length() == 0) {
            Utils.showError("Invalid Input");
            return;
        }

        //searchName = comboBoxSearch.getEditor().getText();

        ArrayList<Song> songs;

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

            setupCloseActions();

            SongsSearchResultWindow.setSongs(songs);
            SongsSearchResultWindow.setSearchName(searchName);

            Utils.openWindow(getClass().getResource("../ui/SongsSearchResultWindow.fxml"), 800, 500);

        } else if (searchFieldComboBox.getValue().equals("Movie")) {

            Movie movie = databaseHelper.findMovie(searchName);

            if (movie == null) {
                Utils.showInfo("No such movie");
                return;
            }

            songs = databaseHelper.getSongsForMovie(searchName);

            if (songs == null) {
                Utils.showError("Unable to get songs from database");
                return;
            }

            if (songs.size() == 0) {
                Utils.showInfo("No songs available");
            }

            MovieSongsSearchResultsWindow.setMovie(movie);
            AddMovieWindow.addMovieEditedCallBack(this);
            setupCloseActions();

            MovieSongsSearchResultsWindow.setSongs(songs);
            Utils.openWindow(getClass().getResource("../ui/MovieSongsSearchResultsWindow.fxml"), 800, 500);

        } else if (searchFieldComboBox.getValue().equals("Artist")) {
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
            Utils.openWindow(getClass().getResource("../ui/ArtistSongsSearchResults.fxml"), 750, 500);
        } else if (searchFieldComboBox.getValue().equals("Record No")) {
            try {
                songs = databaseHelper.getSongsForMovie(Integer.parseInt(searchName));

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

                MovieSongsSearchResultsWindow.setMovie(songs.get(0).getMovie());
                MovieSongsSearchResultsWindow.setSongs(songs);
                Utils.openWindow(getClass().getResource("../ui/MovieSongsSearchResultsWindow.fxml"), 500, 500);

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                Utils.showError("Invalid search parameter");

            }


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

        comboBoxSearch.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() != 0)
                searchName = newValue;

        });

        comboBoxSearch.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue)
                        comboBoxSearch.getEditor().setText(searchName);
                }
        );
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

        (new Thread(() -> {
            recordNumbers = databaseHelper.getAllRecordNumbers();
            selectionChanged();
        })).start();
    }

    private void setUpMenu() {
        setUpMoviesMenu();
        setUpSongsMenu();
        setUpArtistMenu();
        setUpLyricistMenu();
        setUpMusicianMenu();
        setUpRecordsMenu();
    }

    private void setUpMusicianMenu() {
        Menu menuMusician = menuBar.getMenus().get(4);

        MenuItem addMusicianMenuItem = menuMusician.getItems().get(0);

        addMusicianMenuItem.setOnAction(e -> {
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/AddMusicianWindow.fxml"), 500, 200);
        });
    }

    private void setUpLyricistMenu() {
        Menu menuSong = menuBar.getMenus().get(3);

        MenuItem addLyricistMenuItem = menuSong.getItems().get(0);

        addLyricistMenuItem.setOnAction(e -> {
            AddSongWindow.setSong(null);
            setupCloseActions();

            Utils.openWindow(getClass().getResource("../ui/AddLyricistWindow.fxml"), 500, 200);
        });

    }

    private void setUpRecordsMenu() {
        Menu menuSong = menuBar.getMenus().get(5);

        MenuItem exportAllMenuItem = menuSong.getItems().get(1);

        exportAllMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(btnSearch.getScene().getWindow());

            if (file != null) {
                String path = file.getAbsolutePath();
                if (!path.endsWith(".csv"))
                    path = path + ".csv";

                if (databaseHelper.exportDatabase(path)) {
                    Utils.showInfo("Exported Successfully");
                } else {
                    Utils.showError("Error occurred while reading the database");
                }
            } else
                Utils.showError("Error occurred while setting the file path");
        });

        MenuItem displayAllItem = menuSong.getItems().get(0);

        displayAllItem.setOnAction(event -> {
            AllRecordsSearchWindow.setRecords(databaseHelper.getAllRecords());
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/AllRecordsSearchWindow.fxml"), 1050, 800);
        });

    }

    private void setUpArtistMenu() {
        Menu menuSong = menuBar.getMenus().get(2);

        MenuItem addArtistMenuItem = menuSong.getItems().get(0);

        addArtistMenuItem.setOnAction(e -> {
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/AddArtistWindow.fxml"), 500, 200);
        });

        MenuItem editArtistMenuItem = menuSong.getItems().get(1);

        editArtistMenuItem.setOnAction(e -> {
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/SearchArtistInputWindow.fxml"), 500, 200);

        });
    }

    private void setUpSongsMenu() {
        Menu menuSong = menuBar.getMenus().get(1);

        MenuItem addSongMenuItem = menuSong.getItems().get(0);

        addSongMenuItem.setOnAction(e -> {
            AddSongWindow.setSong(null);
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/AddSongWindow.fxml"), 500, 480);

        });
    }

    private void setUpMoviesMenu() {
        Menu menuMovie = menuBar.getMenus().get(0);

        MenuItem addMovieMenuItem = menuMovie.getItems().get(0);

        addMovieMenuItem.setOnAction(e -> {
            AddMovieWindow.setMovie(null);
            setupCloseActions();
            Utils.openWindow(getClass().getResource("../ui/AddMovieWindow.fxml"), 500, 300);
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
        } else if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 2) {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();

            if (artistNames != null)
                comboBoxSearch.getItems().addAll(artistNames);
        } else if (searchFieldComboBox.getSelectionModel().getSelectedIndex() == 3) {
            if (comboBoxSearch.getItems().size() > 0)
                comboBoxSearch.getItems().clear();

            if (recordNumbers != null)
                comboBoxSearch.getItems().addAll(recordNumbers);
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
            recordNumbers.add("" + edited.getRecordNo());
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
            songNames = databaseHelper.getAllSongs();
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
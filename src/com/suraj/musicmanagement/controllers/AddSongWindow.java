package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.FxUtilTest;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.*;
import com.suraj.musicmanagement.interfaces.ArtistAddedCallBack;
import com.suraj.musicmanagement.interfaces.LyricistAddedCallBack;
import com.suraj.musicmanagement.interfaces.MusicianAddedCallBack;
import com.suraj.musicmanagement.interfaces.SongEditedCallBack;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class AddSongWindow extends Application implements Initializable, ArtistAddedCallBack, LyricistAddedCallBack, MusicianAddedCallBack {
    @FXML
    private TextField txtFieldSongName;

    @FXML
    private ComboBox<String> comboBoxMovies;

    @FXML
    private ComboBox<String> comboBoxArtist1;

    @FXML
    private ComboBox<String> comboBoxArtist2;

    @FXML
    private ComboBox<String> comboBoxArtist3;

    @FXML
    private ComboBox<String> comboBoxArtist4;

    @FXML
    private ComboBox<String> comboBoxLyricist;

    @FXML
    private ComboBox<String> comboBoxMusician;

    @FXML
    private Button btnAddNewArtist;

    @FXML
    private Button btnAddNewLyricist;

    @FXML
    private Button btnAddNewMusician;

    @FXML
    private Button btnSaveSong;

    @FXML
    private Button btnDeleteSong;

    @FXML
    private Label lblNewSong;

    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private HashMap<String, Movie> stringMovieHashMap = new HashMap<>();
    private HashMap<String, Artist> stringArtistHashMap = new HashMap<>();
    private HashMap<String, Lyricist> stringLyricistHashMap = new HashMap<>();
    private HashMap<String, Musician> stringMusicianHashMap = new HashMap<>();

    private ArrayList<Artist> artists;


    private static Song song;

    private static HashSet<SongEditedCallBack> songEditedCallBacks;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/AddSongWindow.fxml"));
        primaryStage.setTitle("Add data.Song");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AddArtistWindow.addArtistAddedCallBack(this);
        AddLyricistWindow.addLyricistAddedCallBack(this);
        AddMusicianWindow.addMusicianAddedCallBack(this);

        comboBoxArtist1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSaveSong.fire();
        });

        comboBoxArtist4.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSaveSong.fire();
        });

        btnSaveSong.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSaveSong.fire();
        });

        btnDeleteSong.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnDeleteSong.fire();
        });

        btnAddNewLyricist.setOnAction(event -> {
            Utils.openWindow(getClass().getResource("../ui/AddLyricistWindow.fxml"), 500, 200);
            setupCloseActions();
        });

        btnAddNewMusician.setOnAction(event -> {
            Utils.openWindow(getClass().getResource("../ui/AddMusicianWindow.fxml"), 500, 200);
            setupCloseActions();
        });


        (new Thread(() -> {
            final ArrayList<Movie> movieArrayList = databaseHelper.getAvailableMovies();

            (new Thread(() -> {
                for (Movie movie : movieArrayList)
                    comboBoxMovies.getItems().add(movie.getName());

                loadSongDetailsIfAvailable();

            })).start();

            (new Thread(() -> {
                for (Movie movie : movieArrayList)
                    stringMovieHashMap.put(movie.getName(), movie);
            })).start();


        })).start();

        (new Thread(this::addArtistsToComboBox)).start();
        (new Thread(this::addLyricistsToComboBox)).start();
        (new Thread(this::addMusiciansToComboBox)).start();


        btnAddNewArtist.setOnAction(e -> {
            Utils.openWindow(getClass().getResource("../ui/AddArtistWindow.fxml"), 500, 200);
            setupCloseActions();
        });

        if (song != null) {
            lblNewSong.setText("Edit Song");
            final Song originalSong = song;

            btnDeleteSong.setOnAction(event -> {
                if (!Utils.confirmDialog("Do you want to delete this song?"))
                    return;

                if (databaseHelper.deleteSong(song)) {
                    Utils.showInfo("Song Deleted");

                    if (songEditedCallBacks != null)
                        for (SongEditedCallBack songEditedCallBack : songEditedCallBacks)
                            if (songEditedCallBack != null)
                                songEditedCallBack.songEdited(originalSong, null);

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }

            });

            btnSaveSong.setOnAction(e -> {
                if (!Utils.confirmDialog("Do you want to save the changes?"))
                    return;

                if (!prepareSong())
                    return;

                if (databaseHelper.updateSong(song)) {
                    Utils.showInfo("Song Saved");

                    if (songEditedCallBacks != null)
                        for (SongEditedCallBack songEditedCallBack : songEditedCallBacks)
                            if (songEditedCallBack != null)
                                songEditedCallBack.songEdited(originalSong, song);

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }

            });

            return;
        }

        btnDeleteSong.setVisible(false);

        btnSaveSong.setOnAction(e -> {
            if (!prepareSong())
                return;

            databaseHelper.addSong(song);

            txtFieldSongName.clear();

            for (SongEditedCallBack songEditedCallBack : songEditedCallBacks)
                songEditedCallBack.songEdited(null, song);

            Utils.showInfo("Song Saved");
        });
    }

    private void addMusiciansToComboBox() {
        ArrayList<Musician> musicians = databaseHelper.getAvailableMusicians();

        if (musicians == null) {
            Utils.showError("Unable to get musicians data");
            return;
        }

        comboBoxMusician.getItems().clear();

        for (Musician musician : musicians) {
            comboBoxMusician.getItems().add(musician.getName());
            stringMusicianHashMap.put(musician.getName(), musician);
        }

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxMusician, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
    }

    private void addLyricistsToComboBox() {
        ArrayList<Lyricist> lyricists = databaseHelper.getAvailableLyricists();

        if (lyricists == null) {
            Utils.showError("Unable to get lyricists data");
            return;
        }


        comboBoxLyricist.getItems().clear();

        for (Lyricist lyricist : lyricists) {
            comboBoxLyricist.getItems().add(lyricist.getName());
            stringLyricistHashMap.put(lyricist.getName(), lyricist);
        }

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxLyricist, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
    }

    private void setupCloseActions() {
        btnSaveSong.getScene().getWindow().setOnCloseRequest(event -> {
            AddArtistWindow.removeArtistAddedCallBack(this);
            AddLyricistWindow.removeLyricistAddedCallBack(this);
            AddMusicianWindow.removeMusicianAddedCallBack(this);
        });
    }

    private void closeWindow() {
        ((Stage) (btnSaveSong.getScene()).getWindow()).close();
    }

    private void loadSongDetailsIfAvailable() {
        if (song != null) {
            txtFieldSongName.setText(song.getName());
            comboBoxMovies.setValue(song.getMovie().getName());

            ComboBox[] stringComboBoxes = new ComboBox[]{comboBoxArtist1, comboBoxArtist2, comboBoxArtist3, comboBoxArtist4};

            for (int i = 0; i < stringComboBoxes.length && i < song.getArtists().size(); i++)
                if (stringComboBoxes[i] != null)
                    stringComboBoxes[i].setValue(song.getArtists().get(i).getName());

            comboBoxLyricist.setValue(song.getLyricist().getName());
            comboBoxMusician.setValue(song.getMusician().getName());
        }
    }

    private boolean prepareSong() {
        if (txtFieldSongName.getText().length() == 0) {
            Utils.showError("Fill required fields");
            return false;
        }

        if (comboBoxMovies.getValue().length() == 0) {
            Utils.showError("Fill required fields");
            return false;
        }

        if (comboBoxArtist1.getSelectionModel().getSelectedItem() == null) {
            Utils.showError("Invalid Information");
            return false;
        }

        Movie movie = stringMovieHashMap.get(comboBoxMovies.getValue());

        if (movie == null) {
            Utils.showError("No such movie");
            return false;
        }

        String artist1 = comboBoxArtist1.getValue();
        String artist2 = comboBoxArtist2.getValue();
        String artist3 = comboBoxArtist3.getValue();
        String artist4 = comboBoxArtist4.getValue();


        if (!doesArtistExist(artist1) || !doesArtistExist(artist2) || !doesArtistExist(artist3) || !doesArtistExist(artist4)) {
            Utils.showError("Artist(s) does not exist");
            return false;
        }

        ArrayList<Artist> currentArtists = new ArrayList<>();

        if (stringArtistHashMap.containsKey(artist1))
            currentArtists.add(stringArtistHashMap.get(artist1));

        if (stringArtistHashMap.containsKey(artist2))
            currentArtists.add(stringArtistHashMap.get(artist2));

        if (stringArtistHashMap.containsKey(artist3))
            currentArtists.add(stringArtistHashMap.get(artist3));

        if (stringArtistHashMap.containsKey(artist4))
            currentArtists.add(stringArtistHashMap.get(artist4));

        Lyricist lyricist = stringLyricistHashMap.get(comboBoxLyricist.getValue());

        if (lyricist == null) {
            Utils.showError("No such Lyricist");
            return false;
        }

        Musician musician = stringMusicianHashMap.get(comboBoxMusician.getValue());

        if (musician == null) {
            Utils.showError("No such Musician");
            return false;
        }

        if (song != null) {
            song = new Song(song.getId(), txtFieldSongName.getText(), movie.getId(), movie, currentArtists, lyricist, musician);
            return true;
        }
        song = new Song(txtFieldSongName.getText(), movie.getId(), movie, currentArtists, lyricist, musician);
        return true;
    }

    public static void setSong(Song song) {
        AddSongWindow.song = song;
    }

    private boolean doesArtistExist(String artist1) {
        if (artist1 == null || artist1.length() == 0)
            return true;

        return stringArtistHashMap.containsKey(artist1);
    }

    @Override
    public void artistAdded() {
        addArtistsToComboBox();
    }

    public void addArtistsToComboBox() {
        comboBoxArtist1.getItems().clear();
        comboBoxArtist2.getItems().clear();
        comboBoxArtist3.getItems().clear();
        comboBoxArtist3.getItems().clear();

        stringArtistHashMap.clear();

        artists = databaseHelper.getAvailableArtists();

        for (Artist artist : artists) {
            comboBoxArtist1.getItems().add(artist.getName());
            comboBoxArtist2.getItems().add(artist.getName());
            comboBoxArtist3.getItems().add(artist.getName());
            comboBoxArtist4.getItems().add(artist.getName());
            stringArtistHashMap.put(artist.getName(), artist);
        }

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxMovies, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist1, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist2, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist3, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist4, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));

    }

    public static void addSongEditedCallBack(SongEditedCallBack songEditedCallBack) {
        if (songEditedCallBacks == null)
            songEditedCallBacks = new HashSet<>();

        songEditedCallBacks.add(songEditedCallBack);
    }

    public static void removeSongEditedCallBack(SongEditedCallBack songEditedCallBack) {
        if (songEditedCallBacks == null)
            return;

        songEditedCallBacks.remove(songEditedCallBack);
    }

    @Override
    public void lyricistAdded() {
        addLyricistsToComboBox();
    }

    @Override
    public void musicianAdded() {
        addMusiciansToComboBox();
    }
}


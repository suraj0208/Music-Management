package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.FxUtilTest;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Artist;
import com.suraj.musicmanagement.data.Movie;
import com.suraj.musicmanagement.data.Song;
import com.suraj.musicmanagement.interfaces.ArtistAddedCallBack;
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
public class AddSongWindow extends Application implements Initializable, ArtistAddedCallBack {
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
    private Button btnAddNewArtist;

    @FXML
    private Button btnSaveSong;

    @FXML
    private Button btnDeleteSong;

    @FXML
    private Label lblNewSong;

    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private HashMap<String, Movie> stringMovieHashMap = new HashMap<>();

    private ArrayList<Artist> artists;

    private HashMap<String, Artist> stringArtistHashMap = new HashMap<>();

    private static Song song;

    private static HashSet<SongEditedCallBack> songEditedCallBacks;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/AddSongWindow.fxml"));
        primaryStage.setTitle("Add data.Song");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AddArtistWindow.addArtistAddedCallBack(this);

        comboBoxArtist1.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER)
                btnSaveSong.fire();
        });

        comboBoxArtist4.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER)
                btnSaveSong.fire();
        });

        btnSaveSong.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER)
                btnSaveSong.fire();
        });

        btnDeleteSong.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER)
                btnDeleteSong.fire();
        });

        (new Thread(() -> {
            final ArrayList<Movie> movieArrayList = databaseHelper.getAvailableMovies();

            (new Thread(() -> {
                for (Movie movie : movieArrayList)
                    comboBoxMovies.getItems().add(movie.getName());

                addArtistsToComboBox();
                loadSongDetailsIfAvailable();

            })).start();

            (new Thread(() -> {
                for (Movie movie : movieArrayList)
                    stringMovieHashMap.put(movie.getName(), movie);
            })).start();

        })).start();


        btnAddNewArtist.setOnAction(e -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("com/suraj/musicmanagement/ui/AddArtistWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));
                setupCloseActions();
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        if (song != null) {
            lblNewSong.setText("Edit data.Song");
            final Song originalSong = song;

            btnDeleteSong.setOnAction(event -> {
                if (!Utils.confirmDialog("Do you want to delete this song?"))
                    return;

                if (databaseHelper.deleteSong(song)) {
                    Utils.showInfo("data.Song Deleted");

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
                    Utils.showInfo("data.Song Saved");

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

            Utils.showInfo("data.Song Saved");
        });
    }

    private void setupCloseActions() {
        btnSaveSong.getScene().getWindow().setOnCloseRequest(event -> {
            AddArtistWindow.removeArtistAddedCallBack(this);
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
            Utils.showError("data.Artist(s) does not exist");
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

        if (song != null) {
            song = new Song(song.getId(), txtFieldSongName.getText(), movie.getId(), currentArtists);
            song.setMovie(movie);
            return true;
        }
        song = new Song(txtFieldSongName.getText(), movie.getId(), currentArtists);
        song.setMovie(movie);
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

}


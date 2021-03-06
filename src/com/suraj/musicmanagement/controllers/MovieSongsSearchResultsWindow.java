package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.*;
import com.suraj.musicmanagement.interfaces.MovieEditedCallBack;
import com.suraj.musicmanagement.interfaces.SongEditedCallBack;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class MovieSongsSearchResultsWindow extends Application implements Initializable, MovieEditedCallBack, SongEditedCallBack {

    @FXML
    private Label lblMovieName;

    @FXML
    private Label lblMovieLanguage;

    @FXML
    private Label lblMovieYear;

    @FXML
    private Label lblMovieRecordNumber;

    @FXML
    private TableView<Song> tblSearchResults;

    @FXML
    private Button btnEditMovie;

    @FXML
    private BorderPane borderPaneRoot;

    private static ArrayList<Song> songs;

    public static void setMovie(Movie movie) {
        MovieSongsSearchResultsWindow.movie = movie;
    }

    private static Movie movie;

    public static void setSongs(ArrayList<Song> songs) {
        MovieSongsSearchResultsWindow.songs = songs;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/MovieSongsSearchResultsWindow.fxml"));
        primaryStage.setTitle("Search Results");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //movie = songs.get(0).getMovie();
        setMovieDetails();
        setSongsTable();
        setupEditButton();
    }

    private void setupCloseEvent() {
        borderPaneRoot.getScene().getWindow().setOnCloseRequest(event -> AddMovieWindow.removeMovieEditedCallBack(this));
    }

    private void setupEditButton() {
        btnEditMovie.setOnAction(e -> {
            AddMovieWindow.setMovie(movie);
            AddMovieWindow.addMovieEditedCallBack(this);
            setupCloseEvent();
            Utils.openWindow(getClass().getResource("../ui/AddMovieWindow.fxml"), 500, 300);
        });
    }

    private void setSongsTable() {
        TableColumn<Song, String> tableColumnSongName = new TableColumn<>("Song Name");

        tableColumnSongName.setMinWidth(200);
        tableColumnSongName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Song, String> tableColumnArtists = new TableColumn<>("Artists");
        tableColumnArtists.setMinWidth(300);

        tableColumnArtists.setCellValueFactory(param -> {
            ArrayList<Artist> artists = param.getValue().getArtists();

            StringBuilder stringBuffer = new StringBuilder();

            for (int i = 0; i < artists.size(); i++) {
                stringBuffer.append(artists.get(i).getName());

                if (i != artists.size() - 1) {
                    stringBuffer.append(", ");
                }
            }

            return new SimpleStringProperty(stringBuffer.toString());
        });

        TableColumn<Song, String> tableColumnLyricist = new TableColumn<>("Lyricist");
        tableColumnLyricist.setMinWidth(150);

        tableColumnLyricist.setCellValueFactory(param -> {
            Lyricist lyricist = param.getValue().getLyricist();
            return new SimpleStringProperty(lyricist.getName());

        });

        TableColumn<Song, String> tableColumnMusician = new TableColumn<>("Musician");
        tableColumnMusician.setMinWidth(150);

        tableColumnMusician.setCellValueFactory(param -> {
            Musician musician = param.getValue().getMusician();
            return new SimpleStringProperty(musician.getName());

        });


        ObservableList<Song> songObservableList = FXCollections.observableArrayList();

        songObservableList.addAll(songs);

        tblSearchResults.getColumns().add(tableColumnSongName);
        tblSearchResults.getColumns().add(tableColumnArtists);
        tblSearchResults.getColumns().add(tableColumnLyricist);
        tblSearchResults.getColumns().add(tableColumnMusician);

        tblSearchResults.setItems(songObservableList);

        tblSearchResults.setRowFactory(tv -> {
            TableRow<Song> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && tableRow.getItem() != null) {
                    AddSongWindow.setSong(tableRow.getItem());

                    AddSongWindow.addSongEditedCallBack(this);

                    (tblSearchResults.getScene()).getWindow().setOnCloseRequest(event1 -> {
                        AddSongWindow.removeSongEditedCallBack(this);
                    });

                    Utils.openWindow(getClass().getResource("../ui/AddSongWindow.fxml"), 500, 480);

                }
            });
            return tableRow;
        });

    }

    private void setMovieDetails() {
        lblMovieName.setText(movie.getName());
        lblMovieLanguage.setText("Language: " + movie.getLanguage().getName());
        lblMovieYear.setText("Year: " + movie.getYear());
        lblMovieRecordNumber.setText("Record No: " + movie.getRecordNo());
    }

    @Override
    public void movieEdited(Movie original, Movie edited) {
        if (edited == null) {
            closeWindow();
            return;
        }
        MovieSongsSearchResultsWindow.movie = edited;
        setMovieDetails();
    }

    private void closeWindow() {
        ((Stage) (btnEditMovie.getScene()).getWindow()).close();
    }

    @Override
    public void songEdited(Song original, Song edited) {
        for (Song song : tblSearchResults.getItems())
            if (song.getId() == original.getId()) {
                tblSearchResults.getItems().remove(song);
                break;
            }

        if (edited != null)
            tblSearchResults.getItems().add(edited);
    }
}


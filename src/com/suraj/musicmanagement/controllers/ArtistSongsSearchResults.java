package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.data.Artist;
import com.suraj.musicmanagement.data.Lyricist;
import com.suraj.musicmanagement.data.Musician;
import com.suraj.musicmanagement.data.Song;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by suraj on 11/6/17.
 */
public class ArtistSongsSearchResults extends Application implements Initializable {

    @FXML
    private Label lblSearchArtistName;

    @FXML
    private TableView<Song> tblSearchResults;

    private static ArrayList<Song> songs;

    private static String searchName;

    public static void setSongs(ArrayList<Song> songs) {
        ArtistSongsSearchResults.songs = songs;
    }

    public static void setSearchName(String searchName) {
        ArtistSongsSearchResults.searchName = searchName;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/ArtistSongsSearchResults.fxml"));
        primaryStage.setTitle("Search Results");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblSearchArtistName.setText("Songs by '" + searchName + "'");
        setSongsTable();
    }

    private void setSongsTable() {
        TableColumn<Song, String> tableColumnSongName = new TableColumn<>("Song");
        tableColumnSongName.setMinWidth(150);
        tableColumnSongName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Song, String> tableColumnMovie = new TableColumn<>("Movie");
        tableColumnMovie.setMinWidth(150);
        tableColumnMovie.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMovie().getName()));

        TableColumn<Song, Integer> tableColumnYear = new TableColumn<>("Year");
        tableColumnSongName.setMinWidth(150);
        tableColumnYear.setCellValueFactory(param -> new ObservableValue<Integer>() {
            @Override
            public void addListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public Integer getValue() {
                return param.getValue().getMovie().getYear();
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        });

        TableColumn<Song, Integer> tableColumnRecordNo = new TableColumn<>("Record No");
        tableColumnRecordNo.setMinWidth(150);
        tableColumnRecordNo.setCellValueFactory(param -> new ObservableValue<Integer>() {
            @Override
            public void addListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public Integer getValue() {
                return param.getValue().getMovie().getRecordNo();
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        });


        TableColumn<Song, String> tableColumnArtists = new TableColumn<>("Artists");
        tableColumnArtists.setMinWidth(250);

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
        tblSearchResults.getColumns().add(tableColumnMovie);
        tblSearchResults.getColumns().add(tableColumnYear);
        tblSearchResults.getColumns().add(tableColumnRecordNo);

        tblSearchResults.setItems(songObservableList);

    }

}

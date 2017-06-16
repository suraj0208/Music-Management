package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.data.Record;
import com.suraj.musicmanagement.data.Song;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by suraj on 16/6/17.
 */
public class AllRecordsSearchWindow extends Application implements Initializable {
    @FXML
    private TableView<Record> tblSearchResults;

    private static ArrayList<Record> records;

    public static void setRecords(ArrayList<Record> records) {
        AllRecordsSearchWindow.records = records;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Record, String> tableColumnSongName = new TableColumn<>("Song");
        tableColumnSongName.setMinWidth(200);
        tableColumnSongName.setCellValueFactory(new PropertyValueFactory<>("songName"));

        TableColumn<Record, String> tableColumnArtists = new TableColumn<>("Artists");
        tableColumnArtists.setMinWidth(250);
        tableColumnArtists.setCellValueFactory(new PropertyValueFactory<>("artists"));

        TableColumn<Record, String> tableColumnMovie = new TableColumn<>("Movie");
        tableColumnMovie.setMinWidth(200);
        tableColumnMovie.setCellValueFactory(new PropertyValueFactory<>("movieName"));

        TableColumn<Record, Integer> tableColumnYear = new TableColumn<>("Year");
        tableColumnYear.setMinWidth(100);
        tableColumnYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Record, String> tableColumnLanguage = new TableColumn<>("Language");
        tableColumnLanguage.setMinWidth(200);
        tableColumnLanguage.setCellValueFactory(new PropertyValueFactory<>("language"));

        TableColumn<Record, Integer> tableColumnRecordNo = new TableColumn<>("Record No");
        tableColumnRecordNo.setMinWidth(100);
        tableColumnRecordNo.setCellValueFactory(new PropertyValueFactory<>("recordNo"));

        tblSearchResults.getColumns().add(tableColumnSongName);
        tblSearchResults.getColumns().add(tableColumnArtists);
        tblSearchResults.getColumns().add(tableColumnMovie);
        tblSearchResults.getColumns().add(tableColumnYear);
        tblSearchResults.getColumns().add(tableColumnLanguage);
        tblSearchResults.getColumns().add(tableColumnRecordNo);

        tblSearchResults.getItems().addAll(records);

    }
}

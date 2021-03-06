package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.FxUtilTest;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Musician;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by suraj on 19/6/17.
 */
public class SearchMusicianInputWindow extends Application implements Initializable {
    @FXML
    private ComboBox<String> comboBoxSearchName;

    @FXML
    private Button btnSearchMusician;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/SearchMusicianInputWindow.fxml"));
        primaryStage.setTitle("Search Musician");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        HashSet<String> musicians = databaseHelper.getAllMusicians();

        comboBoxSearchName.getItems().addAll(musicians);

        comboBoxSearchName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSearchMusician.fire();
        });

        btnSearchMusician.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSearchMusician.fire();
        });

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearchName, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || typedText.toLowerCase().equals(objectToCompare.toLowerCase()));

        btnSearchMusician.setOnAction(event -> {
            Musician musician = databaseHelper.getMusicianFromName(comboBoxSearchName.getValue());

            if (musician == null) {
                Utils.showError("Unable to find musician");
                return;
            }

            AddMusicianWindow.setMusician(musician);
            Utils.openWindow(getClass().getResource("../ui/AddMusicianWindow.fxml"), 500, 200);
            closeWindow();
        });
    }

    private void closeWindow() {
        ((Stage) (btnSearchMusician.getScene()).getWindow()).close();
    }
}


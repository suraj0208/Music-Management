package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.FxUtilTest;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Lyricist;
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
public class SearchLyricistInputWindow extends Application implements Initializable {
    @FXML
    private ComboBox<String> comboBoxSearchName;

    @FXML
    private Button btnSearchLyricist;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/SearchLyricistInputWindow.fxml"));
        primaryStage.setTitle("Search Lyricist");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        HashSet<String> lyricists = databaseHelper.getAllLyricists();

        comboBoxSearchName.getItems().addAll(lyricists);

        comboBoxSearchName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSearchLyricist.fire();
        });

        btnSearchLyricist.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSearchLyricist.fire();
        });

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearchName, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || typedText.toLowerCase().equals(objectToCompare.toLowerCase()));

        btnSearchLyricist.setOnAction(event -> {
            Lyricist lyricist = databaseHelper.getLyricistFromName(comboBoxSearchName.getValue());

            if (lyricist == null) {
                Utils.showError("Unable to find lyricist");
                return;
            }

            AddLyricistWindow.setLyricist(lyricist);
            Utils.openWindow(getClass().getResource("../ui/AddLyricistWindow.fxml"), 500, 200);
            closeWindow();
        });
    }

    private void closeWindow() {
        ((Stage) (btnSearchLyricist.getScene()).getWindow()).close();
    }
}

package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Musician;
import com.suraj.musicmanagement.interfaces.MusicianAddedCallBack;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by suraj on 18/6/17.
 */
public class AddMusicianWindow extends Application implements Initializable {
    @FXML
    private Button btnSaveMusician;

    @FXML
    private TextField txtFieldMusicianName;

    @FXML
    private Label lblAddMusicianWindowTitle;

    @FXML
    private Button btnDeleteMusician;

    private static Musician musician;

    private static HashSet<MusicianAddedCallBack> musicianAddedCallBacks;

    public static void addMusicianAddedCallBack(MusicianAddedCallBack musicianAddedCallBack) {
        if (musicianAddedCallBacks == null)
            musicianAddedCallBacks = new HashSet<>();

        musicianAddedCallBacks.add(musicianAddedCallBack);
    }

    public static void removeMusicianAddedCallBack(MusicianAddedCallBack musicianAddedCallBack) {
        if (musicianAddedCallBacks == null)
            return;

        musicianAddedCallBacks.remove(musicianAddedCallBack);
    }

    public static void setMusician(Musician musician) {
        AddMusicianWindow.musician = musician;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/AddMusicianWindow.fxml"));
        primaryStage.setTitle("Add Musician");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtFieldMusicianName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveMusician.fire();
        });

        btnSaveMusician.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveMusician.fire();
        });

        if (musician != null) {
            lblAddMusicianWindowTitle.setText("Edit Musician Name");

            txtFieldMusicianName.setText(musician.getName());

            btnSaveMusician.setOnAction(event -> {
                if (txtFieldMusicianName.getText().length() == 0) {
                    Utils.showError("Invalid Input");
                    return;
                }
                musician.setName(txtFieldMusicianName.getText());
                if (new DatabaseHelper().updateMusician(musician)) {
                    Utils.showInfo("Information updated");
                    closeWindow();

                    if (musicianAddedCallBacks != null)
                        for (MusicianAddedCallBack musicianAddedCallBack : musicianAddedCallBacks)
                            if (musicianAddedCallBack != null)
                                musicianAddedCallBack.musicianAdded();

                } else {
                    Utils.showError("Error Occurred");
                }
            });

            btnDeleteMusician.setOnAction(event -> {
                if (!Utils.confirmDialog("Do you want to delete musician and all its songs?"))
                    return;

                if (new DatabaseHelper().deleteMusician(musician)) {
                    Utils.showInfo("Musician Deleted");

                    if (musicianAddedCallBacks != null)
                        for (MusicianAddedCallBack musicianAddedCallBack : musicianAddedCallBacks)
                            if (musicianAddedCallBack != null)
                                musicianAddedCallBack.musicianAdded();

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }
            });

            return;
        }

        btnDeleteMusician.setVisible(false);
        btnSaveMusician.setOnAction(e -> addMusician());
    }

    private void closeWindow() {
        ((Stage) (btnSaveMusician.getScene()).getWindow()).close();
    }

    public void addMusician() {
        if (txtFieldMusicianName.getText().length() == 0) {
            Utils.showError("Invalid Input");
            return;
        }

        Musician musician = new Musician(txtFieldMusicianName.getText());
        new DatabaseHelper().addMusician(musician);

        if (musicianAddedCallBacks != null)
            for (MusicianAddedCallBack musicianAddedCallBack : musicianAddedCallBacks)
                if (musicianAddedCallBack != null)
                    musicianAddedCallBack.musicianAdded();

        closeWindow();
    }
}

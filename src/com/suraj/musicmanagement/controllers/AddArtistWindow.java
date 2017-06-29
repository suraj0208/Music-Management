package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Artist;
import com.suraj.musicmanagement.interfaces.ArtistAddedCallBack;
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
 * Created by suraj on 8/6/17.
 */
public class AddArtistWindow extends Application implements Initializable {
    @FXML
    private Button btnSaveArtist;

    @FXML
    private TextField txtFieldArtistName;

    @FXML
    private Label lblAddArtistWindowTitle;

    @FXML
    private Button btnDeleteArtist;

    private static Artist artist;

    private static HashSet<ArtistAddedCallBack> artistAddedCallBacks;

    public static void addArtistAddedCallBack(ArtistAddedCallBack artistAddedCallBack) {
        if (artistAddedCallBacks == null)
            artistAddedCallBacks = new HashSet<>();

        artistAddedCallBacks.add(artistAddedCallBack);
    }

    public static void removeArtistAddedCallBack(ArtistAddedCallBack artistAddedCallBack) {
        if (artistAddedCallBacks == null)
            return;

        artistAddedCallBacks.remove(artistAddedCallBack);
    }

    public static void setArtist(Artist artist) {
        AddArtistWindow.artist = artist;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/suraj/musicmanagement/ui/AddArtistWindow.fxml"));
        primaryStage.setTitle("Add Artist");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtFieldArtistName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveArtist.fire();
        });

        btnSaveArtist.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveArtist.fire();
        });

        if (artist != null) {
            lblAddArtistWindowTitle.setText("Edit Artist Name");

            txtFieldArtistName.setText(artist.getName());

            btnSaveArtist.setOnAction(event -> {
                if (txtFieldArtistName.getText().length() == 0) {
                    Utils.showError("Invalid Input");
                    return;
                }
                artist.setName(txtFieldArtistName.getText());
                if (new DatabaseHelper().updateArtist(artist)) {
                    Utils.showInfo("Information updated");
                    closeWindow();

                    if (artistAddedCallBacks != null)
                        for (ArtistAddedCallBack artistAddedCallBack : artistAddedCallBacks)
                            if (artistAddedCallBack != null)
                                artistAddedCallBack.artistAdded();

                } else {
                    Utils.showError("Error Occurred");
                }
            });

            btnDeleteArtist.setOnAction(event -> {
                if (!Utils.confirmDialog("Do you want to delete artist and all its songs?"))
                    return;

                if (new DatabaseHelper().deleteArtist(artist)) {
                    Utils.showInfo("Artist Deleted");

                    if (artistAddedCallBacks != null)
                        for (ArtistAddedCallBack artistAddedCallBack : artistAddedCallBacks)
                            if (artistAddedCallBack != null)
                                artistAddedCallBack.artistAdded();

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }
            });

            return;
        }

        btnDeleteArtist.setVisible(false);
        btnSaveArtist.setOnAction(e -> addArtist());
    }

    private void closeWindow() {
        ((Stage) (btnSaveArtist.getScene()).getWindow()).close();
    }

    public void addArtist() {
        if (txtFieldArtistName.getText().length() == 0) {
            Utils.showError("Invalid Input");
            return;
        }

        Artist artist = new Artist(txtFieldArtistName.getText());
        new DatabaseHelper().addArtist(artist);

        if (artistAddedCallBacks != null)
            for (ArtistAddedCallBack artistAddedCallBack : artistAddedCallBacks)
                if (artistAddedCallBack != null)
                    artistAddedCallBack.artistAdded();

        closeWindow();
    }
}

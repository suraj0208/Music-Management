package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Lyricist;
import com.suraj.musicmanagement.interfaces.LyricistAddedCallBack;
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
public class AddLyricistWindow extends Application implements Initializable {
    @FXML
    private Button btnSaveLyricist;

    @FXML
    private TextField txtFieldLyricistName;

    @FXML
    private Label lblAddLyricistWindowTitle;

    @FXML
    private Button btnDeleteLyricist;

    private static Lyricist lyricist;

    private static HashSet<LyricistAddedCallBack> lyricistAddedCallBacks;

    public static void addLyricistAddedCallBack(LyricistAddedCallBack lyricistAddedCallBack) {
        if (lyricistAddedCallBacks == null)
            lyricistAddedCallBacks = new HashSet<>();

        lyricistAddedCallBacks.add(lyricistAddedCallBack);
    }

    public static void removeLyricistAddedCallBack(LyricistAddedCallBack lyricistAddedCallBack) {
        if (lyricistAddedCallBacks == null)
            return;

        lyricistAddedCallBacks.remove(lyricistAddedCallBack);
    }

    public static void setLyricist(Lyricist lyricist) {
        AddLyricistWindow.lyricist = lyricist;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/AddLyricistWindow.fxml"));
        primaryStage.setTitle("Add Lyricist");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtFieldLyricistName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveLyricist.fire();
        });

        btnSaveLyricist.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) btnSaveLyricist.fire();
        });

        if (lyricist != null) {
            lblAddLyricistWindowTitle.setText("Edit Lyricist Name");

            txtFieldLyricistName.setText(lyricist.getName());

            btnSaveLyricist.setOnAction(event -> {
                if (txtFieldLyricistName.getText().length() == 0) {
                    Utils.showError("Invalid Input");
                    return;
                }
                lyricist.setName(txtFieldLyricistName.getText());
                if (new DatabaseHelper().updateLyricist(lyricist)) {
                    Utils.showInfo("Information updated");
                    closeWindow();

                    if (lyricistAddedCallBacks != null)
                        for (LyricistAddedCallBack lyricistAddedCallBack : lyricistAddedCallBacks)
                            if (lyricistAddedCallBack != null)
                                lyricistAddedCallBack.lyricistAdded();

                } else {
                    Utils.showError("Error Occurred");
                }
            });

            btnDeleteLyricist.setOnAction(event -> {
                if (!Utils.confirmDialog("Do you want to delete lyricist and all its songs?"))
                    return;

                if (new DatabaseHelper().deleteLyricist(lyricist)) {
                    Utils.showInfo("Lyricist Deleted");

                    if (lyricistAddedCallBacks != null)
                        for (LyricistAddedCallBack lyricistAddedCallBack : lyricistAddedCallBacks)
                            if (lyricistAddedCallBack != null)
                                lyricistAddedCallBack.lyricistAdded();

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }
            });

            return;
        }

        btnDeleteLyricist.setVisible(false);
        btnSaveLyricist.setOnAction(e -> addLyricist());
    }

    private void closeWindow() {
        ((Stage) (btnSaveLyricist.getScene()).getWindow()).close();
    }

    public void addLyricist() {
        if (txtFieldLyricistName.getText().length() == 0) {
            Utils.showError("Invalid Input");
            return;
        }

        Lyricist lyricist = new Lyricist(txtFieldLyricistName.getText());
        new DatabaseHelper().addLyricist(lyricist);

        if (lyricistAddedCallBacks != null)
            for (LyricistAddedCallBack lyricistAddedCallBack : lyricistAddedCallBacks)
                if (lyricistAddedCallBack != null)
                    lyricistAddedCallBack.lyricistAdded();

        closeWindow();
    }
}

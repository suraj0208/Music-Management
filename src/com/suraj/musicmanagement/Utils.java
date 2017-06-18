package com.suraj.musicmanagement;

import com.suraj.musicmanagement.controllers.AddSongWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created by suraj on 8/6/17.
 */
public class Utils {
    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();

        /*Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + selection + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            //do stuff
        }*/

//      Stage dialog = new Stage();
//      dialog.initStyle(StageStyle.UTILITY);
//      Scene scene = new Scene(new Group(new Text(20, 20, msg)), 150, 50);
//      dialog.setScene(scene);
//      dialog.show();
    }

    public static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();

        /*Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + selection + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            //do stuff
        }*/

//      Stage dialog = new Stage();
//      dialog.initStyle(StageStyle.UTILITY);
//      Scene scene = new Scene(new Group(new Text(20, 20, msg)), 150, 50);
//      dialog.setScene(scene);
//      dialog.show();
    }

    public static boolean confirmDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            return true;
        }

        return false;
    }

    public static void openWindow(URL location, int width, int height){
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(location);

            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

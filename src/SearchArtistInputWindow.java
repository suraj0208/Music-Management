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

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by suraj on 12/6/17.
 */

public class SearchArtistInputWindow extends Application implements Initializable {
    @FXML
    private ComboBox<String> comboBoxSearchName;

    @FXML
    private Button btnSearchArtist;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SearchArtistInputWindow.fxml"));
        primaryStage.setTitle("Search Artist");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        HashSet<String> artists = databaseHelper.getAllArtists();

        comboBoxSearchName.getItems().addAll(artists);

        comboBoxSearchName.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER)
                btnSearchArtist.fire();
        });

        FxUtilTest.autoCompleteComboBoxPlus(comboBoxSearchName,(typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || typedText.toLowerCase().equals(objectToCompare.toLowerCase()));

        btnSearchArtist.setOnAction(event -> {
            Parent root = null;
            try {
                Artist artist = databaseHelper.getArtistFromName(comboBoxSearchName.getValue());

                if(artist==null){
                    Utils.showError("Unable to find artist");
                    return;
                }

                AddArtistWindow.setArtist(artist);
                root = FXMLLoader.load(getClass().getResource("AddArtistWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));
                stage.show();
                closeWindow();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void closeWindow() {
        ((Stage) (btnSearchArtist.getScene()).getWindow()).close();
    }
}

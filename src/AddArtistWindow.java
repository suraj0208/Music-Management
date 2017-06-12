import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AddArtistWindow.fxml"));
        primaryStage.setTitle("Add Artist");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSaveArtist.setOnAction(e -> addArtist());
        txtFieldArtistName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) addArtist();
        });

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

        ((Stage) (btnSaveArtist.getScene()).getWindow()).close();
    }
}

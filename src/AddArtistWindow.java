import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class AddArtistWindow extends Application implements Initializable {
    @FXML
    private Button btnSaveArtist;

    @FXML
    private TextField txtFieldArtistName;

    private static ArtistAdddedCallBack artistAdddedCallBack;

    public static void setArtistAdddedCallBack(ArtistAdddedCallBack artistAdddedCallBack) {
        AddArtistWindow.artistAdddedCallBack = artistAdddedCallBack;
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
        btnSaveArtist.setOnAction(e -> {
            if (txtFieldArtistName.getText().length() == 0) {
                Utils.showError("Invalid Input");
                return;
            }

            Artist artist = new Artist(txtFieldArtistName.getText());
            new DatabaseHelper().addArtist(artist);

            if (artistAdddedCallBack != null)
                artistAdddedCallBack.artistAdded();

            ((Stage) (btnSaveArtist.getScene()).getWindow()).close();
        });

    }
}

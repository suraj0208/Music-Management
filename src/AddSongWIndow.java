import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class AddSongWIndow extends Application implements Initializable, ArtistAdddedCallBack {
    @FXML
    private TextField txtFieldSongName;

    @FXML
    private ComboBox<String> comboBoxMovies;

    @FXML
    private ComboBox<String> comboBoxArtist1;

    @FXML
    private ComboBox<String> comboBoxArtist2;

    @FXML
    private ComboBox<String> comboBoxArtist3;

    @FXML
    private ComboBox<String> comboBoxArtist4;

    @FXML
    private Button btnAddNewArtist;

    @FXML
    private Button btnSaveSong;

    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private HashMap<String, Movie> stringMovieHashMap = new HashMap<>();
    private ArrayList<Artist> artists;
    private HashMap<String, Artist> stringArtistHashMap = new HashMap<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AddSongWindow.fxml"));
        primaryStage.setTitle("Add Song");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxMovies, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist1, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist2, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist3, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));
        FxUtilTest.autoCompleteComboBoxPlus(comboBoxArtist4, (typedText, objectToCompare) -> objectToCompare.toLowerCase().contains(typedText.toLowerCase()) || objectToCompare.equals(typedText));


        (new Thread(() -> {
            ArrayList<Movie> movieArrayList = databaseHelper.getAvailableMovies();

            (new Thread(() -> {
                for (Movie movie : movieArrayList)
                    comboBoxMovies.getItems().add(movie.getName());
            })).start();

            (new Thread(() -> {
                ArrayList<Movie> movieArrayList1 = databaseHelper.getAvailableMovies();
                for (Movie movie : movieArrayList1)
                    stringMovieHashMap.put(movie.getName(), movie);
            })).start();

        })).start();

        addArtistsToComboBox();

        btnAddNewArtist.setOnAction(e -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AddArtistWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));
                AddArtistWindow.setArtistAddedCallBack(this);
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btnSaveSong.setOnAction(e -> {
            if (txtFieldSongName.getText().length() == 0) {
                Utils.showError("Fill required fields");
                return;
            }

            if (comboBoxMovies.getValue().length() == 0) {
                Utils.showError("Fill required fields");
                return;
            }

            if (comboBoxArtist1.getValue() == null) {
                Utils.showError("At least one artist is necessary");
                return;
            }

            Movie movie = stringMovieHashMap.get(comboBoxMovies.getValue());

            if (movie == null) {
                Utils.showError("No such movie");
                return;
            }

            String artist1 = comboBoxArtist1.getValue();
            String artist2 = comboBoxArtist2.getValue();
            String artist3 = comboBoxArtist3.getValue();
            String artist4 = comboBoxArtist4.getValue();


            if (!doesArtistExist(artist1) || !doesArtistExist(artist2) || !doesArtistExist(artist3) || !doesArtistExist(artist4)) {
                Utils.showError("Artist(s) does not exist");
                return;
            }

            ArrayList<Artist> currentArtists = new ArrayList<>();

            if (stringArtistHashMap.containsKey(artist1))
                currentArtists.add(stringArtistHashMap.get(artist1));

            if (stringArtistHashMap.containsKey(artist2))
                currentArtists.add(stringArtistHashMap.get(artist2));

            if (stringArtistHashMap.containsKey(artist3))
                currentArtists.add(stringArtistHashMap.get(artist3));

            if (stringArtistHashMap.containsKey(artist4))
                currentArtists.add(stringArtistHashMap.get(artist4));

            Song song = new Song(txtFieldSongName.getText(), movie.getId(), currentArtists);

            databaseHelper.addSong(song);

            txtFieldSongName.clear();
        });
    }

    private boolean doesArtistExist(String artist1) {
        if (artist1 == null)
            return true;

        return stringArtistHashMap.containsKey(artist1);
    }

    @Override
    public void artistAdded() {
        addArtistsToComboBox();
    }

    public void addArtistsToComboBox() {
        comboBoxArtist1.getItems().clear();
        comboBoxArtist2.getItems().clear();
        comboBoxArtist3.getItems().clear();
        comboBoxArtist3.getItems().clear();

        stringArtistHashMap.clear();

        artists = databaseHelper.getAvailableArtists();

        for (Artist artist : artists) {
            comboBoxArtist1.getItems().add(artist.getName());
            comboBoxArtist2.getItems().add(artist.getName());
            comboBoxArtist3.getItems().add(artist.getName());
            comboBoxArtist4.getItems().add(artist.getName());
            stringArtistHashMap.put(artist.getName(), artist);
        }
    }
}

interface ArtistAdddedCallBack {
    void artistAdded();
}

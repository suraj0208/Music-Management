import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class MovieSongsSearchResultsWindow extends Application implements Initializable, MovieEditedCallBack {

    @FXML
    private Label lblMovieName;

    @FXML
    private Label lblMovieLanguage;

    @FXML
    private Label lblMovieYear;

    @FXML
    private Label lblMovieRecordNumber;

    @FXML
    private TableView<Song> tblSearchResults;

    @FXML
    private Button btnEditMovie;

    private static ArrayList<Song> songs;

    private static Movie movie;

    public static ArrayList<Song> getSongs() {
        return songs;
    }

    public static void setSongs(ArrayList<Song> songs) {
        MovieSongsSearchResultsWindow.songs = songs;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MovieSongsSearchResultsWindow.fxml"));
        primaryStage.setTitle("Search Results");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        movie = songs.get(0).getMovie();
        setMovieDetails();
        setSongsTable();
        AddMovieWindow.setMovieEditedCallBack(this);

        btnEditMovie.setOnAction(e -> {
            AddMovieWindow.setMovie(songs.get(0).getMovie());
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AddMovieWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 300));
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void setSongsTable() {
        TableColumn<Song, String> tableColumnSongName = new TableColumn<>("Song Name");

        tableColumnSongName.setMinWidth(200);
        tableColumnSongName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Song, String> tableColumnArtists = new TableColumn<>("Artists");
        tableColumnArtists.setMinWidth(300);
        tableColumnArtists.setCellValueFactory(new PropertyValueFactory<>("artists"));

        tableColumnArtists.setCellValueFactory(param -> {
            ArrayList<Artist> artists = param.getValue().getArtists();

            StringBuilder stringBuffer = new StringBuilder();

            for (int i = 0; i < artists.size(); i++) {
                stringBuffer.append(artists.get(i).getName());

                if (i != artists.size() - 1) {
                    stringBuffer.append(", ");
                }
            }

            return new SimpleStringProperty(stringBuffer.toString());
        });


        ObservableList<Song> songObservableList = FXCollections.observableArrayList();

        songObservableList.addAll(songs);

        tblSearchResults.getColumns().add(tableColumnSongName);
        tblSearchResults.getColumns().add(tableColumnArtists);

        tblSearchResults.setItems(songObservableList);

    }

    private void setMovieDetails() {
        lblMovieName.setText(movie.getName());
        lblMovieLanguage.setText("Language: " + movie.getLanguage());
        lblMovieYear.setText("Year: " + movie.getYear());
        lblMovieRecordNumber.setText("Record No: " + movie.getRecordNo());
    }

    @Override
    public void movieEdited() {
        if (movie == null) {
            closeWindow();
            return;
        }

        setMovieDetails();
    }

    private void closeWindow() {
        ((Stage) (btnEditMovie.getScene()).getWindow()).close();
    }


    public static void setMovie(Movie movie) {
        MovieSongsSearchResultsWindow.movie = movie;
    }
}

interface MovieEditedCallBack {
    void movieEdited();
}

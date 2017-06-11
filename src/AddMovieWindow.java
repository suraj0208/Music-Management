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
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class AddMovieWindow extends Application implements Initializable, LanguageAddedCallBack {
    @FXML
    TextField txtFieldMovieName;

    @FXML
    TextField txtFieldMovieYear;

    @FXML
    ComboBox<String> comboMovieLanguage;

    @FXML
    Button btnAddNewLanguage;

    @FXML
    TextField txtFieldRecordNo;

    @FXML
    Button btnSaveMovie;


    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private ArrayList<Language> languageArrayList;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AddMovieWindow.fxml"));
        primaryStage.setTitle("Add Movie");
        primaryStage.setScene(new Scene(root, 520, 300));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnAddNewLanguage.setOnAction(e -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("AddLanguageWindow.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 500, 200));

                AddLanguageWindow.setLanguageAddedCallBack(this);

                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        addLanguagesToComboBox();

        btnSaveMovie.setOnAction(e -> {
            try {

                if (txtFieldMovieName.getText().length() == 0 || txtFieldMovieYear.getText().length() == 0 || txtFieldRecordNo.getText().length() == 0) {
                    Utils.showError("Fill required fields");
                    return;
                }


                String movieName = txtFieldMovieName.getText();
                int movieYear = Integer.parseInt(txtFieldMovieYear.getText());
                int movieRecordNo = Integer.parseInt(txtFieldRecordNo.getText());

                if (languageArrayList.size() == 0) {
                    Utils.showError("Add a language first");
                    return;
                }

                Language selectedLanguage = languageArrayList.get(0);

                for (Language language : languageArrayList)
                    if (language.getName().equals(comboMovieLanguage.getValue())) {
                        selectedLanguage = language;
                        break;
                    }

                Movie movie = new Movie(movieName, movieYear, selectedLanguage.getId(), movieRecordNo);
                databaseHelper.addMovie(movie);

                txtFieldMovieName.clear();
                txtFieldMovieYear.clear();
                txtFieldRecordNo.clear();

                Utils.showInfo("Movie added");

            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.showError("Invalid Input");
            }

        });

    }

    private void addLanguagesToComboBox() {
        languageArrayList = databaseHelper.getAvailableLanguages();

        if (languageArrayList == null) {
            Utils.showError("Error occurred while getting language data");
            return;
        }

        comboMovieLanguage.getItems().clear();

        for (Language language : languageArrayList)
            comboMovieLanguage.getItems().add(language.getName());

        if (languageArrayList.size() > 0)
            comboMovieLanguage.setPromptText(languageArrayList.get(0).getName());

    }

    @Override
    public void languageAdded() {
        addLanguagesToComboBox();
    }
}

interface LanguageAddedCallBack{
    void languageAdded();
}

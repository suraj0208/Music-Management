package com.suraj.musicmanagement.controllers;

import com.suraj.musicmanagement.DatabaseHelper;
import com.suraj.musicmanagement.Utils;
import com.suraj.musicmanagement.data.Language;
import com.suraj.musicmanagement.data.Movie;
import com.suraj.musicmanagement.interfaces.LanguageAddedCallBack;
import com.suraj.musicmanagement.interfaces.MovieEditedCallBack;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Created by suraj on 8/6/17.
 */
public class AddMovieWindow extends Application implements Initializable, LanguageAddedCallBack {
    @FXML
    private TextField txtFieldMovieName;

    @FXML
    private TextField txtFieldMovieYear;

    @FXML
    private ComboBox<String> comboMovieLanguage;

    @FXML
    private Button btnAddNewLanguage;

    @FXML
    private TextField txtFieldRecordNo;

    @FXML
    private Button btnSaveMovie;

    @FXML
    private Button btnDeleteMovie;

    @FXML
    private Label lblAddMovieWindowTitle;

    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private ArrayList<Language> languageArrayList;

    private static Movie movie;

    private static HashSet<MovieEditedCallBack> movieEditedCallBacks;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/suraj/musicmanagement/ui/AddMovieWindow.fxml"));
        primaryStage.setTitle("Add Movie");
        primaryStage.setScene(new Scene(root, 520, 300));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtFieldRecordNo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSaveMovie.fire();
        });

        btnSaveMovie.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnSaveMovie.fire();
        });

        btnDeleteMovie.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                btnDeleteMovie.fire();
        });

        btnAddNewLanguage.setOnAction(e -> {
            Utils.openWindow(getClass().getClassLoader().getResource("com/suraj/musicmanagement/ui/AddLanguageWindow.fxml"), 500, 200);
            AddLanguageWindow.setLanguageAddedCallBack(this);

        });

        addLanguagesToComboBox();

        if (movie != null) {
            final Movie originalMovie = movie;

            txtFieldMovieName.setText(movie.getName());
            txtFieldMovieYear.setText("" + movie.getYear());
            txtFieldRecordNo.setText("" + movie.getRecordNo());
            comboMovieLanguage.setValue(movie.getLanguage().getName());
            lblAddMovieWindowTitle.setText("Edit Movie Details");

            btnDeleteMovie.setOnAction(e -> {
                if (!Utils.confirmDialog("Do you want to delete this movie and all its songs?"))
                    return;

                if (databaseHelper.deleteMovie(movie.getId())) {
                    Utils.showInfo("Movie Deleted");

                    for (MovieEditedCallBack movieEditedCallBack : movieEditedCallBacks)
                        movieEditedCallBack.movieEdited(originalMovie, null);

                    closeWindow();
                } else {
                    Utils.showError("Error Occurred");
                }
            });

            btnSaveMovie.setOnAction(e -> {
                if (!prepareMovie())
                    return;

                if (!Utils.confirmDialog("Do you want to save the changes?"))
                    return;

                if (databaseHelper.updateMovie(movie)) {
                    Utils.showInfo("Movie Saved");
                    closeWindow();

                    if (movieEditedCallBacks != null)
                        for (MovieEditedCallBack movieEditedCallBack : movieEditedCallBacks)
                            if (movieEditedCallBack != null)
                                movieEditedCallBack.movieEdited(originalMovie, movie);
                }
            });

            return;
        }

        btnDeleteMovie.setVisible(false);

        btnSaveMovie.setOnAction(e -> {
            try {
                if (!prepareMovie())
                    return;

                databaseHelper.addMovie(movie);
                clearFields();

                Utils.showInfo("Movie Saved");

                for (MovieEditedCallBack movieEditedCallBack : movieEditedCallBacks)
                    if (movieEditedCallBack != null)
                        movieEditedCallBack.movieEdited(null, movie);

                closeWindow();
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.showError("Invalid Input");
            }

        });

    }

    private void closeWindow() {
        ((Stage) (btnSaveMovie.getScene()).getWindow()).close();
    }

    public static Movie getMovie() {
        return movie;
    }

    public static void setMovie(Movie movie) {
        AddMovieWindow.movie = movie;
    }

    private void clearFields() {
        txtFieldMovieName.clear();
        txtFieldMovieYear.clear();
        txtFieldRecordNo.clear();
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

    private boolean prepareMovie() {
        if (txtFieldMovieName.getText().length() == 0 || txtFieldMovieYear.getText().length() == 0 || txtFieldRecordNo.getText().length() == 0) {
            Utils.showError("Fill required fields");
            return false;
        }

        String movieName = txtFieldMovieName.getText();
        int movieYear = Integer.parseInt(txtFieldMovieYear.getText());
        int movieRecordNo = Integer.parseInt(txtFieldRecordNo.getText());

        if (languageArrayList.size() == 0) {
            Utils.showError("Add a language first");
            return false;
        }
        Language selectedLanguage = languageArrayList.get(0);

        for (Language language : languageArrayList)
            if (language.getName().equals(comboMovieLanguage.getSelectionModel().getSelectedItem())) {
                selectedLanguage = language;
                break;
            }

        if (movie != null) {
            movie = new Movie(movie.getId(), movieName, movieYear,selectedLanguage, movieRecordNo);
            return true;
        }

        movie = new Movie(movieName, movieYear,selectedLanguage, movieRecordNo);

        return true;
    }

    public static void addMovieEditedCallBack(MovieEditedCallBack movieEditedCallBack) {
        if (movieEditedCallBacks == null)
            movieEditedCallBacks = new HashSet<>();

        AddMovieWindow.movieEditedCallBacks.add(movieEditedCallBack);
    }

    public static void removeMovieEditedCallBack(MovieEditedCallBack movieEditedCallBack) {
        if (movieEditedCallBacks == null)
            return;

        movieEditedCallBacks.remove(movieEditedCallBack);
    }

}


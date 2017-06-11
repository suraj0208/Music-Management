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
public class AddLanguageWindow extends Application implements Initializable {
    @FXML
    private Button btnSaveLanguage;

    @FXML
    private TextField txtFieldLanguageName;

    private static LanguageAddedCallBack languageAddedCallBack;

    public static void setLanguageAddedCallBack(LanguageAddedCallBack languageAddedCallBack) {
        AddLanguageWindow.languageAddedCallBack = languageAddedCallBack;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AddLanguageWindow.fxml"));
        primaryStage.setTitle("Add Language");
        primaryStage.setScene(new Scene(root, 500, 200));
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSaveLanguage.setOnAction(event -> {
            if(txtFieldLanguageName.getText().length()==0){
                Utils.showError("Invalid Input");
                return;
            }

            Language language = new Language(txtFieldLanguageName.getText());
            new DatabaseHelper().addLanguage(language);

            if(languageAddedCallBack!=null)
                languageAddedCallBack.languageAdded();

            ((Stage)(btnSaveLanguage.getScene()).getWindow()).close();

        });
    }
}

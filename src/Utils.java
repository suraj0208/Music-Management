import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
}

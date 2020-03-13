package alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AlertDialog {

    public void showSuccessDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/icons/plus(1).png"));
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));
        alert.show();
    }

    public void showFailureDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/icons/error.png"));
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));
        alert.show();
    }

    public Alert showConfirmationDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/icons/question.png"));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES,ButtonType.NO);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));

        return alert;
    }

}

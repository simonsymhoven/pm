package alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AlertDialog {

    public void showSuccessDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/img/icons8-ausgefüllte-checkbox-100.png"));
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));
        alert.show();
    }

    public void showFailureDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/img/icons8-löschen-50.png"));
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));
        alert.show();
    }

    public Alert showConfirmationDialog(String header, String message){
        Image img = new Image(AlertDialog.class.getResourceAsStream("/img/icons8-fragezeichen-100.png"));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES,ButtonType.NO);
        alert.setHeaderText(header);
        alert.setGraphic(new ImageView(img));

        return alert;
    }

}

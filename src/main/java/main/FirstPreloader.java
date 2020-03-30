package main;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class FirstPreloader extends Preloader {
    private Stage stage;

    private Scene createPreloaderScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/views/preloader.fxml"));
        return new Scene(root);
    }

    public void start(Stage stage) throws IOException {
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(createPreloaderScene());
        stage.show();
    }


    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}

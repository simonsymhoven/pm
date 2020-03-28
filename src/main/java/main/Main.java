package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sql.DatabaseFactoryUtils;

public class Main extends Application {
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseFactoryUtils.getSessionFactory().openSession();

        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        stage.setTitle("Portfolio Management");
        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });

        stage.setScene(new Scene(root));
        stage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }
}

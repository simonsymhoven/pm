package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sql.DatabaseFactory;

public class Main extends Application {
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseFactory.getSessionFactory().openSession();

        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        //root.getStylesheets().add(getClass().getResource("/fullpackstyling.css").toExternalForm());
        stage.setTitle("Portfolio Management");
        stage.initStyle(StageStyle.UNDECORATED);

        //grab your root here
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        //move around here
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

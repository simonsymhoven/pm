package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavBarController implements Initializable {
    double x,y = 0;
    @FXML
    private BorderPane navBarPane;

    @FXML
    public Label userName;

    @FXML
    public Circle profile;

    @FXML
    private void handleShowView(ActionEvent e) {
        String view = (String) ((Node) e.getSource()).getUserData();
        loadFXML(getClass().getResource(view));
    }

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            navBarPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userName.setText(LoginController.loggedinUser.vorname + ", " + LoginController.loggedinUser.name);
        userName.setAlignment(Pos.BASELINE_RIGHT);

        byte[] bytes = LoginController.loggedinUser.getImage();
        Image img = new Image(new ByteArrayInputStream(bytes));
        ImagePattern pattern = new ImagePattern(img);
        profile.setFill(pattern);

    }

    @FXML
    private void logout() throws IOException {
        Stage oldStage = (Stage) navBarPane.getScene().getWindow();
        oldStage.hide();

        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Stage stage = new Stage();

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


        stage.setTitle("Portfolio Management");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.show();

    }
}

package controllers;

import com.jfoenix.controls.JFXButton;
import controllers.login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j2;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class NavBarController implements Initializable {
    @FXML
    private JFXButton dashboardButton;
    @FXML
    private BorderPane navBarPane;
    @FXML
    private Circle profile;
    private double x = 0;
    private double y = 0;

    private JFXButton oldButton;

    public void handleShowView(ActionEvent e) {
        if (oldButton != null) {
            oldButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            dashboardButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        JFXButton button = (JFXButton) e.getSource();
        button.setContentDisplay(ContentDisplay.TOP);
        oldButton = button;
        String view = (String) button.getUserData();
        loadFXML(getClass().getResource(view));
    }

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            navBarPane.setCenter(loader.load());
        } catch (IOException e) {
           log.error(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        byte[] bytes = LoginController.getLoggedUser().getImage();
        Image img = new Image(new ByteArrayInputStream(bytes));
        ImagePattern pattern = new ImagePattern(img);
        profile.setFill(pattern);

    }

    public void logout() throws IOException {
        Stage oldStage = (Stage) navBarPane.getScene().getWindow();
        oldStage.hide();

        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Stage stage = new Stage();

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

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

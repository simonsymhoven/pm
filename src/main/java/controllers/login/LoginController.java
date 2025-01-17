package controllers.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entities.user.User;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import json.JSONReader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import sql.EntityUserImpl;
import sql.DatabaseFactoryUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class LoginController implements Initializable {
    @FXML
    @Getter
    private AnchorPane pane;
    @FXML
    private JFXButton registration;
    @FXML
    @Getter
    private JFXTextField userName;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton close;
    @FXML
    private Label messageLabel;
    @FXML
    private JFXCheckBox checkBox;
    @Getter
    private static User loggedUser;
    private double x = 0;
    private double y = 0;
    private EntityUserImpl entityUser = new EntityUserImpl();
    private JSONReader jsonReader = new JSONReader();

    private void validate() {
        ObservableList<String> styleClassUserName = userName.getStyleClass();
        ObservableList<String> styleClassPassword = password.getStyleClass();
        String username = userName.getText();
        String pass = password.getText();
        User user = entityUser.get(username);

        if (user != null) {
            if (BCrypt.checkpw(pass, user.getHash())) {
                loggedUser = user;
                if (checkBox.isSelected()) {
                    try {
                        jsonReader.write("user", username);
                    } catch (IOException e) {
                        log.error(e);
                    }
                }

                loadNewStage();
            } else {
                if (username.trim().length() == 0 || pass.trim().length() == 0) {
                    if (!styleClassUserName.contains("error")) {
                        styleClassUserName.add("error");
                    }
                    if (!styleClassPassword.contains("error")) {
                        styleClassPassword.add("error");
                    }
                    messageLabel.setText("Benutzername und Passwort müssen ausgefüllt sein!");
                } else if (!username.equals(user.getUserName()) || !BCrypt.checkpw(pass, user.getHash())) {
                    if (!styleClassUserName.contains("error")) {
                        styleClassUserName.add("error");
                    }
                    if (!styleClassPassword.contains("error")) {
                        styleClassPassword.add("error");
                    }
                    messageLabel.setText("Benutzername oder Passwort ist inkorrekt!");
                }
            }
        } else {
            messageLabel.setText("Benutzername konnte nicht gefunden werden!");
            if (!styleClassUserName.contains("error")) {
                styleClassUserName.add("error");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!entityUser.getAll().isEmpty()) {
            userName.setText(jsonReader.read("user").toString());
        }
        checkBox.setSelected(true);
        close.setOnMouseClicked(e -> {
            DatabaseFactoryUtils.shutdown();
            Platform.exit();
        });

        password.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                validate();
            }
        });
    }

    private void loadNewStage() {
        Stage oldStage = (Stage) registration.getScene().getWindow();
        oldStage.hide();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
            Stage stage = new Stage();
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            });
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/app/app-icon.png")));
            stage.setTitle("Portfolio Management");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void registrate() {
        Stage dialog = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/registration.fxml"));
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                dialog.setX(event.getScreenX() - x);
                dialog.setY(event.getScreenY() - y);
            });
            dialog.setScene(new Scene(root));
            dialog.initOwner(registration.getScene().getWindow());
            dialog.setUserData(this);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
        } catch (IOException ex) {
            log.error(ex);
        }
    }
}

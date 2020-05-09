package controllers.login.registration;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import snackbar.SnackBar;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controllers.login.LoginController;
import entities.user.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import sql.EntityUserImpl;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class RegistrationController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField forename;
    @FXML
    private JFXButton close;
    @FXML
    private JFXTextField surname;
    private Stage stage;
    private RegistrationModel registrationModel;
    private LoginController loginController;
    private EntityUserImpl entityUser;

    public RegistrationController() {
        this.registrationModel = new RegistrationModel();
        this.entityUser = new EntityUserImpl();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) password.getScene().getWindow();
            this.loginController = (LoginController) stage.getUserData();
        });
        close.setOnMouseClicked(e -> stage.close());

        addTextProperty();
        addFocusProperty();

        password.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                registrate();
            }
        });
    }

    private void addFocusProperty() {
        forename.focusedProperty().addListener((observable, old, newValue) -> {
            if (!newValue && registrationModel.getForename() != null && registrationModel.getSurname() != null) {
                searchForProfilePicture();
            }
        });
        surname.focusedProperty().addListener((observable, old, newValue) -> {
            if (!newValue && registrationModel.getForename() != null && registrationModel.getSurname() != null) {
                searchForProfilePicture();
            }
        });
    }

    private void addTextProperty() {
        surname.textProperty().addListener((observable, old, newValue) -> {
            if (!"".equals(newValue)) {
                registrationModel.setSurname(newValue);
            }
        });
        forename.textProperty().addListener((observable, old, newValue) -> {
            if (!"".equals(newValue)) {
                registrationModel.setForename(newValue);
            }
        });
        username.textProperty().addListener((observable, old, newValue) -> {
            if (!"".equals(newValue)) {
                registrationModel.setUsername(newValue);
            }
        });
        password.textProperty().addListener((observable, old, newValue) -> {
            if (!"".equals(newValue)) {
                registrationModel.setPassword(newValue);
            }
        });
    }

    private void searchForProfilePicture() {
        HTTPImage task = new HTTPImage(registrationModel);

        task.setOnSucceeded(succeededEvent -> {

        });

        task.setOnFailed(failedEvent -> {
            log.error(" TASK FAILED! ");
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    public void registrate() {
        Platform.runLater(() -> {
            username.setStyle("-jfx-focus-color: #343f4a;\n"
                    + " -jfx-unfocus-color: #4d4d4d;\n"
                    + " -jfx-background-color: -fx-text-box-border, -fx-background ;\n"
                    + " -jfx-background-insets: 0, 0 0 1 0 ;\n"
                    + " -jfx-background-radius: 0 ;");
            registrationModel.setHash(BCrypt.hashpw(registrationModel.getPassword(), BCrypt.gensalt()));
            if (entityUser.get(registrationModel.getUsername()) == null) {
                User user = new User(
                        registrationModel.getSurname(),
                        registrationModel.getForename(),
                        registrationModel.getUsername(),
                        registrationModel.getHash(),
                        registrationModel.getImage()
                );

                if (entityUser.add(user)) {
                    loginController.getUserName().setText(user.getUserName());
                    SnackBar snackbar = new SnackBar(loginController.getPane());
                    stage.close();
                    snackbar.show("Nutzer wurde erfolgreich hinzugefügt!");
                }
            } else {
                username.clear();
                password.clear();
                SnackBar snackbar = new SnackBar(pane);
                snackbar.show("Nutzername bereits vorhanden!");
            }
        });
    }

    public void backtologin() {
        stage.close();
    }
}

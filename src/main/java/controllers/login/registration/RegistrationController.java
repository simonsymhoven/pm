package controllers.login.registration;

import snackbar.SnackBar;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controllers.login.LoginController;
import entities.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.EmailValidator;
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
    private ProgressIndicator progressIndicator;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private ImageView profilePicture;
    @FXML
    private JFXTextField forename;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton registration;
    @FXML
    private JFXTextField surname;
    private Stage stage;
    private RegistrationModel registrationModel;
    private LoginController loginController;
    private EntityUserImpl entityUser;
    private boolean picturefound = false;

    public RegistrationController() {
        this.registrationModel = new RegistrationModel();
        this.entityUser = new EntityUserImpl();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) registration.getScene().getWindow();
            this.loginController = (LoginController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> stage.close());
        surname.textProperty().addListener((observable, old, newValue) -> {
            if (!newValue.equals("")) {
                registrationModel.setSurname(newValue);
            }
        });
        forename.textProperty().addListener((observable, old, newValue) -> {
            if (!newValue.equals("")) {
                registrationModel.setForename(newValue);
            }
        });
        username.textProperty().addListener((observable, old, newValue) -> {
            if (!newValue.equals("")) {
                registrationModel.setUsername(newValue);
            }
        });
        password.textProperty().addListener((observable, old, newValue) -> {
            if (!newValue.equals("")) {
                registrationModel.setPassword(newValue);
            }
        });


        forename.focusedProperty().addListener((observable, old, newValue) -> {
            if (!newValue) {
                if (registrationModel.getForename() != null && registrationModel.getSurname() != null) {
                    searchForProfilePicture();
                    picturefound = true;
                }
            }
        });

        surname.focusedProperty().addListener((observable, old, newValue) -> {
            if (!newValue) {
                if (registrationModel.getForename() != null && registrationModel.getSurname() != null) {
                    searchForProfilePicture();
                    picturefound = true;
                }
            }
        });


        registration.disableProperty().bind(forename.textProperty().isEmpty()
                .or(surname.textProperty().isEmpty())
                .or(username.textProperty().isEmpty())
                .or(password.textProperty().isEmpty()));
    }

    private void searchForProfilePicture() {
        HTTPImage task = new HTTPImage(registrationModel);

        task.setOnRunning(successesEvent -> {
            profilePicture.setImage(null);
            progressIndicator.setVisible(true);
        });

        task.setOnSucceeded(succeededEvent -> {
            try {
                if (task.get() != null) {
                    byte[] data = task.get();
                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    BufferedImage bImage = ImageIO.read(bis);
                    profilePicture.setImage(SwingFXUtils.toFXImage(bImage, null));
                }
            } catch (Exception e) {
                log.error(e);
            }
            progressIndicator.setVisible(false);
        });

        task.setOnFailed(failedEvent -> {
            progressIndicator.setVisible(false);
            log.error(" TASK FAILED! ");
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    public void registrate() {
        Platform.runLater(() -> {
            if (EmailValidator.getInstance().isValid(registrationModel.getUsername())) {
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
            } else {
                username.setStyle("-jfx-text-box-border: #ff4646 ;\n"
                        + " -jfx-unfocus-color: #ff4646;\n"
                        + " -jfx-focus-color: #ff4646;");
                log.error("Not a valid email addess: " + registrationModel.getUsername());
            }
        });
    }
}

package controllers.client.client_addModal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import entities.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import snackbar.SnackBar;
import sql.EntityClientImpl;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientAddModalController implements Initializable {

    @FXML
    private JFXButton addClient;
    @FXML
    private JFXButton close;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField strategy;
    @FXML
    private AnchorPane pane;

    private Stage stage;
    private ClientController clientController;
    private ClientAddModalModel clientAddModalModel;
    private EntityClientImpl entityClient;

    public ClientAddModalController() {
        this.clientAddModalModel = new ClientAddModalModel();
        this.entityClient = new EntityClientImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) addClient.getScene().getWindow();
            clientController = (ClientController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> {
            clientController.getLabel().setText("Übersicht");
            stage.close();
        });

        addClient.disableProperty().bind(name.textProperty().isEmpty().or(
                        symbol.textProperty().isEmpty().or(
                                strategy.textProperty().isEmpty())
        ));

        strategy.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^(?=.*[0-9])\\d{0,2}(?:\\.\\d{0,2})?$")) {
                strategy.setText(oldValue);
            }
        });
    }

    public void add() {
        clientAddModalModel.setClient(
                new Client(
                        name.getText(),
                        symbol.getText(),
                        Double.parseDouble(strategy.getText().replace(" %", "")),
                        new BigDecimal(0)
                )
        );

        if (entityClient.add(clientAddModalModel.getClient())) {
            clientController.getClients();
            clientController.getComboBox().getSelectionModel().select(clientAddModalModel.getClient());
            SnackBar snackBar = new SnackBar(clientController.getPane());
            snackBar.show("Client wurde erfolgreich hinzugefügt!");
            stage.close();
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Client konnte nicht hinzugefügt werden!");
        }
    }
}

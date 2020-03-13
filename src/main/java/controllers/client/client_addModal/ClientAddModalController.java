package controllers.client.client_addModal;


import alert.AlertDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import entities.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sql.EntityClientImpl;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientAddModalController implements Initializable {
    @FXML
    public JFXButton addClient;
    @FXML
    public JFXButton close;
    @FXML
    public JFXTextField name;
    @FXML
    public JFXTextField symbol;
    @FXML
    public JFXTextField strategy;

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
            clientController.label.setText("Übersicht");
            stage.close();
        });

        addClient.disableProperty().bind(name.textProperty().isEmpty().or(
                        symbol.textProperty().isEmpty().or(
                                strategy.textProperty().isEmpty())
        ));


        strategy.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                strategy.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    @FXML
    public void addClient() {
        clientAddModalModel.setClient(
                new Client(
                        name.getText(),
                        symbol.getText(),
                        Integer.parseInt(strategy.getText()),
                        new BigDecimal(0)
                )
        );

        if (entityClient.add(clientAddModalModel.getClient())) {
            Alert alert = new AlertDialog().showSuccessDialog("Erledigt!", "Client wurde hinzugefügt.");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                clientController.getClients();
                clientController.comboBox.getSelectionModel().select(clientAddModalModel.getClient());
                stage.close();
            }
        } else {
            new AlertDialog().showFailureDialog("Uuuups!", "Client konnte nicht hinzugefügt werden.");
        }
    }
}

package controllers.client.client_addModal;


import alert.AlertDialog;
import controllers.client.ClientController;
import entities.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sql.EntityClientImpl;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientAddModalController implements Initializable {
    @FXML
    public Button addClient;
    @FXML
    public Label lblClose;
    @FXML
    public TextField name;
    @FXML
    public TextField symbol;
    @FXML
    public TextField strategy;


    private ClientAddModalModel clientAddModalModel;
    private EntityClientImpl entityClient;

    public ClientAddModalController() {
        this.clientAddModalModel = new ClientAddModalModel();
        this.entityClient = new EntityClientImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClose.setOnMouseClicked(e -> {
            Stage stage = (Stage) addClient.getScene().getWindow();
            ClientController clientController = (ClientController) stage.getUserData();
            clientController.label.setText("Übersicht");
            clientController.getClients();
            clientController.comboBox.getSelectionModel().select(clientAddModalModel.getClient());
            stage.close();
        });

        addClient.disableProperty().bind(name.textProperty().isEmpty().or(
                        symbol.textProperty().isEmpty().or(
                                strategy.textProperty().isEmpty())
        ));

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
            new AlertDialog().showSuccessDialog("Erledigt!", "Client wurde hinzugefügt.");

            name.clear();
            symbol.clear();
            strategy.clear();
        } else {
            new AlertDialog().showFailureDialog("Uuuups!", "Client konnte nicht hinzugefügt werden.");
        }
    }
}

package controllers.client.client_auditModal;


import controllers.client.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sql.EntityClientImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientAuditModalController implements Initializable {
    @FXML
    public Label lblClose;
    @FXML
    public AnchorPane pane;

    private ClientAuditModalModel clientAuditModalModel;
    private EntityClientImpl entityClient;
    private ClientController clientController;
    private Stage stage;


    public ClientAuditModalController() {
        this.entityClient = new EntityClientImpl();
        this.clientAuditModalModel = new ClientAuditModalModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            clientController = (ClientController) stage.getUserData();

            clientAuditModalModel.setRevisions(
                    new ArrayList<>(
                            entityClient.getAudit(clientController.getClientModel().getClient())
                    )
            );
        });


        lblClose.setOnMouseClicked(e -> {
            clientController.label.setText("Übersicht");
            clientController.getClients();
            stage.close();
        });


    }
}

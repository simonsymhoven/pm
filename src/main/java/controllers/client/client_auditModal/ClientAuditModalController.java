package controllers.client.client_auditModal;

import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.client.ClientController;
import entities.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.envers.RevisionType;
import sql.EntityClientImpl;
import sql.EntityPortfolioStockImpl;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private ClientAuditModalModel clientAuditModalModel;
    private EntityClientImpl entityClient;
    private EntityPortfolioStockImpl entityPortfolio;
    private ClientController clientController;
    private Stage stage;

    public ClientAuditModalController() {
        this.entityClient = new EntityClientImpl();
        this.entityPortfolio = new EntityPortfolioStockImpl();
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

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            clientAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(),
                        clientToString(revision.getClient(), revision.getRevisionType()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            clientController.getLabel().setText("Übersicht");
            stage.close();
        });


    }

    private String clientToString(Client client, RevisionType revisionType) {
        if (revisionType.equals(RevisionType.ADD)) {
            return client.getName() + " [" + client.getSymbol() + "] wurde hinzugefügt.";
        } else {
            return "Depotwert = " + NumberFormat.getCurrencyInstance().format(client.getDepoValue())
                    .replace("EUR", "EUR ") + ", Aktien: " + entityPortfolio.getAll(client).size() + " Stk.";
        }
    }
}

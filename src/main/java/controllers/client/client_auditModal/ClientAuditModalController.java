package controllers.client.client_auditModal;


import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.client.ClientController;
import entities.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sql.EntityClientImpl;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientAuditModalController implements Initializable {
    @FXML
    public JFXButton close;
    @FXML
    public AnchorPane pane;
    @FXML
    public ScrollPane scrollPane;

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

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            clientAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(), clientToString(revision.getClient()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            clientController.label.setText("Übersicht");
            stage.close();
        });


    }

    private String clientToString(Client client) {
        return client.getName() + " [" + client.getSymbol() + "]:"
                + " Strategie: " + client.getStrategy() + "%, Depowert: " + NumberFormat.getCurrencyInstance()
                .format(client.getDepoValue()).replace("EUR", "EUR ");


    }
}

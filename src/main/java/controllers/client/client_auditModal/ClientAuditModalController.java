package controllers.client.client_auditModal;


import com.jfoenix.controls.JFXButton;
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
            clientAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = new HBox();
                hbox.setPrefSize(550, 70);
                hbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                                + "-fx-border-width: 0.5;" + "-fx-border-insets: 5;"
                                + "-fx-border-radius: 5;" + "-fx-border-color: #343f4a;");
                Image img;

                switch(revision.getRevisionType()) {
                    case ADD:
                        img = new Image(getClass().getResourceAsStream("/img/icons8-plus-2-50.png"));
                        break;
                    case DEL:
                        img = new Image(getClass().getResourceAsStream("/img/icons8-löschen-50.png"));
                        break;
                    case MOD:
                        img = new Image(getClass().getResourceAsStream("/img/icons8-viele-bearbeiten-50.png"));
                        break;
                    default:
                        img = new Image(getClass().getResourceAsStream("/img/icons8-fragezeichen-100.png"));
                        break;
                }

                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(70);
                imageView.setFitHeight(70);

                VBox dateChangeBox = new VBox();
                dateChangeBox.setPadding(new Insets(20,5,5,20));
                dateChangeBox.setPrefSize(480,70);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
                HBox date = new HBox();
                date.setPrefSize(480, 35);
                Label lDate = new Label();
                String sdate =  simpleDateFormat.format(revision.getRevisionDate());
                sdate = sdate.replace(" ", ", um ");
                lDate.setText("Am " + sdate + " Uhr wurde folgende Änderung durchgeführt:");
                date.getChildren().add(lDate);

                HBox change = new HBox();
                change.setPrefSize(480, 35);
                Label lChange = new Label();
                lChange.setText(clientToString(revision.getClient()));
                change.getChildren().add(lChange);

                dateChangeBox.getChildren().addAll(date, change);

                hbox.getChildren().addAll(imageView, dateChangeBox);
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

package controllers.client;

import alert.AlertDialog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import entities.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import sql.EntityClientImpl;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Log4j2

public class ClientController implements Initializable {
    @FXML
    @Getter
    private JFXComboBox<Client> comboBox;
    @FXML
    @Getter
    private Label label;
    @FXML
    private JFXButton addClient;
    @FXML
    private JFXButton deleteClient;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField strategy;
    @FXML
    private JFXTextField depoValue;
    @FXML
    private JFXButton showAudit;

    private EntityClientImpl entityClient;
    @Getter
    private ClientModel clientModel;
    private double x = 0;
    private double y = 0;

    public ClientController() {
        Locale.setDefault(Locale.GERMANY);
        this.entityClient = new EntityClientImpl();
        this.clientModel = new ClientModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteClient.disableProperty().bind(comboBox.valueProperty().isNull());
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        getClients();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clientModel.setClient(newValue);
                label.setText(clientModel.getClient().getName());
                name.setText(clientModel.getClient().getName());
                symbol.setText(clientModel.getClient().getSymbol());
                strategy.setText(clientModel.getClient().getStrategy() + " %");
                depoValue.setText(NumberFormat.getCurrencyInstance()
                        .format(clientModel.getClient().getDepoValue())
                        .replace("EUR", "EUR ")
                );
            } else {
                name.clear();
                symbol.clear();
                strategy.clear();
                depoValue.clear();
            }
        });

        addClient.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/client/client_add_modal.fxml"));
                root.setOnMousePressed(event -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    dialog.setX(event.getScreenX() - x);
                    dialog.setY(event.getScreenY() - y);
                });
                dialog.setScene(new Scene(root));
                dialog.initOwner(addClient.getScene().getWindow());
                dialog.setUserData(this);
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.show();
            } catch (IOException ex) {
                log.error(ex);
            }

        });

        deleteClient.setOnMouseClicked(e -> {
            Alert alert = new AlertDialog()
                    .showConfirmationDialog(
                            "Ganz sicher?",
                            "Bist du dir wirklich sicher, dass du diesen Clienten löschen möchtest? \n"
                                    + "Diese Aktion kann nicht widerrufen werden!");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (entityClient.delete(clientModel.getClient())) {
                    comboBox.getItems().remove(clientModel.getClient());
                    label.setText("Übersicht");
                    getClients();
                    comboBox.getSelectionModel().clearSelection();
                }
            }
        });

        showAudit.setOnMouseClicked(e ->  {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/client/client_audit_modal.fxml"));
                root.setOnMousePressed(event -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });
                root.setOnMouseDragged(event -> {
                    dialog.setX(event.getScreenX() - x);
                    dialog.setY(event.getScreenY() - y);
                });
                dialog.setScene(new Scene(root));
                dialog.initOwner(showAudit.getScene().getWindow());
                dialog.setUserData(this);
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.show();
            } catch (IOException ex) {
               log.error(ex);
            }
        });

    }

    public void getClients() {
        clientModel.setClients(entityClient.getAll());
        clientModel.getClients().forEach(c -> {
            if (!comboBox.getItems().contains(c)) {
                comboBox.getItems().add(c);
            }
        });
    }

}

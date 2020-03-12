package controllers.client;

import alert.AlertDialog;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import entities.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import sql.EntityClientImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Log4j2

public class ClientController implements Initializable {

    double x,y = 0;
    @FXML
    public JFXComboBox<Client> comboBox;
    @FXML
    public Label label;
    @FXML
    public Button addClient;
    @FXML
    public Button deleteClient;
    @FXML
    public JFXTextField name;
    @FXML
    public JFXTextField symbol;
    @FXML
    public JFXTextField strategy;
    @FXML
    public JFXTextField depoValue;
    @FXML
    public Button showAudit;


    private EntityClientImpl entityClient;
    @Getter
    public ClientModel clientModel;

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
                label.setText(clientModel.getClient().name);
                name.setText(clientModel.getClient().name);
                symbol.setText(clientModel.getClient().symbol);
                strategy.setText(clientModel.getClient().strategy + " %");
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
    }

    @FXML
    public void addClient() throws IOException {
        Stage dialog = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/client/client_add_modal.fxml"));
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
    }

    @FXML
    public void deleteClient(){
        Alert alert = new AlertDialog()
                .showConfirmationDialog("Ganz sicher?", "Bist du dir wirklich sicher, dass du diesen Clienten löschen möchtest? \n" +
                        "Diese Aktion kann nicht widerrufen werden!");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            entityClient.delete(clientModel.getClient());
            comboBox.getItems().remove(clientModel.getClient());
            label.setText("Übersicht");
            getClients();
            comboBox.getSelectionModel().clearSelection();
        }
    }

    public void getClients(){
        clientModel.setClients(entityClient.getAll());
        clientModel.getClients().forEach(c -> {
            if (!comboBox.getItems().contains(c)) {
                comboBox.getItems().add(c);
            }
        });
    }

    @FXML
    public void getAudit() throws IOException {
        Stage dialog = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/client/client_audit_modal.fxml"));
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
    }
}

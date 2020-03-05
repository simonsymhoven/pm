package controllers.client;

import entities.Client;
import enums.ClientState;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sql.EntityClientImpl;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML
    public ComboBox comboBox;
    @FXML
    public Label label;
    @FXML
    public Button editClient;
    @FXML
    public Button addClient;
    @FXML
    public Button deleteClient;
    @FXML
    public Button save;
    @FXML
    public Button cancel;
    @FXML
    public TextField name;
    @FXML
    public TextField symbol;
    @FXML
    public TextField strategy;
    @FXML
    public TextField depoValue;



    private EntityClientImpl entityClient;
    private ClientModel clientModel;

    public ClientController() {
        Locale.setDefault(Locale.GERMANY);
        this.entityClient = new EntityClientImpl();
        this.clientModel = new ClientModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setDisable(true);
        cancel.setDisable(true);
        deleteClient.setDisable(true);
        editClient.setDisable(true);

        getClients();

        comboBox.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            clientModel.setClient(entityClient.get(newValue));
            name.setText(clientModel.getClient().name);
            symbol.setText(clientModel.getClient().symbol);
            strategy.setText(clientModel.getClient().strategy + " %");
            depoValue.setText(NumberFormat.getCurrencyInstance()
                    .format(clientModel.getClient().getDepoValue())
                    .replace("EUR", "EUR ")
            );

            editClient.setDisable(false);
            deleteClient.setDisable(false);
        });
    }

    @FXML
    public void editClient() {
        label.setText("Client bearbeiten");
        clientModel.setClientState(ClientState.EDIT_CLIENT);
        setEditGUI();

    }

    @FXML
    public void addClient(){
        label.setText("Client hinzufügen");
        clientModel.setClientState(ClientState.ADD_CLIENT);
        setAddGUI();
    }

    @FXML
    public void save(){
        Image img = new Image(getClass().getResourceAsStream("/img/icons8-ausgefüllte-checkbox-100.png"));

        switch(clientModel.getClientState()) {
            case ADD_CLIENT:
                clientModel.setClient(new Client());
                clientModel.getClient().setName(name.getText());
                clientModel.getClient().setSymbol(symbol.getText());
                clientModel.getClient().setStrategy(Integer.parseInt(strategy.getText()));
                clientModel.getClient().setDepoValue(new BigDecimal(depoValue.getText()));

                entityClient.add(clientModel.getClient());

                Alert alertAdd = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Client wurde angelegt.");
                alertAdd.setHeaderText("Erledigt!");
                alertAdd.setGraphic(new ImageView(img));
                alertAdd.show();

                resetGUI();
                break;
            case EDIT_CLIENT:
                clientModel.getClient().setName(name.getText());
                clientModel.getClient().setSymbol(symbol.getText());
                clientModel.getClient().setStrategy(Integer.parseInt(strategy.getText()));
                clientModel.getClient().setDepoValue(new BigDecimal(depoValue.getText()));

                entityClient.update(clientModel.getClient());

                Alert alertEdit = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Client wurde bearbeitet.");
                alertEdit.setHeaderText("Erledigt!");
                alertEdit.setGraphic(new ImageView(img));
                alertEdit.show();

                resetGUI();
                break;
            default:
                break;
        }
    }

    @FXML
    public void deleteClient(){
        Image img = new Image(getClass().getResourceAsStream("/img/icons8-fragezeichen-100.png"));
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Bist du dir wirklich sicher, dass du diesen Clienten löschen möchtest?",
                ButtonType.NO, ButtonType.YES);
        alert.setGraphic(new ImageView(img));
        alert.setHeaderText("Ganz sicher?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            entityClient.delete(clientModel.getClient());
            resetGUI();
        }
    }

    @FXML
    public void cancel(){
        resetGUI();
    }

    private void resetGUI(){
        label.setText("Übersicht");

        comboBox.getItems().clear();
        comboBox.setDisable(false);
        comboBox.setPromptText("Client");

        getClients();

        name.clear();
        symbol.clear();
        depoValue.clear();
        strategy.clear();
        name.setEditable(false);
        symbol.setEditable(false);
        depoValue.setEditable(false);
        strategy.setEditable(false);

        save.setDisable(true);
        cancel.setDisable(true);

        addClient.setDisable(false);
        editClient.setDisable(true);
        deleteClient.setDisable(true);
    }

    private void setAddGUI() {
        editClient.setDisable(true);
        addClient.setDisable(true);
        deleteClient.setDisable(true);
        comboBox.setDisable(true);
        save.setDisable(false);
        cancel.setDisable(false);

        name.setEditable(true);
        name.clear();
        name.setPromptText("Name Client");

        symbol.setEditable(true);
        symbol.clear();
        symbol.setPromptText("Symbol Client");

        strategy.setEditable(true);
        strategy.clear();
        strategy.setPromptText("z.B. 65 (in %)");

        depoValue.setEditable(true);
        depoValue.clear();
        depoValue.setPromptText("Depowert (in €)");
    }

    private void setEditGUI() {
        if (!comboBox.getSelectionModel().isEmpty()) {
            comboBox.setEditable(false);

            save.setDisable(false);
            cancel.setDisable(false);

            name.setEditable(true);
            symbol.setEditable(true);
            strategy.setEditable(true);
            strategy.setText(strategy.getText().replace(" %", ""));

            depoValue.setEditable(true);
            depoValue.setText(depoValue.getText()
                    .substring(0, depoValue.getText().length() - 2)
                    .replace(".", "")
                    .replace(",", ".")
            );

            editClient.setDisable(true);
            addClient.setDisable(true);
            deleteClient.setDisable(true);
        }
    }

    private void getClients() {
        clientModel.setClients(entityClient.getAll());
        clientModel.getClients().forEach(c -> comboBox.getItems().add(c.symbol));
    }
}

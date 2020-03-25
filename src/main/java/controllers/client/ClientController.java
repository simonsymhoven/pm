package controllers.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import entities.client.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityClientImpl;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Log4j2

public class ClientController implements Initializable {
    @FXML
    @Getter
    private AnchorPane pane;
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
    private JFXButton editClient;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField strategyStocksLowerLimit;
    @FXML
    private JFXTextField strategyStocksTargetValue;
    @FXML
    private JFXTextField strategyStocksUpperLimit;
    @FXML
    private JFXTextField strategyAlternativeLowerLimit;
    @FXML
    private JFXTextField strategyAlternativeTargetValue;
    @FXML
    private JFXTextField strategyAlternativeUpperLimit;
    @FXML
    private JFXTextField strategyIoanLowerLimit;
    @FXML
    private JFXTextField strategyIoanTargetValue;
    @FXML
    private JFXTextField strategyIoanUpperLimit;
    @FXML
    private JFXTextField strategyLiquidityLowerLimit;
    @FXML
    private JFXTextField strategyLiquidityTargetValue;
    @FXML
    private JFXTextField strategyLiquidityUpperLimit;
    @FXML
    private JFXTextField depoValue;
    @FXML
    private JFXButton showAudit;
    @FXML
    private PieChart pieChart;
    @FXML
    private TextArea comment;

    private EntityClientImpl entityClient;
    @Getter
    private ClientModel clientModel;
    private double x = 0;
    private double y = 0;
    private ObservableList<PieChart.Data> clientStrategyData;

    public ClientController() {
        this.entityClient = new EntityClientImpl();
        this.clientModel = new ClientModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteClient.disableProperty().bind(comboBox.valueProperty().isNull());
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());
        editClient.disableProperty().bind(comboBox.valueProperty().isNull());

        getClients();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clientModel.setClient(newValue);
                drawChart();
                label.setText(clientModel.getClient().getName());
                name.setText(clientModel.getClient().getName());
                symbol.setText(clientModel.getClient().getSymbol());
                strategyAlternativeLowerLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyAlternativeLowerLimit()) + " %");
                strategyAlternativeTargetValue.setText(String.format("%.2f", clientModel.getClient().getStrategyAlternativeTargetValue()) + " %");
                strategyAlternativeUpperLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyAlternativeUpperLimit()) + " %");
                strategyIoanLowerLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyIoanLowerLimit()) + " %");
                strategyIoanTargetValue.setText(String.format("%.2f", clientModel.getClient().getStrategyIoanTargetValue()) + " %");
                strategyIoanUpperLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyIoanUpperLimit()) + " %");
                strategyStocksLowerLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyStocksLowerLimit()) + " %");
                strategyStocksTargetValue.setText(String.format("%.2f", clientModel.getClient().getStrategyStocksTargetValue()) + " %");
                strategyStocksUpperLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyStocksUpperLimit()) + " %");
                strategyLiquidityLowerLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyLiquidityLowerLimit()) + " %");
                strategyLiquidityTargetValue.setText(String.format("%.2f", clientModel.getClient().getStrategyLiquidityTargetValue()) + " %");
                strategyLiquidityUpperLimit.setText(String.format("%.2f", clientModel.getClient().getStrategyLiquidityUpperLimit()) + " %");

                depoValue.setText(NumberFormat.getCurrencyInstance()
                        .format(clientModel.getClient().getDepoValue())
                        .replace("EUR", "EUR ")
                );

                comment.setText(clientModel.getClient().getComment());
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
            if (entityClient.delete(clientModel.getClient())) {
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Client wurde erfolgreich gelöscht!");
                comboBox.getItems().remove(clientModel.getClient());
                label.setText("Übersicht");
                getClients();
                comboBox.getSelectionModel().clearSelection();
                clear();
            } else {
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Client konnte nicht gelöscht werden!");
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


        editClient.setOnMouseClicked(e ->  {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/client/client_edit_modal.fxml"));
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

    private void drawChart() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            pieChart.setVisible(true);
            clientStrategyData = loadClientStrategyData();
            pieChart.setData(clientStrategyData);
            pieChart.setLabelsVisible(true);
            pieChart.setLegendVisible(false);
        }));

        timeline.setCycleCount(1);
        timeline.play();
    }

    private ObservableList<PieChart.Data> loadClientStrategyData() {
        List<PieChart.Data> list = new ArrayList<>();
        if (clientModel.getClient().getStrategyStocksTargetValue() != 0) {
            list.add(new PieChart.Data("Aktien", clientModel.getClient().getStrategyStocksTargetValue()));
        }
        if (clientModel.getClient().getStrategyAlternativeTargetValue() != 0) {
            list.add(new PieChart.Data("alt. Investments", clientModel.getClient().getStrategyAlternativeTargetValue()));
        }
        if (clientModel.getClient().getStrategyIoanTargetValue() != 0) {
            list.add(new PieChart.Data("Anleihen", clientModel.getClient().getStrategyIoanTargetValue()));
        }
        if (clientModel.getClient().getStrategyLiquidityTargetValue() != 0) {
            list.add(new PieChart.Data("Liquidität", clientModel.getClient().getStrategyLiquidityTargetValue()));
        }

        return FXCollections.observableArrayList(list);
    }

    private void clear() {
        name.clear();
        symbol.clear();
        strategyAlternativeLowerLimit.clear();
        strategyAlternativeTargetValue.clear();
        strategyAlternativeUpperLimit.clear();
        strategyIoanLowerLimit.clear();
        strategyIoanTargetValue.clear();
        strategyIoanUpperLimit.clear();
        strategyStocksLowerLimit.clear();
        strategyStocksTargetValue.clear();
        strategyStocksUpperLimit.clear();
        strategyLiquidityLowerLimit.clear();
        strategyLiquidityTargetValue.clear();
        strategyLiquidityUpperLimit.clear();
        comment.clear();
        depoValue.clear();
        pieChart.setVisible(false);
    }
}

package controllers.stock;

import com.jfoenix.controls.JFXButton;
import yahooAPI.YahooStockAPI;
import alert.AlertDialog;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import entities.Stock;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import sql.EntityStockImpl;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
public class StockController implements Initializable {

    private double x, y = 0;
    @FXML
    public JFXComboBox<Stock> comboBox;
    @FXML
    public Label label;
    @FXML
    public JFXTextField name;
    @FXML
    public JFXTextField symbol;
    @FXML
    public JFXTextField exchange;
    @FXML
    public JFXTextField price;
    @FXML
    public JFXTextField change;
    @FXML
    public JFXTextField currency;
    @FXML
    public JFXTextField share;
    @FXML
    public ImageView imgView;
    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    public AnchorPane pane;
    @FXML
    private JFXButton addStock;
    @FXML
    private JFXButton updateStock;
    @FXML
    private JFXButton deleteStock;
    @FXML
    public JFXButton showAudit;
    @Getter
    private StockModel stockModel;
    private EntityStockImpl entityAktien;

    public StockController() {
        Locale.setDefault(Locale.GERMANY);
        this.stockModel = new StockModel();
        this.entityAktien = new EntityStockImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteStock.disableProperty().bind(comboBox.valueProperty().isNull());
        updateStock.disableProperty().bind(comboBox.valueProperty().isNull());
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        getAktien();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                stockModel.setStock(newValue);
                currency.setText(stockModel.getStock().getCurrency());
                label.setText(stockModel.getStock().getName());
                name.setText(stockModel.getStock().getName());
                symbol.setText(stockModel.getStock().getSymbol());
                exchange.setText(stockModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(stockModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(stockModel.getStock().getChange()));

                share.setText((stockModel.getStock().getShare() + " %").replace(".", ","));
                Plot task = new Plot(stockModel);

                task.setOnRunning(successesEvent -> {
                    progressIndicator.setVisible(true);
                });

                task.setOnSucceeded(succeededEvent -> {
                    try {
                        if (task.get() != null) {
                            imgView.setImage(task.get());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e);
                    }
                    progressIndicator.setVisible(false);
                });

                task.setOnFailed(failedEvent -> {
                    progressIndicator.setVisible(false);
                    log.error(" TASK FAILED! ");
                });

                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(task);
                executorService.shutdown();
            } else {
                currency.clear();
                label.setText("");
                name.clear();
                symbol.clear();
                exchange.clear();
                price.clear();
                change.clear();
                imgView.setImage(null);
                share.clear();
            }

        });

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/stock/stock_audit_modal.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
        });

        addStock.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/stock/stock_add_modal.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                dialog.setX(event.getScreenX() - x);
                dialog.setY(event.getScreenY() - y);
            });
            dialog.setScene(new Scene(root));
            dialog.setUserData(this);
            dialog.initOwner(addStock.getScene().getWindow());
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        });

        deleteStock.setOnMouseClicked(e -> {
            Alert alert = new AlertDialog()
                    .showConfirmationDialog("Ganz sicher?", "Bist du dir wirklich sicher, dass du diese Aktie löschen möchtest? \n" +
                            "Diese Aktion kann nicht widerrufen werden!");
            alert.showAndWait();


            if (alert.getResult() == ButtonType.YES) {
                // TODO: BUG?! stokcModel.getStock leifert immer 0 für die ID
                entityAktien.delete(comboBox.getSelectionModel().getSelectedItem());
                comboBox.getItems().remove(comboBox.getSelectionModel().getSelectedItem());
                comboBox.getSelectionModel().clearSelection();
                imgView.setImage(null);
                getAktien();
            }
        });

        updateStock.setOnMouseClicked(e -> {
            // TODO: BUG?! stokcModel.getStock leifert immer 0 für die ID
            Stock selectedStock = comboBox.getSelectionModel().getSelectedItem();
            if (entityAktien.update(selectedStock)) {
                new AlertDialog().showSuccessDialog("Erledigt!", "Aktie " + stockModel.getStock().getName() + " wurde erfolgreich aktualisiert.");
                comboBox.getItems().remove(selectedStock);
                getAktien();
                comboBox.getSelectionModel().select(selectedStock);
            }
        });

    }

    public void getAktien() {
        stockModel.setAktien(entityAktien.getAll());
        stockModel.getAktien().forEach(c -> {
            if (!comboBox.getItems().contains(c)) {
                comboBox.getItems().add(c);
            }
        });
    }

}

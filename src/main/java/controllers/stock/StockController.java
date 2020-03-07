package controllers.stock;

import YahooAPI.YahooStockAPI;
import alert.AlertDialog;
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
    public ComboBox<Stock> comboBox;
    @FXML
    public Label label;
    @FXML
    public TextField name;
    @FXML
    public TextField symbol;
    @FXML
    public TextField exchange;
    @FXML
    public TextField price;
    @FXML
    public TextField change;
    @FXML
    public TextField currency;
    @FXML
    public ImageView imgView;
    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    public AnchorPane pane;
    @FXML
    private Button addStock;
    @FXML
    private Button updateStock;
    @FXML
    private Button deleteStock;

    private StockModel stockModel;
    private EntityStockImpl entityAktien;
    private YahooStockAPI yahooStockAPI;

    public StockController() {
        Locale.setDefault(Locale.GERMANY);
        this.yahooStockAPI = new YahooStockAPI();
        this.stockModel = new StockModel();
        this.entityAktien = new EntityStockImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteStock.disableProperty().bind(comboBox.valueProperty().isNull());
        updateStock.disableProperty().bind(comboBox.valueProperty().isNull());

        getAktien();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null ) {
                stockModel.setHistory(new ArrayList<>(yahooStockAPI.getHistory(newValue.getSymbol())));
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
            }

        });

    }

    public void getAktien() {
        comboBox.getItems().clear();
        stockModel.setAktien(entityAktien.getAll());
        stockModel.getAktien().forEach(c -> comboBox.getItems().add(c));
    }

    @FXML
    public void addStock() throws IOException {
        Stage dialog = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/stock/stock_add_modal.fxml"));
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
    }

    @FXML
    public void updateStock(){
        // TODO: BUG?! stokcModel.getStock leifert immer 0 für die ID
        Stock selectedStock = comboBox.getSelectionModel().getSelectedItem();
        if (entityAktien.update(selectedStock)) {
            new AlertDialog().showSuccessDialog("Erledigt!", "Aktie " + stockModel.getStock().getName() + " wurde erfolgreich aktualisiert.");

            getAktien();
            comboBox.getSelectionModel().select(selectedStock);
        }
    }

    @FXML
    public void deleteStock(){
        Alert alert = new AlertDialog()
                .showConfirmationDialog("Ganz sicher?", "Bist du dir wirklich sicher, dass du diese Aktie löschen möchtest? \n" +
                        "Diese Aktion kann nicht widerrufen werden!");
        alert.showAndWait();


        if (alert.getResult() == ButtonType.YES) {
            // TODO: BUG?! stokcModel.getStock leifert immer 0 für die ID
            entityAktien.delete(comboBox.getSelectionModel().getSelectedItem());
            comboBox.getSelectionModel().clearSelection();
            getAktien();
        }
    }
}

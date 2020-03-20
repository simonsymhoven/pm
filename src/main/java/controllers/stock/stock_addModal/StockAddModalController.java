package controllers.stock.stock_addModal;


import alert.AlertDialog;
import javafx.application.Platform;
import yahooAPI.YahooStockAPI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.stock.StockController;
import entities.Stock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sql.EntityStockImpl;
import java.net.URL;
import java.util.ResourceBundle;

public class StockAddModalController implements Initializable {
    @FXML
    public Label info;
    @FXML
    public JFXButton addAktie;
    @FXML
    public JFXButton close;
    @FXML
    public JFXButton search;
    @FXML
    public JFXTextField symbol;
    @FXML
    public JFXTextField name;
    @FXML
    public JFXTextField symbol2;
    @FXML
    public JFXTextField exchange;
    @FXML
    public JFXTextField currency;
    @FXML
    public JFXTextField share;
    @FXML
    public Label shareInfo;

    private StockAddModalModel stockAddModalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityStockImpl entityAktien;
    private StockController stockController;
    private Stage stage;

    public StockAddModalController() {
        this.stockAddModalModel = new StockAddModalModel();
        this.yahooStockAPI = new YahooStockAPI();
        this.entityAktien = new EntityStockImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) addAktie.getScene().getWindow();
            stockController = (StockController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> {
            stockController.label.setText("Übersicht");
            stage.close();
        });

        entityAktien.getAll().forEach(stock -> stockAddModalModel.setAmount(stockAddModalModel.getAmount() - stock.getShare()));

        shareInfo.setText("Bitte wähle noch den Anteil der Aktie (Es sind noch " + stockAddModalModel.getAmount() + "% übrig):");

        search.disableProperty().bind(symbol.textProperty().isEmpty());

        share.disableProperty().bind(symbol2.textProperty().isEmpty()
                .or(name.textProperty().isEmpty().or(
                        exchange.textProperty().isEmpty().or(
                                currency.textProperty().isEmpty())
                )
        ));

        addAktie.disableProperty().bind(share.textProperty().isEmpty());

        symbol.textProperty().addListener((observableValue, s, newValue) -> stockAddModalModel.setSymbol(newValue));


        share.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("^(?=.*[0-9])\\d{0,2}(?:\\.\\d{0,2})?$") || Double.parseDouble(newValue) > stockAddModalModel.getAmount()) {
                share.setText(oldValue);
            }
            stockAddModalModel.getStock().setShare(Double.parseDouble(newValue));

        });

    }

    @FXML
    public void add() {
        info.setText("");
        if (entityAktien.add(stockAddModalModel.getStock())) {
            Alert alert = new AlertDialog().showSuccessDialog("Erledigt!", "Aktie wurde hinzugefügt.");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                stockController.getStocks();
                stockController.comboBox.getSelectionModel().select(stockAddModalModel.getStock());
                stage.close();
            }

        } else {
            new AlertDialog().showFailureDialog("Uuuups!", "Aktie konnte nicht hinzugefügt werden.");
        }
    }

    @FXML
    public void searchAktie() {
        Stock stock = yahooStockAPI.getStock(stockAddModalModel.getSymbol());
        stockAddModalModel.setStock(stock);

        if (stock != null) {
            symbol.clear();
            name.setText(stockAddModalModel.getStock().getName());
            symbol2.setText(stockAddModalModel.getStock().getSymbol());
            exchange.setText(stockAddModalModel.getStock().getExchange());
            currency.setText(stockAddModalModel.getStock().getCurrency());
        } else {
            info.setText("Aktie \"" + stockAddModalModel.getSymbol() + "\" wurde nicht gefunden! \n" +
                    "Bitte wähle eine andere Aktie aus und probiere es erneut.");
        }

    }
}

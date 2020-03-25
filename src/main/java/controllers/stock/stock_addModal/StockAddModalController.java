package controllers.stock.stock_addModal;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import snackbar.SnackBar;
import yahooapi.YahooStockAPI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.stock.StockController;
import entities.Stock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sql.EntityStockImpl;
import java.net.URL;
import java.util.ResourceBundle;

public class StockAddModalController implements Initializable {
    @FXML
    private JFXButton addStock;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton search;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol2;
    @FXML
    private JFXTextField exchange;
    @FXML
    private JFXTextField currency;
    @FXML
    private JFXTextField share;
    @FXML
    private Label shareInfo;
    @FXML
    private AnchorPane pane;

    private StockAddModalModel stockAddModalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityStockImpl entityStock;
    private StockController stockController;
    private Stage stage;
    private String regex = "^(([1-9][0-9]*)|0)?(\\.[0-9]*)?";

    public StockAddModalController() {
        this.stockAddModalModel = new StockAddModalModel();
        this.yahooStockAPI = new YahooStockAPI();
        this.entityStock = new EntityStockImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) addStock.getScene().getWindow();
            stockController = (StockController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> {
            stockController.getLabel().setText("Übersicht");
            stage.close();
        });

        entityStock.getAll().forEach(stock -> stockAddModalModel.setAmount(stockAddModalModel.getAmount() - stock.getShare()));

        shareInfo.setText("Bitte wähle noch den Anteil der Aktie im Musterportfolio.\n"
                + "Es stehen noch " + String.format("%.2f", stockAddModalModel.getAmount()) + "% zur Verfügung!");

        shareInfo.visibleProperty().bind(name.textProperty().isNotEmpty()
            .and(symbol2.textProperty().isNotEmpty()
                .and(exchange.textProperty().isNotEmpty()
                    .and(currency.textProperty().isNotEmpty())
                )
            )
        );

        share.visibleProperty().bind(name.textProperty().isNotEmpty()
                .and(symbol2.textProperty().isNotEmpty()
                        .and(exchange.textProperty().isNotEmpty()
                                .and(currency.textProperty().isNotEmpty())
                        )
                )
        );

        search.disableProperty().bind(symbol.textProperty().isEmpty());

        share.disableProperty().bind(symbol2.textProperty().isEmpty()
                .or(name.textProperty().isEmpty()
                .or(exchange.textProperty().isEmpty()
                .or(currency.textProperty().isEmpty()))
        ));

        addStock.disableProperty().bind(share.textProperty().isEmpty());

        symbol.textProperty().addListener((observableValue, s, newValue) -> {
            if (newValue.matches("[0-9]")) {
               symbol.setText(s);
            } else {
                stockAddModalModel.setSymbol(newValue);
            }
        });


        share.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    share.setText(oldValue);
                } else {
                    if (Double.parseDouble(newValue) > stockAddModalModel.getAmount()) {
                        share.setText(oldValue);
                    } else {
                        stockAddModalModel.getStock().setShare(Double.parseDouble(newValue));
                    }
                }
            }
        });

    }

    @FXML
    public void add() {
        if (entityStock.add(stockAddModalModel.getStock())) {
            stockController.getStocks();
            stockController.getComboBox().getSelectionModel().select(stockAddModalModel.getStock());
            SnackBar snackBar = new SnackBar(stockController.getPane());
            stage.close();
            snackBar.show("Aktie wurde erfolgreich hinzugefügt!");
        } else {
            clear();
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Aktie konnte nicht hinzugefügt werden,\nvermutlich existiert diese bereits!");
        }

    }

    @FXML
    public void searchAktie() {
        Stock stock = yahooStockAPI.getStock(stockAddModalModel.getSymbol());
        stockAddModalModel.setStock(stock);

        if (stock != null && !stock.getCurrency().equals("")) {
            name.setText(stockAddModalModel.getStock().getName());
            symbol2.setText(stockAddModalModel.getStock().getSymbol());
            exchange.setText(stockAddModalModel.getStock().getExchange());
            currency.setText(stockAddModalModel.getStock().getCurrency());
        } else {
            clear();
           SnackBar snackBar = new SnackBar(pane);
           snackBar.show("Es wurde keine passende Aktie gefunden!");
        }
    }

    private void clear() {
        symbol.clear();
        symbol2.clear();
        share.clear();
        currency.clear();
        exchange.clear();
        name.clear();
    }
}

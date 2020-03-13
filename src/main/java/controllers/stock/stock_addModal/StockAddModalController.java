package controllers.stock.stock_addModal;


import yahooAPI.YahooStockAPI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.stock.StockController;
import entities.Stock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private StockAddModalModel stockAddModalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityStockImpl entityAktien;

    public StockAddModalController() {
        this.stockAddModalModel = new StockAddModalModel();
        this.yahooStockAPI = new YahooStockAPI();
        this.entityAktien = new EntityStockImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        close.setOnMouseClicked(e -> {
            Stage stage = (Stage) addAktie.getScene().getWindow();
            StockController stockController = (StockController) stage.getUserData();
            stockController.getAktien();
            stockController.comboBox.getSelectionModel().select(stockAddModalModel.getStock());
            stage.close();
        });

        search.disableProperty().bind(symbol.textProperty().isEmpty());

        addAktie.disableProperty().bind(symbol2.textProperty().isEmpty()
                .or(name.textProperty().isEmpty().or(
                        exchange.textProperty().isEmpty().or(
                                currency.textProperty().isEmpty())
                )
        ));

        symbol.textProperty().addListener((observableValue, s, newValue) -> stockAddModalModel.setSymbol(newValue));
    }

    @FXML
    public void addAktie() {
        info.setText("");
        if (entityAktien.add(stockAddModalModel.getStock())) {
            Image img = new Image(getClass().getResourceAsStream("/img/icons8-ausgefüllte-checkbox-100.png"));
            Alert alertAdd = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Aktie wurde hinzugefügt.");
            alertAdd.setHeaderText("Erledigt!");
            alertAdd.setGraphic(new ImageView(img));
            alertAdd.show();

            name.clear();
            symbol2.clear();
            exchange.clear();
            currency.clear();
        } else {
            Image img = new Image(getClass().getResourceAsStream("/img/icons8-löschen-50.png"));
            Alert alertError = new Alert(
                    Alert.AlertType.ERROR,
                    "Aktie konnte nicht hinzugefügt werden.");
            alertError.setHeaderText("Uuuups!");
            alertError.setGraphic(new ImageView(img));
            alertError.show();

            name.clear();
            symbol2.clear();
            exchange.clear();
            currency.clear();
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

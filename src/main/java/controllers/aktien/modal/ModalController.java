package controllers.aktien.modal;

import YahooAPI.StockDTO;
import YahooAPI.YahooStockAPI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import yahoofinance.YahooFinance;

import javax.swing.event.ChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ModalController implements Initializable {

    @FXML
    public Button addAktie;
    @FXML
    public Label lblClose;
    @FXML
    public Button search;
    @FXML
    public TextField symbol;

    private ModalModel modalModel;
    private YahooStockAPI yahooStockAPI;

    public ModalController() {
        this.modalModel = new ModalModel();
        this.yahooStockAPI = new YahooStockAPI();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClose.setOnMouseClicked(e -> {
            Stage stage = (Stage) addAktie.getScene().getWindow();
            stage.close();
        });

        search.disableProperty().bind(symbol.textProperty().isEmpty());

        symbol.textProperty().addListener((observableValue, s, newValue) -> {
            modalModel.setSymbol(newValue);
        });
    }

    @FXML
    public void addAktie() {

    }

    @FXML
    public void searchAktie() {
        StockDTO stockDTO = yahooStockAPI.getStock(modalModel.getSymbol());

        if (stockDTO != null) {
            symbol.clear();
        }
    }
}

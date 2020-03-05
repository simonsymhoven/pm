package controllers.aktien.modal;


import YahooAPI.YahooStockAPI;
import controllers.aktien.AktienController;
import controllers.aktien.AktienModel;
import entities.Stock;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sql.EntityAktienImpl;
import java.net.URL;
import java.util.ResourceBundle;

public class ModalController implements Initializable {
    @FXML
    public TextArea info;
    @FXML
    public Button addAktie;
    @FXML
    public Label lblClose;
    @FXML
    public Button search;
    @FXML
    public TextField symbol;
    @FXML
    public TextField name;
    @FXML
    public TextField symbol2;
    @FXML
    public TextField exchange;
    @FXML
    public TextField currency;

    private ModalModel modalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityAktienImpl entityAktien;

    public ModalController() {
        this.modalModel = new ModalModel();
        this.yahooStockAPI = new YahooStockAPI();
        this.entityAktien = new EntityAktienImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClose.setOnMouseClicked(e -> {
            Stage stage = (Stage) addAktie.getScene().getWindow();
            AktienController aktienController = (AktienController) stage.getUserData();
            aktienController.getAktien();
            stage.close();
        });

        search.disableProperty().bind(symbol.textProperty().isEmpty());

        addAktie.disableProperty().bind(symbol2.textProperty().isEmpty()
                .and(name.textProperty().isEmpty().and(
                        exchange.textProperty().isEmpty().and(
                                currency.textProperty().isEmpty())
                )
        ));

        symbol.textProperty().addListener((observableValue, s, newValue) -> {
            modalModel.setSymbol(newValue);
        });
    }

    @FXML
    public void addAktie() {
        if (entityAktien.add(modalModel.getStock())) {
            info.setText("Aktie \"" + modalModel.getStock().getSymbol() + "\" wurde erfolgreich hinzugefügt und steht nun für die Portfolios zur Verfügung.");
            name.clear();
            symbol2.clear();
            exchange.clear();
            currency.clear();
        } else {
            info.setText("Aktie \"" + modalModel.getStock().getSymbol() + "\" konnte nicht hinzugefügt werden. \n" +
                    "Entweder exisitert diese bereits oder ein Fehler ist aufgetreten!");
        }
    }

    @FXML
    public void searchAktie() {
        Stock stock = yahooStockAPI.getStock(modalModel.getSymbol());
        modalModel.setStock(stock);

        if (stock != null) {
            info.setText("Aktie \"" + modalModel.getStock().getSymbol() + "\" wurde gefunden und kann nun hinzugefügt werden.");
            symbol.clear();
            name.setText(modalModel.getStock().getName());
            symbol2.setText(modalModel.getStock().getSymbol());
            exchange.setText(modalModel.getStock().getExchange());
            currency.setText(modalModel.getStock().getCurrency());
        } else {
            info.setText("Aktie \"" + modalModel.getSymbol() + "\" wurde nicht gefunden! \n" +
                    "Bitte wähle eine andere Aktie aus und probiere es erneut.");
        }

    }
}

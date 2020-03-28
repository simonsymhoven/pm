package controllers.investments.alternative.alternative_add_modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.investments.alternative.AlternativeController;
import entities.alternative.Alternative;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import snackbar.SnackBar;
import sql.EntityAlternativeImpl;
import yahooapi.YahooStockAPI;
import java.net.URL;
import java.util.ResourceBundle;

public class AlternativeAddModalController implements Initializable {
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
    private AnchorPane pane;

    private AlternativeAddModalModel alternativeAddModalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityAlternativeImpl entityAlternative;
    private AlternativeController alternativeController;
    private Stage stage;
    public AlternativeAddModalController() {
        this.alternativeAddModalModel = new AlternativeAddModalModel();
        this.yahooStockAPI = new YahooStockAPI();
        this.entityAlternative = new EntityAlternativeImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) addStock.getScene().getWindow();
            alternativeController = (AlternativeController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> {
            alternativeController.getLabel().setText("Übersicht");
            stage.close();
        });
        search.disableProperty().bind(symbol.textProperty().isEmpty());

        addStock.disableProperty().bind(name.textProperty().isEmpty()
                .or(symbol2.textProperty().isEmpty()
                        .or(exchange.textProperty().isEmpty()
                                .or(currency.textProperty().isEmpty())
                        )
                )
        );

        symbol.textProperty().addListener((observableValue, s, newValue) ->
                alternativeAddModalModel.setSymbol(newValue)
        );

    }

    @FXML
    public void add() {
        if (entityAlternative.add(alternativeAddModalModel.getAlternative())) {
            alternativeController.getAlternatives();
            alternativeController.getComboBox().getSelectionModel().select(alternativeAddModalModel.getAlternative());
            SnackBar snackBar = new SnackBar(alternativeController.getPane());
            stage.close();
            snackBar.show("Alternatives Investment wurde erfolgreich hinzugefügt!");
        } else {
            clear();
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Alternatives Investment konnte nicht hinzugefügt werden,\nvermutlich existiert diese bereits!");
        }

    }

    @FXML
    public void searchAlternative() {
        Alternative stock = yahooStockAPI.getAlternative(alternativeAddModalModel.getSymbol());
        alternativeAddModalModel.setAlternative(stock);

        if (stock != null && !stock.getCurrency().equals("")) {
            name.setText(alternativeAddModalModel.getAlternative().getName());
            symbol2.setText(alternativeAddModalModel.getAlternative().getSymbol());
            exchange.setText(alternativeAddModalModel.getAlternative().getExchange());
            currency.setText(alternativeAddModalModel.getAlternative().getCurrency());
        } else {
            clear();
           SnackBar snackBar = new SnackBar(pane);
           snackBar.show("Es wurde keine passendes alternatives Investment gefunden!");
        }
    }

    private void clear() {
        symbol.clear();
        symbol2.clear();
        currency.clear();
        exchange.clear();
        name.clear();
    }
}

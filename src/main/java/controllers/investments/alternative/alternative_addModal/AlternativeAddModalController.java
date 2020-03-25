package controllers.investments.alternative.alternative_addModal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controllers.investments.alternative.AlternativeController;
import entities.alternative.Alternative;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    private JFXTextField share;
    @FXML
    private Label shareInfo;
    @FXML
    private AnchorPane pane;

    private AlternativeAddModalModel alternativeAddModalModel;
    private YahooStockAPI yahooStockAPI;
    private EntityAlternativeImpl entityAlternative;
    private AlternativeController alternativeController;
    private Stage stage;
    private String regex = "^(([1-9][0-9]*)|0)?(\\.[0-9]*)?";

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

        entityAlternative.getAll().forEach(stock -> alternativeAddModalModel.setAmount(alternativeAddModalModel.getAmount() - stock.getShare()));

        shareInfo.setText("Bitte wähle noch den Anteil der Aktie im Musterportfolio.\n"
                + "Es stehen noch " + String.format("%.2f", alternativeAddModalModel.getAmount()) + "% zur Verfügung!");

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
            alternativeAddModalModel.setSymbol(newValue);
        });


        share.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    share.setText(oldValue);
                } else {
                    if (Double.parseDouble(newValue) > alternativeAddModalModel.getAmount()) {
                        share.setText(oldValue);
                    } else {
                        alternativeAddModalModel.getAlternative().setShare(Double.parseDouble(newValue));
                    }
                }
            }
        });

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
        share.clear();
        currency.clear();
        exchange.clear();
        name.clear();
    }
}

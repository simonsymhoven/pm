package controllers.aktien;

import YahooAPI.YahooStockAPI;
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
import sql.EntityAktienImpl;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
public class AktienController implements Initializable {
    private double x, y = 0;
    @FXML
    public ComboBox<String> comboBox;
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
    private Button addAktie;

    private AktienModel aktienModel;
    private EntityAktienImpl entityAktien;
    private YahooStockAPI yahooStockAPI;

    public AktienController() {
        Locale.setDefault(Locale.GERMANY);
        this.yahooStockAPI = new YahooStockAPI();
        this.aktienModel = new AktienModel();
        this.entityAktien = new EntityAktienImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getAktien();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            aktienModel.setHistory(yahooStockAPI.getHistory(newValue));
            aktienModel.setStock(aktienModel.getAktien().get(newValue));

            Plot task = new Plot(aktienModel);

            task.setOnRunning(successesEvent -> {
                progressIndicator.setVisible(true);
                currency.setText(aktienModel.getStock().getCurrency());
                label.setText(aktienModel.getStock().getName());
                name.setText(aktienModel.getStock().getName());
                symbol.setText(aktienModel.getStock().getSymbol());
                exchange.setText(aktienModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(aktienModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(aktienModel.getStock().getChange()));
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
        });

    }

    public void getAktien() {
        List<Stock> aktien = entityAktien.getAll();
        Map<String, Stock> map  = new HashMap<>();
        aktien.forEach(e -> map.put(e.getSymbol(), e));
        aktienModel.setAktien(map);
        comboBox.getItems().clear();
        for (Stock stock : aktienModel.getAktien().values()) {
            comboBox.getItems().add(stock.getSymbol());
        }
    }

    @FXML
    public void addAktie() throws IOException {
        Stage dialog = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/views/modal.fxml"));
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
        dialog.initOwner(addAktie.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}

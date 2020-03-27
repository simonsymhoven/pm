package controllers.investments.stocks;

import com.jfoenix.controls.JFXButton;
import entities.client.clientStock.ClientStock;
import entities.stock.Stock;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import snackbar.SnackBar;
import sql.EntityPortfolioStockImpl;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import sql.EntityStockImpl;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
public class StockController implements Initializable {

    private double x = 0;
    private double y = 0;
    @FXML
    @Getter
    private JFXComboBox<Stock> comboBox;
    @FXML
    @Getter
    private Label label;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField exchange;
    @FXML
    private JFXTextField price;
    @FXML
    private JFXTextField change;
    @FXML
    private JFXTextField currency;
    @FXML
    private JFXTextField share;
    @FXML
    private ImageView imgView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private JFXButton addStock;
    @FXML
    private JFXButton deleteStock;
    @FXML
    private JFXButton showAudit;
    @FXML
    @Getter
    private AnchorPane pane;
    @Getter
    private StockModel stockModel;
    private EntityStockImpl entityStock;
    private EntityPortfolioStockImpl entityPortfolio;

    public StockController() {
        Locale.setDefault(Locale.GERMANY);
        this.stockModel = new StockModel();
        this.entityStock = new EntityStockImpl();
        this.entityPortfolio = new EntityPortfolioStockImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());
        deleteStock.setDisable(true);
        getStocks();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            deleteStock.setDisable(false);
            if (newValue != null) {
                stockModel.setStock(newValue);

                List<ClientStock> list =  entityPortfolio.getAllForStock(newValue);
                if (list.size() > 0) {
                    deleteStock.setDisable(true);
                }

                currency.setText(stockModel.getStock().getCurrency());
                label.setText(stockModel.getStock().getName());
                name.setText(stockModel.getStock().getName());
                symbol.setText(stockModel.getStock().getSymbol());
                exchange.setText(stockModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(stockModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(stockModel.getStock().getChange()));

                share.setText(String.format("%.2f", stockModel.getStock().getShare()) + " %");

                plotStock();

            } else {
                clear();
            }

        });

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/investments/stock/stock_audit_modal.fxml"));
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
            } catch (IOException ex) {
               log.error(ex);
            }

        });

        addStock.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/investments/stock/stock_add_modal.fxml"));
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
            } catch (IOException ex) {
               log.error(ex);
            }

        });

        deleteStock.setOnMouseClicked(e -> {
            // TODO: BUG?! stokcModel.getStock leifert immer 0 für die ID
            log.info("BUGGY1?: " + stockModel.getStock().getId());

            if (entityStock.delete(comboBox.getSelectionModel().getSelectedItem())) {
                comboBox.getItems().remove(comboBox.getSelectionModel().getSelectedItem());
                comboBox.getSelectionModel().clearSelection();
                imgView.setImage(null);
                getStocks();
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Aktie wurde erfolgreich gelöscht!");
            } else {
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Aktie konnte nicht gelöscht werden!");
            }
        });
    }

    private void plotStock() {
        Plot task = new Plot(stockModel);

        task.setOnRunning(successesEvent -> {
                pane.setDisable(true);
                progressIndicator.setVisible(true);
        });

        task.setOnSucceeded(succeededEvent -> {
            try {
                if (task.get() != null) {
                    imgView.setImage(task.get());
                }
            } catch (Exception e) {
                log.error(e);
            }
            progressIndicator.setVisible(false);
            pane.setDisable(false);
        });

        task.setOnFailed(failedEvent -> {
            progressIndicator.setVisible(false);
            pane.setDisable(false);
            log.error(" TASK FAILED! ");
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    public void getStocks() {
        stockModel.setStocks(entityStock.getAll());
        stockModel.getStocks().forEach(c -> {
            if (!comboBox.getItems().contains(c)) {
                comboBox.getItems().add(c);
            }
        });
    }

    private void clear() {
        pane.getChildren()
                .filtered(node -> node instanceof JFXTextField)
                .forEach(node -> ((JFXTextField) node).clear());
    }

}

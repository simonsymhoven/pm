package controllers.portfolio.stock;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.client.Client;
import entities.client.client_stock.ClientStock;
import entities.client.client_stock.ClientStockKey;
import entities.stock.Stock;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityPortfolioStockImpl;
import sql.EntityStockImpl;
import sql.EntityClientImpl;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

@Log4j2
public class PortfolioStockController implements Initializable {
    @FXML
    private JFXComboBox<Client> comboBox;
    @FXML
    private JFXListView<Stock> stockList;
    @FXML
    private JFXListView<Stock> stockListClient;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField exchange;
    @FXML
    private JFXTextField change;
    @FXML
    private JFXTextField price;
    @FXML
    private JFXTextField currency;
    @FXML
    private JFXTextField quantity;
    @FXML
    private JFXTextField shareTarget;
    @FXML
    private JFXTextField shareActual;
    @FXML
    private JFXTextField diffRelative;
    @FXML
    private JFXTextField diffAbsolute;
    @FXML
    private JFXButton showAudit;
    @FXML
    private JFXButton update;
    @FXML
    private AnchorPane pane;

    private double x = 0;
    private double y = 0;
    @Getter
    private PortfolioStockModel portfolioStockModel;
    private EntityStockImpl entityStock;
    private EntityClientImpl entityClient;
    private EntityPortfolioStockImpl entityPortfolio;

    public PortfolioStockController() {
        this.portfolioStockModel = new PortfolioStockModel();
        this.entityStock = new EntityStockImpl();
        this.entityClient = new EntityClientImpl();
        this.entityPortfolio = new EntityPortfolioStockImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/portfolio/stock/stock_portfolio_audit_modal.fxml"));
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

        stockList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        stockListClient.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        portfolioStockModel.setStockList(entityStock.getAll());
        portfolioStockModel.setClients(entityClient.getAll());

        portfolioStockModel.getClients().forEach(c -> comboBox.getItems().add(c));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioStockModel.setClient(newValue);
                portfolioStockModel.getClientStocks().addAll(entityPortfolio.getAll(portfolioStockModel.getClient()));
                updateStockLists(portfolioStockModel.getClient());
            }
        });

        portfolioStockModel.setClientStocks(new ArrayList<>());

        update.disableProperty().bind(stockListClient.getSelectionModel().selectedItemProperty().isNull()
                .or(quantity.textProperty().isEqualTo("0")));

        shareActual.disableProperty().bind(quantity.textProperty().isEmpty());
        diffRelative.disableProperty().bind(shareActual.textProperty().isEmpty());
        diffAbsolute.disableProperty().bind(shareActual.textProperty().isEmpty());

        stockListClient.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioStockModel.setStock(newValue);

                portfolioStockModel.getClientStocks().forEach(c -> {
                    if (c.getClient().getId() == portfolioStockModel.getClient().getId() && c.getStock().getId() == portfolioStockModel.getStock().getId()) {
                        portfolioStockModel.setClientStock(c);
                    }
                });

                quantity.setText(String.valueOf(portfolioStockModel.getClientStock().getQuantity()));
                currency.setText(portfolioStockModel.getStock().getCurrency());
                name.setText(portfolioStockModel.getStock().getName());
                symbol.setText(portfolioStockModel.getStock().getSymbol());
                exchange.setText(portfolioStockModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioStockModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioStockModel.getStock().getChange()));
                shareActual.setText(String.format("%.2f", portfolioStockModel.getClientStock().getShareActual()).replace(",", ".") + " %");
                diffRelative.setText(String.format("%.2f", portfolioStockModel.getClientStock().getDiffRelative()).replace(",", ".") + " %");
                diffAbsolute.setText(String.valueOf(portfolioStockModel.getClientStock().getDiffAbsolute()));
                shareTarget.setText(String.format("%.2f", portfolioStockModel.getClientStock().getShareTarget()).replace(",", ".") + " %");

                quantity.setEditable(true);
            } else {
                clearFields();
                quantity.setEditable(false);
            }
        });

        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches("\\d*")) {
                    quantity.setText(newValue.replaceAll("\\D", ""));
                } else {
                    try {
                        int i = Integer.parseInt(newValue);
                        if (i > 0) {
                            portfolioStockModel.setQuantity(i);
                            calculate();
                            replaceClientStock();
                        }
                    } catch (Exception e) {
                        log.error(e);
                        log.error("Anzahl entspricht keiner positiven ganzzahligen Zahl!");
                    }
                }
            }
        });

        // Add mouse event handlers for the source
        stockList.setOnDragDetected(event -> {
            log.info("Event on Source: drag detected");
            dragDetected(event, stockList);
        });

        stockList.setOnDragOver(event -> {
            log.info("Event on Source: drag over");
            dragOver(event, stockList);
        });

        stockList.setOnDragDropped(event -> {
            log.info("Event on Source: drag dropped");
            dragDropped(event, stockList);
        });

        stockList.setOnDragDone(event -> {
            log.info("Event on Source: drag done");
            dragDone(event, stockList);
        });

        // Add mouse event handlers for the target
        stockListClient.setOnDragDetected(event -> {
            log.info("Event on Target: drag detected");
            dragDetected(event, stockListClient);
        });

        stockListClient.setOnDragOver(event -> {
            log.info("Event on Target: drag over");
            dragOver(event, stockListClient);
        });

        stockListClient.setOnDragDropped(event -> {
            log.info("Event on Target: drag dropped");
            dragDropped(event, stockListClient);
        });

        stockListClient.setOnDragDone(event -> {
            log.info("Event on Target: drag done");
            dragDone(event, stockListClient);
        });



    }

    private void calculate() {
        // SHARE SOLL
        double shareValue = (portfolioStockModel.getStock().getShare() / 100.0)
                * (portfolioStockModel.getClient().getStrategy().getStockTarget() / 100.0) * 100.0;
        portfolioStockModel.setShareTarget(shareValue);

        // SHARE IST
        double valueNewStock = portfolioStockModel.getStock().getPrice().doubleValue() * portfolioStockModel.getQuantity();
        double clientDepoValue = portfolioStockModel.getClient().getCapital().doubleValue();

        double shareValueIst = 100;
        if (clientDepoValue != 0) {
            shareValueIst = (valueNewStock / clientDepoValue) * 100.0;
        }

        portfolioStockModel.setShareActual(shareValueIst);
        shareActual.setText(String.format("%.2f", portfolioStockModel.getShareActual()).replace(",", ".") + " %");

        portfolioStockModel.setDiffRelative(portfolioStockModel.getShareTarget() - portfolioStockModel.getShareActual());
        diffRelative.setText(String.format("%.2f", portfolioStockModel.getDiffRelative()).replace(",", ".") + " %");

        portfolioStockModel.setDiffAbsolute(
                (int) ((portfolioStockModel.getDiffRelative() * portfolioStockModel.getClient().getCapital().doubleValue())
                        / portfolioStockModel.getStock().getPrice().doubleValue() / 100.0));

        shareActual.setText(String.format("%.2f", portfolioStockModel.getShareActual()).replace(",", ".") + " %");
        diffRelative.setText(String.format("%.2f", portfolioStockModel.getDiffRelative()).replace(",", ".") + " %");
        diffAbsolute.setText(String.valueOf(portfolioStockModel.getDiffAbsolute()));
        shareTarget.setText(String.format("%.2f", portfolioStockModel.getShareTarget()).replace(",", ".") + " %");
    }

    private void replaceClientStock() {
        portfolioStockModel.getClientStocks().remove(portfolioStockModel.getClientStock());
        portfolioStockModel.getClientStock().setQuantity(portfolioStockModel.getQuantity());
        portfolioStockModel.getClientStock().setShareTarget(portfolioStockModel.getShareTarget());
        portfolioStockModel.getClientStock().setShareActual(portfolioStockModel.getShareActual());
        portfolioStockModel.getClientStock().setDiffRelative(portfolioStockModel.getDiffRelative());
        portfolioStockModel.getClientStock().setDiffAbsolute(portfolioStockModel.getDiffAbsolute());
        portfolioStockModel.getClientStocks().add(portfolioStockModel.getClientStock());
    }


    private void clearFields() {
        name.clear();
        symbol.clear();
        exchange.clear();
        price.clear();
        change.clear();
        currency.clear();
        quantity.clear();
        shareActual.clear();
        shareTarget.clear();
        diffAbsolute.clear();
        diffRelative.clear();
    }

    private void updateStockLists(Client client) {
        Set<Stock> set = client.getStocks();
        stockListClient.getItems().clear();
        stockList.getItems().clear();
        stockList.getItems().addAll(portfolioStockModel.getStockList());
        set.forEach(stock -> {
            stockListClient.getItems().add(stock);
            stockList.getItems().remove(stock);
        });
    }

    private void dragDetected(MouseEvent event, ListView<Stock> listView) {
        int selectedCount = listView.getSelectionModel().getSelectedIndices().size();

        if (selectedCount == 0) {
            event.consume();
            return;
        }

        Dragboard dragboard = listView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        Stock selectedItem = getSelectedStock(listView);

        ClipboardContent content = new ClipboardContent();
        content.put(portfolioStockModel.getFormat(), selectedItem);

        dragboard.setContent(content);
        event.consume();
    }

    private void dragOver(DragEvent event, ListView<Stock> listView) {
        Dragboard dragboard = event.getDragboard();

        if (event.getGestureSource() != listView && dragboard.hasContent(portfolioStockModel.getFormat())) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event, ListView<Stock> listView) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasContent(portfolioStockModel.getFormat())) {
            Stock stock = (Stock) dragboard.getContent(portfolioStockModel.getFormat());
            listView.getItems().add(stock);
            dragCompleted = true;
        }

        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    private void dragDone(DragEvent event, ListView<Stock> listView) {
        TransferMode tm = event.getTransferMode();
        if (tm.equals(TransferMode.COPY) || tm.equals(TransferMode.MOVE)) {
            removeSelectedStock(listView);
        }

        event.consume();
    }


    private Stock getSelectedStock(ListView<Stock> listView) {
        return listView.getSelectionModel().getSelectedItem();
    }

    private void removeSelectedStock(ListView<Stock> listView) {
        Stock selectedStock = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().clearSelection();
        listView.getItems().remove(selectedStock);


        if (listView.getId().equals("stockListClient")) {
            log.info("[1/2] Aktie " + selectedStock + " wird dem Nutzer entzogen. Aus listView entfernen.");
            if (entityClient.removeStock(portfolioStockModel.getClient(), selectedStock)) {
                portfolioStockModel.getClientStocks().removeIf(clientStock -> clientStock.getClient().getId() == portfolioStockModel.getClient().getId()
                        && clientStock.getStock().getId() == selectedStock.getId());
                log.info("[2/2] Aktie " + selectedStock + " wird dem Nutzer entzogen. Aus Model entfernen.");
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Aktie wurde dem Client entzogen!");
            }
        } else if (listView.getId().equals("stockList")) {
            double shareValue =
                    (selectedStock.getShare() / 100.0) * (portfolioStockModel.getClient().getStrategy().getStockTarget() / 100.0) * 100.0;
            ClientStock clientStock = new ClientStock(
                    new ClientStockKey(portfolioStockModel.getClient().getId(), selectedStock.getId()),
                    portfolioStockModel.getClient(),
                    selectedStock,
                    0,
                    shareValue,
                    0,
                    0,
                    0
            );
            portfolioStockModel.getClientStocks().add(clientStock);
            entityPortfolio.update(clientStock);
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Aktie wurde dem Client hinzugefügt!");
        }
    }

    public void refresh() {
        portfolioStockModel.getClientStocks().forEach(clientStock -> entityPortfolio.update(clientStock));
        SnackBar snackBar = new SnackBar(pane);
        snackBar.show("Client wurde aktualisiert!");
    }
}

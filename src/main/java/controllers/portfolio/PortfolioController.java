package controllers.portfolio;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.Client;
import entities.ClientStock;
import entities.ClientStockKey;
import entities.Stock;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import sql.EntityPortfolioImpl;
import sql.EntityStockImpl;
import sql.EntityClientImpl;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

@Log4j2
public class PortfolioController implements Initializable {
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

    private double x = 0;
    private double y = 0;
    @Getter
    private PortfolioModel portfolioModel;
    private EntityStockImpl entityStock;
    private EntityClientImpl entityClient;
    private EntityPortfolioImpl entityPortfolio;

    public PortfolioController() {
        this.portfolioModel = new PortfolioModel();
        this.entityStock = new EntityStockImpl();
        this.entityClient = new EntityClientImpl();
        this.entityPortfolio = new EntityPortfolioImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/portfolio/portfolio_audit_modal.fxml"));
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

        portfolioModel.setStockList(entityStock.getAll());
        portfolioModel.setClients(entityClient.getAll());

        portfolioModel.getClients().forEach(c -> comboBox.getItems().add(c));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioModel.setClient(newValue);
                portfolioModel.getClientStocks().addAll(entityPortfolio.getAll(portfolioModel.getClient()));
                updateStockLists(portfolioModel.getClient());
            }
        });

        portfolioModel.setClientStocks(new ArrayList<>());

        update.disableProperty().bind(stockListClient.getSelectionModel().selectedItemProperty().isNull()
                .or(quantity.textProperty().isEqualTo("0")));

        shareActual.disableProperty().bind(quantity.textProperty().isEmpty());
        diffRelative.disableProperty().bind(shareActual.textProperty().isEmpty());
        diffAbsolute.disableProperty().bind(shareActual.textProperty().isEmpty());

        stockListClient.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioModel.setStock(newValue);

                portfolioModel.getClientStocks().forEach(c -> {
                    if (c.getClient().getId() == portfolioModel.getClient().getId() && c.getStock().getId() == portfolioModel.getStock().getId()) {
                        portfolioModel.setClientStock(c);
                    }
                });

                quantity.setText(String.valueOf(portfolioModel.getClientStock().getQuantity()));
                currency.setText(portfolioModel.getStock().getCurrency());
                name.setText(portfolioModel.getStock().getName());
                symbol.setText(portfolioModel.getStock().getSymbol());
                exchange.setText(portfolioModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getChange()));
                shareActual.setText(String.format("%.2f", portfolioModel.getClientStock().getShareActual()).replace(",", ".") + " %");
                diffRelative.setText(String.format("%.2f", portfolioModel.getClientStock().getDiffRelative()).replace(",", ".") + " %");
                diffAbsolute.setText(String.valueOf(portfolioModel.getClientStock().getDiffAbsolute()));
                shareTarget.setText(String.format("%.2f", portfolioModel.getClientStock().getShareTarget()).replace(",", ".") + " %");

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
                            portfolioModel.setQuantity(i);
                            calculateDepot();
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

    private void calculateDepot() {
        // SHARE SOLL
        double shareValue = (portfolioModel.getStock().getShare() / 100.0)
                * (portfolioModel.getClient().getStrategy() / 100.0) * 100.0;
        portfolioModel.setShareTarget(shareValue);

        // SHARE IST
        double valueNewStock = portfolioModel.getStock().getPrice().doubleValue() * portfolioModel.getQuantity();
        double clientDepoValue = portfolioModel.getClient().getDepoValue().doubleValue();

        double shareValueIst = 100;
        if (clientDepoValue != 0) {
            shareValueIst = (valueNewStock / clientDepoValue) * 100.0;
        }

        portfolioModel.setShareActual(shareValueIst);
        shareActual.setText(String.format("%.2f", portfolioModel.getShareActual()).replace(",", ".") + " %");

        portfolioModel.setDiffRelative(portfolioModel.getShareTarget() - portfolioModel.getShareActual());
        diffRelative.setText(String.format("%.2f", portfolioModel.getDiffRelative()).replace(",", ".") + " %");

        portfolioModel.setDiffAbsolute(
                (int) ((portfolioModel.getDiffRelative() * portfolioModel.getClient().getDepoValue().doubleValue())
                        / portfolioModel.getStock().getPrice().doubleValue() / 100.0));

        shareActual.setText(String.format("%.2f", portfolioModel.getShareActual()).replace(",", ".") + " %");
        diffRelative.setText(String.format("%.2f", portfolioModel.getDiffRelative()).replace(",", ".") + " %");
        diffAbsolute.setText(String.valueOf(portfolioModel.getDiffAbsolute()));
        shareTarget.setText(String.format("%.2f", portfolioModel.getShareTarget()).replace(",", ".") + " %");
    }

    private void replaceClientStock() {
        portfolioModel.getClientStocks().remove(portfolioModel.getClientStock());
        portfolioModel.getClientStock().setQuantity(portfolioModel.getQuantity());
        portfolioModel.getClientStock().setShareTarget(portfolioModel.getShareTarget());
        portfolioModel.getClientStock().setShareActual(portfolioModel.getShareActual());
        portfolioModel.getClientStock().setDiffRelative(portfolioModel.getDiffRelative());
        portfolioModel.getClientStock().setDiffAbsolute(portfolioModel.getDiffAbsolute());
        portfolioModel.getClientStocks().add(portfolioModel.getClientStock());
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
        stockList.getItems().addAll(portfolioModel.getStockList());
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
        content.put(portfolioModel.getFormat(), selectedItem);

        dragboard.setContent(content);
        event.consume();
    }

    private void dragOver(DragEvent event, ListView<Stock> listView) {
        Dragboard dragboard = event.getDragboard();

        if (event.getGestureSource() != listView && dragboard.hasContent(portfolioModel.getFormat())) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event, ListView<Stock> listView) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasContent(portfolioModel.getFormat())) {
            Stock stock = (Stock) dragboard.getContent(portfolioModel.getFormat());
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
            log.info("[1/2] Aktie " + selectedStock + " wird dem Nutzer entzogen. AUs listView entfernen.");
            if (entityClient.removeStock(portfolioModel.getClient(), selectedStock)) {
                for (ClientStock clientStock : portfolioModel.getClientStocks()) {
                    if (clientStock.getClient().getId() == portfolioModel.getClient().getId() && clientStock.getStock().getId() == selectedStock.getId()) {
                        log.info("[2/2] Aktie " + selectedStock + " wird dem Nutzer entzogen. Aus Model entfernen.");
                        portfolioModel.getClientStocks().remove(clientStock);
                    }
                }
            }
        } else if (listView.getId().equals("stockList")) {
            double shareValue =
                    (selectedStock.getShare() / 100.0) * (portfolioModel.getClient().getStrategy() / 100.0) * 100.0;
            portfolioModel.getClientStocks().add(new ClientStock(
                    new ClientStockKey(portfolioModel.getClient().getId(), selectedStock.getId()),
                    portfolioModel.getClient(),
                    selectedStock,
                    0,
                    shareValue,
                    0,
                    0,
                    0
            ));
        }
    }

    public void refresh() {
        portfolioModel.getClientStocks().forEach(clientStock -> entityPortfolio.update(clientStock));
    }
}

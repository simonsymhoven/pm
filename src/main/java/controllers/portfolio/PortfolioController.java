package controllers.portfolio;

import entities.Client;
import entities.Stock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import lombok.extern.log4j.Log4j2;
import sql.EntityStockImpl;
import sql.EntityClientImpl;

import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

@Log4j2
public class PortfolioController implements Initializable {
    @FXML
    public ComboBox<Client> comboBox;
    @FXML
    public Button save;
    @FXML
    public Button cancel;
    @FXML
    private ListView<Stock> aktienList;
    @FXML
    private ListView<Stock> aktienListKunde;
    @FXML
    private TextField name;
    @FXML
    private TextField symbol;
    @FXML
    private TextField exchange;
    @FXML
    private TextField price;
    @FXML
    private TextField change;
    @FXML
    private TextField currency;




    private PortfolioModel portfolioModel;
    private EntityStockImpl entityAktien;
    private EntityClientImpl entityClient;

    public PortfolioController() {
        this.portfolioModel = new PortfolioModel();
        this.entityAktien = new EntityStockImpl();
        this.entityClient = new EntityClientImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.disableProperty().bind(comboBox.valueProperty().isNull());
        cancel.disableProperty().bind(comboBox.valueProperty().isNull());

        aktienList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        aktienListKunde.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        portfolioModel.setAktienList(entityAktien.getAll());
        portfolioModel.setClients(entityClient.getAll());

        portfolioModel.getClients().forEach(c -> comboBox.getItems().add(c));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioModel.setClient(newValue);
                updateStockLists(portfolioModel.getClient());
            }
        });

        aktienListKunde.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioModel.setStock(newValue);

                currency.setText(portfolioModel.getStock().getCurrency());
                name.setText(portfolioModel.getStock().getName());
                symbol.setText(portfolioModel.getStock().getSymbol());
                exchange.setText(portfolioModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getChange()));

            } else {
                clearFields();

            }
        });


        // Add mouse event handlers for the source
        aktienList.setOnDragDetected(event -> {
            log.info("Event on Source: drag detected");
            dragDetected(event, aktienList);
        });

        aktienList.setOnDragOver(event -> {
            log.info("Event on Source: drag over");
            dragOver(event, aktienList);
        });

        aktienList.setOnDragDropped(event -> {
            log.info("Event on Source: drag dropped");
            dragDropped(event, aktienList);
        });

        aktienList.setOnDragDone(event -> {
            log.info("Event on Source: drag done");
            dragDone(event, aktienList);
        });

        // Add mouse event handlers for the target
        aktienListKunde.setOnDragDetected(event -> {
            log.info("Event on Target: drag detected");
            dragDetected(event, aktienListKunde);
        });

        aktienListKunde.setOnDragOver(event -> {
            log.info("Event on Target: drag over");
            dragOver(event, aktienListKunde);
        });

        aktienListKunde.setOnDragDropped(event -> {
            log.info("Event on Target: drag dropped");
            dragDropped(event, aktienListKunde);
        });

        aktienListKunde.setOnDragDone(event -> {
            log.info("Event on Target: drag done");
            dragDone(event, aktienListKunde);
        });


    }

    private void clearFields() {
        name.clear();
        symbol.clear();
        exchange.clear();
        price.clear();
        change.clear();
        currency.clear();
    }

    private void updateStockLists(Client client) {
        Set<Stock> set = client.getStocks();
        aktienListKunde.getItems().clear();
        aktienList.getItems().clear();
        aktienList.getItems().addAll(portfolioModel.getAktienList());
        set.forEach(stock -> {
            aktienListKunde.getItems().add(stock);
            aktienList.getItems().remove(stock);
        });
    }

    private void dragDetected(MouseEvent event, ListView<Stock> listView) {
        int selectedCount = listView.getSelectionModel().getSelectedIndices().size();

        if (selectedCount == 0) {
            event.consume();
            return;
        }

        Dragboard dragboard = listView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        ArrayList<Stock> selectedItem = getSelectedAktien(listView);

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

        if(dragboard.hasContent(portfolioModel.getFormat())) {
            ArrayList<Stock> list = (ArrayList<Stock>)dragboard.getContent(portfolioModel.getFormat());
            listView.getItems().addAll(list);
            dragCompleted = true;
        }

        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    private void dragDone(DragEvent event, ListView<Stock> listView) {
        TransferMode tm = event.getTransferMode();
        log.info("TRANSFER MODE: " + tm.toString());
        if (tm == TransferMode.COPY) {
            removeSelectedAktie(listView);
        }

        event.consume();
    }


    private ArrayList<Stock> getSelectedAktien(ListView<Stock> listView) {
        ArrayList<Stock> list = new ArrayList(listView.getSelectionModel().getSelectedItems());
        return list;
    }

    private void removeSelectedAktie(ListView<Stock> listView) {
        List<Stock> selectedList = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
        log.info("Items to delete: " + selectedList);
        listView.getSelectionModel().clearSelection();
        listView.getItems().removeAll(selectedList);
    }

    @FXML
    public void save() {
        Set<Stock> stocks = new HashSet<>();
        aktienListKunde.getItems().forEach(stock -> {
            stocks.add(stock);
        });

        portfolioModel.getClient().setStocks(stocks);
        if (entityClient.update(portfolioModel.getClient())) {
            Image img = new Image(getClass().getResourceAsStream("/img/icons8-ausgefüllte-checkbox-100.png"));
            Alert alertAdd = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Portfolio für " + portfolioModel.getClient().getName() + " wurde aktualisiert.");
            alertAdd.setHeaderText("Erledigt!");
            alertAdd.setGraphic(new ImageView(img));
            alertAdd.show();
        } else {
            Image img = new Image(getClass().getResourceAsStream("/img/icons8-löschen-50.png"));
            Alert alertError = new Alert(
                    Alert.AlertType.ERROR,
                    "Portfolio für " + portfolioModel.getClient().getName() + " konnte nicht aktualisiert werden.");
            alertError.setHeaderText("Uuuups!");
            alertError.setGraphic(new ImageView(img));
            alertError.show();
        }

        reset();
    }

    @FXML
    public void cancel() {
       reset();
    }

    private void reset(){
        aktienList.getItems().clear();
        aktienListKunde.getItems().clear();
        comboBox.getSelectionModel().clearSelection();
    }
}

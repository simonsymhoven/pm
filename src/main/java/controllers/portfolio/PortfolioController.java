package controllers.portfolio;

import entities.Client;
import entities.Stock;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import lombok.extern.log4j.Log4j2;
import sql.EntityAktienImpl;
import sql.EntityClientImpl;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Log4j2
public class PortfolioController implements Initializable {
    @FXML
    public ComboBox<String> comboBox;
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
    private EntityAktienImpl entityAktien;
    private EntityClientImpl entityClient;

    public PortfolioController() {
        this.portfolioModel = new PortfolioModel();
        this.entityAktien = new EntityAktienImpl();
        this.entityClient = new EntityClientImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        portfolioModel.setAktienList(entityAktien.getAll());

        getClients();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            portfolioModel.setClient(entityClient.get(newValue));
            updateStockLists(portfolioModel.getClient());
        });

        aktienList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        aktienListKunde.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        aktienList.setItems(FXCollections.observableList(portfolioModel.getAktienList()));

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
                name.clear();
                symbol.clear();
                exchange.clear();
                price.clear();
                change.clear();
                currency.clear();
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

    private void updateStockLists(Client client) {

    }

    private void getClients() {
        portfolioModel.setClients(entityClient.getAll());
        portfolioModel.getClients().forEach(c -> comboBox.getItems().add(c.symbol));
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
    }

    @FXML
    public void cancel() {
    }
}

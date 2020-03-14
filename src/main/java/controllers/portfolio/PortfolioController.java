package controllers.portfolio;

import com.fasterxml.jackson.databind.type.PlaceholderForType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.Client;
import entities.ClientStock;
import entities.ClientStockKey;
import entities.Stock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
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
import java.util.*;

@Log4j2
public class PortfolioController implements Initializable {
    double x,y = 0;
    @FXML
    public JFXComboBox<Client> comboBox;
    @FXML
    private JFXListView<Stock> aktienList;
    @FXML
    private ListView<Stock> aktienListKunde;
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
    private JFXTextField quantatiy;
    @FXML
    public JFXButton showAudit;

    @Getter
    public PortfolioModel portfolioModel;
    private EntityStockImpl entityAktien;
    private EntityClientImpl entityClient;
    private EntityPortfolioImpl entityPortfolio;

    public PortfolioController() {
        this.portfolioModel = new PortfolioModel();
        this.entityAktien = new EntityStockImpl();
        this.entityClient = new EntityClientImpl();
        this.entityPortfolio = new EntityPortfolioImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/portfolio/portfolio_audit_modal.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

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
        });

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
                getClientStock();
                quantatiy.setText(String.valueOf(portfolioModel.getClientStock().getQuantatiy()));
                currency.setText(portfolioModel.getStock().getCurrency());
                name.setText(portfolioModel.getStock().getName());
                symbol.setText(portfolioModel.getStock().getSymbol());
                exchange.setText(portfolioModel.getStock().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioModel.getStock().getChange()));

                quantatiy.setEditable(true);
            } else {
                clearFields();
                quantatiy.setEditable(false);
            }
        });




        quantatiy.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantatiy.setText(newValue.replaceAll("\\D", ""));
            } else {
                entityPortfolio.update(new ClientStock(
                        new ClientStockKey(portfolioModel.getClient().getId(), portfolioModel.getStock().getId()),
                        portfolioModel.getClient(),
                        portfolioModel.getStock(),
                        Integer.valueOf(newValue)
                ));
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

    private void getClientStock() {
        portfolioModel.setClientStock(
                entityPortfolio.get(
                    new ClientStockKey(portfolioModel.getClient().getId(), portfolioModel.getStock().getId())
                )
        );
    }

    private void clearFields() {
        name.clear();
        symbol.clear();
        exchange.clear();
        price.clear();
        change.clear();
        currency.clear();
        quantatiy.clear();
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
        if (tm.equals(TransferMode.COPY) || tm.equals(TransferMode.MOVE)) {
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
        log.info("Items selectd: " + selectedList);
        listView.getSelectionModel().clearSelection();
        listView.getItems().removeAll(selectedList);

        if (listView.getId().equals("aktienList")) {
            log.info("Stocks werden dem Nutzer hinzugefügt.");
            entityClient.addStocks(portfolioModel.getClient(),selectedList);
        } else if (listView.getId().equals("aktienListKunde")) {
            log.info("Stocks werden dem Nutzer entzogen.");
            entityClient.removeStocks(portfolioModel.getClient(), selectedList);
        }

    }

}

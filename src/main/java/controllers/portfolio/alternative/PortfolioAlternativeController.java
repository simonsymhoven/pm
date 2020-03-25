package controllers.portfolio.alternative;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entities.alternative.Alternative;
import entities.client.Client;
import entities.client.ClientAlternative;
import entities.client.ClientAlternativeKey;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityAlternativeImpl;
import sql.EntityClientImpl;
import sql.EntityPortfolioAlternativeImpl;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

@Log4j2
public class PortfolioAlternativeController implements Initializable {
    @FXML
    private JFXComboBox<Client> comboBox;
    @FXML
    private JFXListView<Alternative> alternativeList;
    @FXML
    private JFXListView<Alternative> alternativeListClient;
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
    private PortfolioAlternativeModel portfolioAlternativeModel;
    private EntityAlternativeImpl entityAlternative;
    private EntityClientImpl entityClient;
    private EntityPortfolioAlternativeImpl entityPortfolioAlternative;

    public PortfolioAlternativeController() {
        this.portfolioAlternativeModel = new PortfolioAlternativeModel();
        this.entityAlternative = new EntityAlternativeImpl();
        this.entityClient = new EntityClientImpl();
        this.entityPortfolioAlternative = new EntityPortfolioAlternativeImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());

        showAudit.setOnMouseClicked(e -> {
            Stage dialog = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/portfolio/alternative/alternative_portfolio_audit_modal.fxml"));
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

        alternativeList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        alternativeListClient.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        portfolioAlternativeModel.setAlternatives(entityAlternative.getAll());
        portfolioAlternativeModel.setClients(entityClient.getAll());

        portfolioAlternativeModel.getClients().forEach(c -> comboBox.getItems().add(c));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioAlternativeModel.setClient(newValue);
                portfolioAlternativeModel.getClientAlternatives().addAll(entityPortfolioAlternative.getAll(portfolioAlternativeModel.getClient()));
                updateStockLists(portfolioAlternativeModel.getClient());
            }
        });

        portfolioAlternativeModel.setClientAlternatives(new ArrayList<>());

        update.disableProperty().bind(alternativeListClient.getSelectionModel().selectedItemProperty().isNull()
                .or(quantity.textProperty().isEqualTo("0")));

        shareActual.disableProperty().bind(quantity.textProperty().isEmpty());
        diffRelative.disableProperty().bind(shareActual.textProperty().isEmpty());
        diffAbsolute.disableProperty().bind(shareActual.textProperty().isEmpty());

        alternativeListClient.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                portfolioAlternativeModel.setAlternative(newValue);

                portfolioAlternativeModel.getClientAlternatives().forEach(c -> {
                    if (c.getClient().getId() == portfolioAlternativeModel.getClient().getId()
                            && c.getAlternative().getId() == portfolioAlternativeModel.getAlternative().getId()) {
                        portfolioAlternativeModel.setClientAlternative(c);
                    }
                });

                quantity.setText(String.valueOf(portfolioAlternativeModel.getClientAlternative().getQuantity()));
                currency.setText(portfolioAlternativeModel.getAlternative().getCurrency());
                name.setText(portfolioAlternativeModel.getAlternative().getName());
                symbol.setText(portfolioAlternativeModel.getAlternative().getSymbol());
                exchange.setText(portfolioAlternativeModel.getAlternative().getExchange());
                price.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioAlternativeModel.getAlternative().getPrice()));
                change.setText(NumberFormat.getCurrencyInstance()
                        .format(portfolioAlternativeModel.getAlternative().getChange()));
                shareActual.setText(String.format("%.2f", portfolioAlternativeModel.getClientAlternative().getShareActual()).replace(",", ".") + " %");
                diffRelative.setText(String.format("%.2f", portfolioAlternativeModel.getClientAlternative().getDiffRelative()).replace(",", ".") + " %");
                diffAbsolute.setText(String.valueOf(portfolioAlternativeModel.getClientAlternative().getDiffAbsolute()));
                shareTarget.setText(String.format("%.2f", portfolioAlternativeModel.getClientAlternative().getShareTarget()).replace(",", ".") + " %");

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
                            portfolioAlternativeModel.setQuantity(i);
                            calculateDepot();
                            replaceClientAlternative();
                        }
                    } catch (Exception e) {
                        log.error(e);
                        log.error("Anzahl entspricht keiner positiven ganzzahligen Zahl!");
                    }
                }
            }
        });

        // Add mouse event handlers for the source
        alternativeList.setOnDragDetected(event -> {
            log.info("Event on Source: drag detected");
            dragDetected(event, alternativeList);
        });

        alternativeList.setOnDragOver(event -> {
            log.info("Event on Source: drag over");
            dragOver(event, alternativeList);
        });

        alternativeList.setOnDragDropped(event -> {
            log.info("Event on Source: drag dropped");
            dragDropped(event, alternativeList);
        });

        alternativeList.setOnDragDone(event -> {
            log.info("Event on Source: drag done");
            dragDone(event, alternativeList);
        });

        // Add mouse event handlers for the target
        alternativeListClient.setOnDragDetected(event -> {
            log.info("Event on Target: drag detected");
            dragDetected(event, alternativeListClient);
        });

        alternativeListClient.setOnDragOver(event -> {
            log.info("Event on Target: drag over");
            dragOver(event, alternativeListClient);
        });

        alternativeListClient.setOnDragDropped(event -> {
            log.info("Event on Target: drag dropped");
            dragDropped(event, alternativeListClient);
        });

        alternativeListClient.setOnDragDone(event -> {
            log.info("Event on Target: drag done");
            dragDone(event, alternativeListClient);
        });



    }

    private void calculateDepot() {
        // SHARE SOLL
        double shareValue = (portfolioAlternativeModel.getAlternative().getShare() / 100.0)
                * (portfolioAlternativeModel.getClient().getStrategyStocksTargetValue() / 100.0) * 100.0;
        portfolioAlternativeModel.setShareTarget(shareValue);

        // SHARE IST
        double valueNewStock = portfolioAlternativeModel.getAlternative().getPrice().doubleValue() * portfolioAlternativeModel.getQuantity();
        double clientDepoValue = portfolioAlternativeModel.getClient().getDepoValue().doubleValue();

        double shareValueIst = 100;
        if (clientDepoValue != 0) {
            shareValueIst = (valueNewStock / clientDepoValue) * 100.0;
        }

        portfolioAlternativeModel.setShareActual(shareValueIst);
        shareActual.setText(String.format("%.2f", portfolioAlternativeModel.getShareActual()).replace(",", ".") + " %");

        portfolioAlternativeModel.setDiffRelative(portfolioAlternativeModel.getShareTarget() - portfolioAlternativeModel.getShareActual());
        diffRelative.setText(String.format("%.2f", portfolioAlternativeModel.getDiffRelative()).replace(",", ".") + " %");

        portfolioAlternativeModel.setDiffAbsolute(
                (int) ((portfolioAlternativeModel.getDiffRelative() * portfolioAlternativeModel.getClient().getDepoValue().doubleValue())
                        / portfolioAlternativeModel.getAlternative().getPrice().doubleValue() / 100.0));

        shareActual.setText(String.format("%.2f", portfolioAlternativeModel.getShareActual()).replace(",", ".") + " %");
        diffRelative.setText(String.format("%.2f", portfolioAlternativeModel.getDiffRelative()).replace(",", ".") + " %");
        diffAbsolute.setText(String.valueOf(portfolioAlternativeModel.getDiffAbsolute()));
        shareTarget.setText(String.format("%.2f", portfolioAlternativeModel.getShareTarget()).replace(",", ".") + " %");
    }

    private void replaceClientAlternative() {
        portfolioAlternativeModel.getClientAlternatives().remove(portfolioAlternativeModel.getClientAlternative());
        portfolioAlternativeModel.getClientAlternative().setQuantity(portfolioAlternativeModel.getQuantity());
        portfolioAlternativeModel.getClientAlternative().setShareTarget(portfolioAlternativeModel.getShareTarget());
        portfolioAlternativeModel.getClientAlternative().setShareActual(portfolioAlternativeModel.getShareActual());
        portfolioAlternativeModel.getClientAlternative().setDiffRelative(portfolioAlternativeModel.getDiffRelative());
        portfolioAlternativeModel.getClientAlternative().setDiffAbsolute(portfolioAlternativeModel.getDiffAbsolute());
        portfolioAlternativeModel.getClientAlternatives().add(portfolioAlternativeModel.getClientAlternative());
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
        Set<Alternative> set = client.getAlternatives();
        alternativeListClient.getItems().clear();
        alternativeList.getItems().clear();
        alternativeList.getItems().addAll(portfolioAlternativeModel.getAlternatives());
        set.forEach(alternative -> {
            alternativeListClient.getItems().add(alternative);
            alternativeList.getItems().remove(alternative);
        });
    }

    private void dragDetected(MouseEvent event, ListView<Alternative> listView) {
        int selectedCount = listView.getSelectionModel().getSelectedIndices().size();

        if (selectedCount == 0) {
            event.consume();
            return;
        }

        Dragboard dragboard = listView.startDragAndDrop(TransferMode.COPY_OR_MOVE);
        Alternative selectedItem = getSelectedAlternative(listView);

        ClipboardContent content = new ClipboardContent();
        content.put(portfolioAlternativeModel.getFormat(), selectedItem);

        dragboard.setContent(content);
        event.consume();
    }

    private void dragOver(DragEvent event, ListView<Alternative> listView) {
        Dragboard dragboard = event.getDragboard();

        if (event.getGestureSource() != listView && dragboard.hasContent(portfolioAlternativeModel.getFormat())) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event, ListView<Alternative> listView) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasContent(portfolioAlternativeModel.getFormat())) {
            Alternative alternative = (Alternative) dragboard.getContent(portfolioAlternativeModel.getFormat());
            listView.getItems().add(alternative);
            dragCompleted = true;
        }

        event.setDropCompleted(dragCompleted);
        event.consume();
    }

    private void dragDone(DragEvent event, ListView<Alternative> listView) {
        TransferMode tm = event.getTransferMode();
        if (tm.equals(TransferMode.COPY) || tm.equals(TransferMode.MOVE)) {
            removeSelectedAlternative(listView);
        }

        event.consume();
    }


    private Alternative getSelectedAlternative(ListView<Alternative> listView) {
        return listView.getSelectionModel().getSelectedItem();
    }

    private void removeSelectedAlternative(ListView<Alternative> listView) {
        Alternative selectedAlternative = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().clearSelection();
        listView.getItems().remove(selectedAlternative);
        if (listView.getId().equals("alternativeListClient")) {
            log.info("[1/2] Aktie " + selectedAlternative + " wird dem Nutzer entzogen. Aus listView entfernen.");
            if (entityClient.removeAlternative(portfolioAlternativeModel.getClient(), selectedAlternative)) {
                portfolioAlternativeModel.getClientAlternatives().removeIf(clientAlternative ->
                        clientAlternative.getClient().getId() == portfolioAlternativeModel.getClient().getId()
                        && clientAlternative.getAlternative().getId() == selectedAlternative.getId());
                log.info("[2/2] Alt. Investment " + selectedAlternative + " wird dem Nutzer entzogen. Aus Model entfernen.");
                SnackBar snackBar = new SnackBar(pane);
                snackBar.show("Alt. Investment wurde dem Client entzogen!");
            }
        } else if (listView.getId().equals("alternativeList")) {
            double shareValue =
                    (selectedAlternative.getShare() / 100.0) * (portfolioAlternativeModel.getClient().getStrategyStocksTargetValue() / 100.0) * 100.0;
            ClientAlternative clientAlternative = new ClientAlternative(
                    new ClientAlternativeKey(portfolioAlternativeModel.getClient().getId(), selectedAlternative.getId()),
                    portfolioAlternativeModel.getClient(),
                    selectedAlternative,
                    0,
                    shareValue,
                    0,
                    0,
                    0
            );
            portfolioAlternativeModel.getClientAlternatives().add(clientAlternative);
            entityPortfolioAlternative.update(clientAlternative);
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Alt. Investment wurde dem Client hinzugefügt!");
        }
    }

    public void refresh() {
        portfolioAlternativeModel.getClientAlternatives().forEach(clientAlternative -> entityPortfolioAlternative.update(clientAlternative));
        SnackBar snackBar = new SnackBar(pane);
        snackBar.show("Alt. Investment wurde aktualisiert!");
    }
}

package controllers.investments.stocks.stock_audit_modal;

import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.investments.stocks.StockController;
import entities.stock.Stock;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.envers.RevisionType;
import sql.EntityStockImpl;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class StockAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private StockAuditModalModel stockAuditModalModel;
    private EntityStockImpl entityStock;
    private StockController stockController;
    private Stage stage;


    public StockAuditModalController() {
        this.stockAuditModalModel = new StockAuditModalModel();
        this.entityStock = new EntityStockImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            stockController = (StockController) stage.getUserData();
            stockAuditModalModel.setRevisions(
                    new ArrayList<>(
                            // TODO : stockController.getModel.getStock liefert null
                            entityStock.getAudit(stockController.getComboBox().getSelectionModel().getSelectedItem())
                    )
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            stockAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(),
                        stockToString(revision.getStock(), revision.getRevisionType()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            stockController.getLabel().setText("Übersicht");
            stage.close();
        });
    }

    private String stockToString(Stock stock, RevisionType revisionType) {
        String signum = "";
        if (stock.getChange() != null && stock.getChange().signum() != -1) {
            signum = "+ ";
        }

        if (revisionType.equals(RevisionType.ADD)) {
            return stock.getName() + " [" + stock.getSymbol() + ", " + stock.getCurrency() + "] wurde hinzugefügt: Preis = "
                    + NumberFormat.getCurrencyInstance().format(stock.getPrice()).replace("EUR", "EUR ")
                    + " ["  + signum
                    + NumberFormat.getCurrencyInstance().format(stock.getChange()).replace("EUR", "EUR ")  + "]";
        } else {
            return "Aktie " + stock.getName() + " [" + stock.getSymbol() + "] wurde aktualisiert: Preis = "
                    + NumberFormat.getCurrencyInstance().format(stock.getPrice()).replace("EUR", "EUR ")
                    + " ["  + signum
                    + NumberFormat.getCurrencyInstance().format(stock.getChange()).replace("EUR", "EUR ")  + "]";
        }
    }
}

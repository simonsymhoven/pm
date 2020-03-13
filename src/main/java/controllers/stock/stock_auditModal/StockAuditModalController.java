package controllers.stock.stock_auditModal;


import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.stock.StockController;
import entities.Stock;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sql.EntityStockImpl;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StockAuditModalController implements Initializable {
    @FXML
    public JFXButton close;
    @FXML
    public AnchorPane pane;
    @FXML
    public ScrollPane scrollPane;

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
                            entityStock.getAudit(stockController.comboBox.getSelectionModel().getSelectedItem())
                    )
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            stockAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(), stockToString(revision.getStock()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            stockController.label.setText("Übersicht");
            stage.close();
        });
    }

    private String stockToString(Stock stock) {
        String signum = "";
        if (stock.getChange().signum() == -1) {
        } else {
            signum = "+ ";
        }
        return stock.getName() + " [" + stock.getSymbol() + ", " + stock.getCurrency() + "]: Preis: "
                + NumberFormat.getCurrencyInstance().format(stock.getPrice()).replace("EUR", "EUR ")
                + " ["  + signum
                + NumberFormat.getCurrencyInstance().format(stock.getChange()).replace("EUR", "EUR ")  + "]";
    }
}

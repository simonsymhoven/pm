package controllers.stock.stock_auditModal;


import controllers.client.ClientController;
import controllers.stock.StockController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sql.EntityClientImpl;
import sql.EntityStockImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StockAuditModalController implements Initializable {
    @FXML
    public Label lblClose;
    @FXML
    public AnchorPane pane;

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
                            entityStock.getAudit(stockController.getStockModel().getStock())
                    )
            );
        });

        lblClose.setOnMouseClicked(e -> {
            stockController.label.setText("Übersicht");
            stockController.getAktien();
            stage.close();
        });


    }
}

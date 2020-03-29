package controllers.portfolio.stock.portfolio_stock_audit_modal;

import com.jfoenix.controls.JFXButton;
import controllers.Box;
import controllers.portfolio.stock.PortfolioStockController;
import entities.client.client_stock.ClientStock;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.envers.RevisionType;
import sql.EntityPortfolioStockImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PortfolioStockAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private PortfolioStockAuditModalModel portfolioStockAuditModalModel;
    private PortfolioStockController portfolioStockController;
    private EntityPortfolioStockImpl entityPortfolioStock;
    private Stage stage;

    public PortfolioStockAuditModalController() {
        this.entityPortfolioStock = new EntityPortfolioStockImpl();
        this.portfolioStockAuditModalModel = new PortfolioStockAuditModalModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            portfolioStockController = (PortfolioStockController) stage.getUserData();

                portfolioStockAuditModalModel.setRevisions(
                    new ArrayList<>(entityPortfolioStock.getAudit(portfolioStockController.getPortfolioStockModel().getClient()))
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            portfolioStockAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(),
                        clientStockToString(revision.getClientStock(), revision.getRevisionType()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> stage.close());

    }

    private String clientStockToString(ClientStock clientStock, RevisionType revisionType) {
        if (revisionType.equals(RevisionType.ADD)) {
            return clientStock.getStock().getName() + " wurde dem Portfolio hinzugefügt.";
        } else if (revisionType.equals(RevisionType.DEL)) {
            return clientStock.getStock().getName() + " wurde aus dem Portfolio entfernt.";
        } else {
            return "Die Anzahl für " + clientStock.getStock().getName() + " wurde bearbeitet: "
                    + clientStock.getQuantity() + " Stk.";
        }

    }

}

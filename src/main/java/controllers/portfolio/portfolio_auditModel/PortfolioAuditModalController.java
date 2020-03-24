package controllers.portfolio.portfolio_auditModel;


import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.portfolio.PortfolioController;
import entities.ClientStock;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.envers.RevisionType;
import sql.EntityPortfolioImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PortfolioAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private PortfolioAuditModalModel portfolioAuditModalModel;
    private PortfolioController portfolioController;
    private EntityPortfolioImpl entityPortfolio;
    private Stage stage;

    public PortfolioAuditModalController() {
        this.entityPortfolio = new EntityPortfolioImpl();
        this.portfolioAuditModalModel = new PortfolioAuditModalModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            portfolioController = (PortfolioController) stage.getUserData();

                portfolioAuditModalModel.setRevisions(
                    new ArrayList<>(entityPortfolio.getAudit(portfolioController.getPortfolioModel().getClient()))
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            portfolioAuditModalModel.getRevisions().forEach(revision -> {
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

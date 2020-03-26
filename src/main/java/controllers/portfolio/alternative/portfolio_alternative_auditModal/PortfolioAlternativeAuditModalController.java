package controllers.portfolio.alternative.portfolio_alternative_auditModal;

import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.portfolio.alternative.PortfolioAlternativeController;
import entities.client.clientAlternative.ClientAlternative;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.envers.RevisionType;
import sql.EntityPortfolioAlternativeImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PortfolioAlternativeAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private PortfolioAlternativeAuditModalModel portfolioAlternativeAuditModalModel;
    private PortfolioAlternativeController portfolioAlternativeController;
    private EntityPortfolioAlternativeImpl entityPortfolioAlternative;
    private Stage stage;

    public PortfolioAlternativeAuditModalController() {
        this.entityPortfolioAlternative = new EntityPortfolioAlternativeImpl();
        this.portfolioAlternativeAuditModalModel = new PortfolioAlternativeAuditModalModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            portfolioAlternativeController = (PortfolioAlternativeController) stage.getUserData();

                portfolioAlternativeAuditModalModel.setRevisions(
                    new ArrayList<>(entityPortfolioAlternative.getAudit(portfolioAlternativeController.getPortfolioAlternativeModel().getClient()))
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            portfolioAlternativeAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(),
                        clientAlternativeToString(revision.getClientAlternative(), revision.getRevisionType()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> stage.close());

    }

    private String clientAlternativeToString(ClientAlternative clientAlternative, RevisionType revisionType) {
        if (revisionType.equals(RevisionType.ADD)) {
            return clientAlternative.getAlternative().getName() + " wurde dem Portfolio hinzugefügt.";
        } else if (revisionType.equals(RevisionType.DEL)) {
            return clientAlternative.getAlternative().getName() + " wurde aus dem Portfolio entfernt.";
        } else {
            return "Die Anzahl für " + clientAlternative.getAlternative().getName() + " wurde bearbeitet: "
                    + clientAlternative.getQuantity() + " Stk.";
        }

    }

}

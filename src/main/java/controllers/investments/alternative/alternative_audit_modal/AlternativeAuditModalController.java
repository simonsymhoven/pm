package controllers.investments.alternative.alternative_audit_modal;

import com.jfoenix.controls.JFXButton;
import controllers.Box;
import controllers.investments.alternative.AlternativeController;
import entities.alternative.Alternative;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.hibernate.envers.RevisionType;
import sql.EntityAlternativeImpl;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Log4j2
public class AlternativeAuditModalController implements Initializable {
    @FXML
    private JFXButton close;
    @FXML
    private AnchorPane pane;
    @FXML
    private ScrollPane scrollPane;

    private AlternativeAuditModalModel alternativeAuditModalModel;
    private EntityAlternativeImpl entityAlternative;
    private AlternativeController alternativeController;
    private Stage stage;


    public AlternativeAuditModalController() {
        this.alternativeAuditModalModel = new AlternativeAuditModalModel();
        this.entityAlternative = new EntityAlternativeImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) pane.getScene().getWindow();
            alternativeController = (AlternativeController) stage.getUserData();
            log.info("Alternative aus alternativeController: " + alternativeController.getAlternativeModel().getAlternative());
            alternativeAuditModalModel.setRevisions(
                    new ArrayList<>(
                            // TODO : alternativeController.getModel.getStock liefert null
                            entityAlternative.getAudit(alternativeController.getComboBox().getSelectionModel().getSelectedItem())
                    )
            );

            VBox vBox = new VBox();
            vBox.setPrefWidth(550);

            Box box = new Box();
            alternativeAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(),
                        stockToString(revision.getAlternative(), revision.getRevisionType()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            alternativeController.getLabel().setText("Übersicht");
            stage.close();
        });
    }

    private String stockToString(Alternative alternative, RevisionType revisionType) {
        String signum = "";
        if (alternative.getChange() != null && alternative.getChange().signum() != -1) {
            signum = "+ ";
        }

        if (revisionType.equals(RevisionType.ADD)) {
            return alternative.getName() + " [" + alternative.getSymbol() + ", " + alternative.getCurrency() + "] wurde hinzugefügt: Preis = "
                    + NumberFormat.getCurrencyInstance().format(alternative.getPrice()).replace("EUR", "EUR ")
                    + " ["  + signum
                    + NumberFormat.getCurrencyInstance().format(alternative.getChange()).replace("EUR", "EUR ")  + "]";
        } else {
            return "Alt. Investment " + alternative.getName() + " [" + alternative.getSymbol() + "] wurde aktualisiert: Preis = "
                    + NumberFormat.getCurrencyInstance().format(alternative.getPrice()).replace("EUR", "EUR ")
                    + " ["  + signum
                    + NumberFormat.getCurrencyInstance().format(alternative.getChange()).replace("EUR", "EUR ")  + "]";
        }
    }
}

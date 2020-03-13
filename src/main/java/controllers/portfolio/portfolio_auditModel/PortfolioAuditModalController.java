package controllers.portfolio.portfolio_auditModel;


import com.jfoenix.controls.JFXButton;
import controllers.client.Box;
import controllers.portfolio.PortfolioController;
import entities.ClientStock;
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
import sql.EntityPortfolioImpl;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PortfolioAuditModalController implements Initializable {
    @FXML
    public JFXButton close;
    @FXML
    public AnchorPane pane;
    @FXML
    public ScrollPane scrollPane;

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
                HBox hbox = box.generateAuditHBox(revision.getRevisionType(), revision.getRevisionDate(), clientStockToString(revision.getClientStock()));
                vBox.getChildren().add(hbox);
            });

            scrollPane.setContent(vBox);
        });

        close.setOnMouseClicked(e -> {
            stage.close();
        });


    }

    private String clientStockToString(ClientStock clientStock) {
        return clientStock.getStock().getName();
    }

}

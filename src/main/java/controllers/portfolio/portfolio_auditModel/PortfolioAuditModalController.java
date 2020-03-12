package controllers.portfolio.portfolio_auditModel;


import com.jfoenix.controls.JFXButton;
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
            portfolioAuditModalModel.getRevisions().forEach(revision -> {
                HBox hbox = new HBox();
                hbox.setPrefSize(550, 70);
                hbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                                + "-fx-border-width: 0.5;" + "-fx-border-insets: 5;"
                                + "-fx-border-radius: 5;" + "-fx-border-color: #343f4a;");
                Image img;

                switch(revision.getRevisionType()) {
                    case ADD:
                        img = new Image(getClass().getResourceAsStream("/icons/plus(1).png"));
                        break;
                    case DEL:
                        img = new Image(getClass().getResourceAsStream("/icons/error.png"));
                        break;
                    case MOD:
                        img = new Image(getClass().getResourceAsStream("/icons/shuffle.png"));
                        break;
                    default:
                        img = new Image(getClass().getResourceAsStream("/icons/question.png"));
                        break;
                }

                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(70);
                imageView.setFitHeight(70);

                VBox dateChangeBox = new VBox();
                dateChangeBox.setPadding(new Insets(20,5,5,20));
                dateChangeBox.setPrefSize(480,70);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm");
                HBox date = new HBox();
                date.setPrefSize(480, 35);
                Label lDate = new Label();
                String sdate =  simpleDateFormat.format(revision.getRevisionDate());
                sdate = sdate.replace(" ", ", um ");
                lDate.setText("Am " + sdate + " Uhr wurde folgende Änderung durchgeführt:");
                date.getChildren().add(lDate);

                HBox change = new HBox();
                change.setPrefSize(480, 35);
                Label lChange = new Label();
                lChange.setText(clientStockToString(revision.getClientStock()));
                change.getChildren().add(lChange);

                dateChangeBox.getChildren().addAll(date, change);

                hbox.getChildren().addAll(imageView, dateChangeBox);
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

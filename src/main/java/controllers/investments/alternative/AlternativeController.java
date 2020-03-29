package controllers.investments.alternative;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import entities.alternative.Alternative;
import entities.client.client_alternative.ClientAlternative;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityAlternativeImpl;
import sql.EntityPortfolioAlternativeImpl;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Log4j2
public class AlternativeController implements Initializable {

    private double x = 0;
    private double y = 0;
    @FXML
    @Getter
    private JFXComboBox<Alternative> comboBox;
    @FXML
    @Getter
    private Label label;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField exchange;
    @FXML
    private JFXTextField price;
    @FXML
    private JFXTextField change;
    @FXML
    private JFXTextField currency;
    @FXML
    private ImageView imgView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private JFXButton addAlternative;
    @FXML
    private JFXButton deleteAlternative;
    @FXML
    private JFXButton showAudit;
    @FXML
    private Label errorLabel;
    @FXML
    @Getter
    private AnchorPane pane;
    @Getter
    private AlternativeModel alternativeModel;
    private EntityAlternativeImpl entityAlternative;
    private EntityPortfolioAlternativeImpl entityPortfolioAlternative;

    public AlternativeController() {
        Locale.setDefault(Locale.GERMANY);
        this.alternativeModel = new AlternativeModel();
        this.entityAlternative = new EntityAlternativeImpl();
        this.entityPortfolioAlternative = new EntityPortfolioAlternativeImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAudit.disableProperty().bind(comboBox.valueProperty().isNull());
        deleteAlternative.setDisable(true);
        getAlternatives();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            deleteAlternative.setDisable(false);
            if (newValue != null) {

                List<ClientAlternative> list =  entityPortfolioAlternative.getAllForStock(newValue);
                if (list.size() > 0) {
                    deleteAlternative.setDisable(true);
                }
                alternativeModel.setAlternative(newValue);
                fillFields();
                plotAlternative();
            } else {
                clearFields();
                imgView.setImage(null);
            }
        });
    }


    public void viewAudit() {
        try {
            Stage dialog = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/views/investments/alternative/alternative_audit_modal.fxml"));
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
    }

    public void delete() {
        if (entityAlternative.delete(comboBox.getSelectionModel().getSelectedItem())) {
            comboBox.getItems().remove(comboBox.getSelectionModel().getSelectedItem());
            comboBox.getSelectionModel().clearSelection();
            imgView.setImage(null);
            getAlternatives();
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Alternatives Investment wurde erfolgreich gelöscht!");
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Alternatives Investment  nicht gelöscht werden!");
        }
    }

    public void add() {
        try {
            Stage dialog = new Stage();
            Parent root = FXMLLoader.load(getClass()
                    .getResource("/views/investments/alternative/alternative_add_modal.fxml"));
            root.setOnMousePressed(event -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                dialog.setX(event.getScreenX() - x);
                dialog.setY(event.getScreenY() - y);
            });
            dialog.setScene(new Scene(root));
            dialog.setUserData(this);
            dialog.initOwner(addAlternative.getScene().getWindow());
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    private void fillFields() {
        currency.setText(alternativeModel.getAlternative().getCurrency());
        label.setText(alternativeModel.getAlternative().getName());
        name.setText(alternativeModel.getAlternative().getName());
        symbol.setText(alternativeModel.getAlternative().getSymbol());
        exchange.setText(alternativeModel.getAlternative().getExchange());
        price.setText(NumberFormat.getCurrencyInstance()
                .format(alternativeModel.getAlternative().getPrice()));
        change.setText(NumberFormat.getCurrencyInstance()
                .format(alternativeModel.getAlternative().getChange()));
    }

    private void plotAlternative() {
        Plot task = new Plot(alternativeModel);
        errorLabel.setVisible(false);

        task.setOnRunning(successesEvent -> {
                pane.setDisable(true);
                progressIndicator.setVisible(true);
        });

        task.setOnSucceeded(succeededEvent -> {
            try {
                if (task.get() != null) {
                    imgView.setImage(task.get());
                }
            } catch (Exception e) {
                log.error(e);
            }
            progressIndicator.setVisible(false);
            pane.setDisable(false);
        });

        task.setOnFailed(failedEvent -> {
            progressIndicator.setVisible(false);
            pane.setDisable(false);
            errorLabel.setVisible(true);
            log.error(" TASK FAILED! ");
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    public void getAlternatives() {
        alternativeModel.setAlternatives(entityAlternative.getAll());
        alternativeModel.getAlternatives().forEach(c -> {
            if (!comboBox.getItems().contains(c)) {
                comboBox.getItems().add(c);
            }
        });
    }

    private void clearFields() {
        pane.getChildren()
                .filtered(node -> node instanceof JFXTextField)
                .forEach(node -> ((JFXTextField) node).clear());
    }

}

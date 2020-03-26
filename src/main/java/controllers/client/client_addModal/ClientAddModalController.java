package controllers.client.client_addModal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import entities.client.Client;
import entities.investment.InvestmentStrategy;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityClientImpl;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class ClientAddModalController implements Initializable {
    @FXML
    private AnchorPane directivePane;
    @FXML
    private JFXButton addClient;
    @FXML
    private JFXButton close;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField symbol;
    @FXML
    private JFXTextField strategyStocksLowerLimit;
    @FXML
    private JFXTextField strategyStocksTargetValue;
    @FXML
    private JFXTextField strategyStocksUpperLimit;
    @FXML
    private JFXTextField strategyAlternativeLowerLimit;
    @FXML
    private JFXTextField strategyAlternativeTargetValue;
    @FXML
    private JFXTextField strategyAlternativeUpperLimit;
    @FXML
    private JFXTextField strategyIoanLowerLimit;
    @FXML
    private JFXTextField strategyIoanTargetValue;
    @FXML
    private JFXTextField strategyIoanUpperLimit;
    @FXML
    private JFXTextField strategyLiquidityLowerLimit;
    @FXML
    private JFXTextField strategyLiquidityTargetValue;
    @FXML
    private JFXTextField strategyLiquidityUpperLimit;
    @FXML
    private JFXTextField depoValue;
    @FXML
    private JFXTextArea comment;
    @FXML
    private AnchorPane pane;
    @FXML
    private Label info;

    private Stage stage;
    private ClientController clientController;
    private ClientAddModalModel clientAddModalModel;
    private EntityClientImpl entityClient;
    private String regex = "^(([1-9][0-9]*)|0)?(\\.[0-9]*)?";
    public ClientAddModalController() {
        this.clientAddModalModel = new ClientAddModalModel();
        this.entityClient = new EntityClientImpl();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) addClient.getScene().getWindow();
            clientController = (ClientController) stage.getUserData();
        });

        close.setOnMouseClicked(e -> {
            clientController.getLabel().setText("Übersicht");
            stage.close();
        });

        directivePane.visibleProperty().bind(name.textProperty().isNotEmpty()
                .and(symbol.textProperty().isNotEmpty())
                .and(depoValue.textProperty().isNotEmpty())
        );

        comment.textProperty().addListener((observable, old, newValue) -> {
            clientAddModalModel.setComment(newValue);
        });

        name.textProperty().addListener((observable, old, newValue) -> {
            clientAddModalModel.setName(newValue);
            info.setText("Welche Strategie soll für " + newValue + " angewendet werden?");
        });

        symbol.textProperty().addListener((observable, old, newValue) -> {
            clientAddModalModel.setSymbol(newValue);
        });

        addClient.disableProperty().bind(name.textProperty().isEmpty()
                .or(symbol.textProperty().isEmpty()
                .or(strategyStocksLowerLimit.textProperty().isEmpty())
                .or(strategyStocksTargetValue.textProperty().isEmpty())
                .or(strategyStocksUpperLimit.textProperty().isEmpty())
                .or(strategyAlternativeLowerLimit.textProperty().isEmpty())
                .or(strategyAlternativeTargetValue.textProperty().isEmpty())
                .or(strategyAlternativeUpperLimit.textProperty().isEmpty())
                .or(strategyIoanLowerLimit.textProperty().isEmpty())
                .or(strategyIoanTargetValue.textProperty().isEmpty())
                .or(strategyIoanUpperLimit.textProperty().isEmpty())
                .or(strategyLiquidityLowerLimit.textProperty().isEmpty())
                .or(strategyLiquidityTargetValue.textProperty().isEmpty())
                .or(strategyLiquidityUpperLimit.textProperty().isEmpty())
                .or(depoValue.textProperty().isEmpty())
        ));

        depoValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    depoValue.setText(oldValue);
                } else {
                    clientAddModalModel.setDepoValue(BigDecimal.valueOf(Double.parseDouble(newValue)));
                }
            }
        });

        textPropertyStockInvestments();
        textPropertyAlternativeInvestments();
        textPropertyLiquidityInvestments();
        textPropertyIoanInvestments();


    }

    private void textPropertyLiquidityInvestments() {
        strategyLiquidityLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getLiquidityInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getLiquidityInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getLiquidityInvestment().setUpperLimit(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyStockInvestments() {
        strategyStocksLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getStockInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getStockInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getStockInvestment().setUpperLimit(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyAlternativeInvestments() {
        strategyAlternativeLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getAlternativeInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getAlternativeInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getAlternativeInvestment().setUpperLimit(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyIoanInvestments() {
        strategyIoanLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getIoanInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getIoanInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getIoanInvestment().setUpperLimit(Double.parseDouble(newValue));
                }
            }
        });
    }

    public void add() {

        InvestmentStrategy investmentStrategy = new InvestmentStrategy(
                clientAddModalModel.getStockInvestment(),
                clientAddModalModel.getAlternativeInvestment(),
                clientAddModalModel.getIoanInvestment(),
                clientAddModalModel.getLiquidityInvestment()
        );

        Client client = new Client(
            name.getText(),
            symbol.getText(),
            investmentStrategy,
            clientAddModalModel.getDepoValue(),
            clientAddModalModel.getComment()
        );


        if (entityClient.add(client)) {
            clientController.getClients();
            clientController.getComboBox().getSelectionModel().select(client);
            SnackBar snackBar = new SnackBar(clientController.getPane());
            snackBar.show("Client wurde erfolgreich hinzugefügt!");
            stage.close();
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Client konnte nicht hinzugefügt werden!");
        }
    }
}

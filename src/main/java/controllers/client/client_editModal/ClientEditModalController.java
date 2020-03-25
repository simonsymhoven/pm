package controllers.client.client_editModal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import entities.client.Client;
import entities.client.investement.InvestmentStrategy;
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
public class ClientEditModalController implements Initializable {
    @FXML
    private AnchorPane directivePane;
    @FXML
    private JFXButton editClient;
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
    private ClientEditModalModel clientEditModalModel;
    private EntityClientImpl entityClient;
    private String regex = "^(([1-9][0-9]*)|0)?(\\.[0-9]*)?";

    public ClientEditModalController() {
        this.clientEditModalModel = new ClientEditModalModel();
        this.entityClient = new EntityClientImpl();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) editClient.getScene().getWindow();
            clientController = (ClientController) stage.getUserData();
            clientEditModalModel.setClient(clientController.getClientModel().getClient());
            name.setText(clientEditModalModel.getClient().getName());
            symbol.setText(clientEditModalModel.getClient().getSymbol());
            strategyAlternativeLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyAlternativeLowerLimit()));
            strategyAlternativeTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategyAlternativeTargetValue()));
            strategyAlternativeUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyAlternativeUpperLimit()));
            strategyIoanLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyIoanLowerLimit()));
            strategyIoanTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategyIoanTargetValue()));
            strategyIoanUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyIoanUpperLimit()));
            strategyStocksLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyStocksLowerLimit()));
            strategyStocksTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategyStocksTargetValue()));
            strategyStocksUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyStocksUpperLimit()));
            strategyLiquidityLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyLiquidityLowerLimit()));
            strategyLiquidityTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategyLiquidityTargetValue()));
            strategyLiquidityUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategyLiquidityUpperLimit()));
            depoValue.setText(String.valueOf(clientEditModalModel.getClient().getDepoValue()));
            comment.setText(clientEditModalModel.getClient().getComment());
        });

        close.setOnMouseClicked(e -> {
            clientController.getLabel().setText(clientEditModalModel.getClient().getName());
            stage.close();
        });

        comment.textProperty().addListener((observable, old, newValue) -> clientEditModalModel.setComment(newValue));

        name.textProperty().addListener((observable, old, newValue) -> {
            clientEditModalModel.setName(newValue);
            info.setText("Welche Strategie soll für " + newValue + " angewendet werden?");
        });

        symbol.textProperty().addListener((observable, old, newValue) -> {
            clientEditModalModel.setSymbol(newValue);
        });

        editClient.disableProperty().bind(name.textProperty().isEmpty()
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
                    clientEditModalModel.setDepoValue(BigDecimal.valueOf(Double.parseDouble(newValue)));
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
                    clientEditModalModel.getLiquidityInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getLiquidityInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getLiquidityInvestment().setUpperLimit(Double.parseDouble(newValue));
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
                    clientEditModalModel.getStockInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getStockInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getStockInvestment().setUpperLimit(Double.parseDouble(newValue));
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
                    clientEditModalModel.getAlternativeInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getAlternativeInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getAlternativeInvestment().setUpperLimit(Double.parseDouble(newValue));
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
                    clientEditModalModel.getIoanInvestment().setLowerLimit(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getIoanInvestment().setTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getIoanInvestment().setUpperLimit(Double.parseDouble(newValue));
                }
            }
        });
    }

    public void edit() {
        InvestmentStrategy investmentStrategy = new InvestmentStrategy(
                clientEditModalModel.getStockInvestment(),
                clientEditModalModel.getAlternativeInvestment(),
                clientEditModalModel.getIoanInvestment(),
                clientEditModalModel.getLiquidityInvestment()
        );

        Client client = new Client(
            name.getText(),
            symbol.getText(),
            investmentStrategy,
            clientEditModalModel.getDepoValue(),
            clientEditModalModel.getComment()
        );

        client.setId(clientEditModalModel.getClient().getId());

        if (entityClient.update(client)) {
            clientController.getComboBox().getItems().remove(clientEditModalModel.getClient());
            clientController.getClients();
            clientController.getComboBox().getSelectionModel().select(client);
            SnackBar snackBar = new SnackBar(clientController.getPane());
            snackBar.show("Client wurde erfolgreich bearbeitet!");
            stage.close();
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Client konnte nicht bearbeitet werden!");
        }
    }
}

package controllers.client.client_editModal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import snackbar.SnackBar;
import sql.EntityClientImpl;
import sql.EntityPortfolioStockImpl;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class ClientEditModalController implements Initializable {
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
    private JFXTextField capital;
    @FXML
    private JFXTextArea comment;
    @FXML
    private AnchorPane pane;

    private Stage stage;
    private ClientController clientController;
    private ClientEditModalModel clientEditModalModel;
    private EntityPortfolioStockImpl entityPortfolioStock;
    private EntityClientImpl entityClient;
    private String regex = "^(([1-9][0-9]*)|0)?(\\.[0-9]*)?";

    public ClientEditModalController() {
        this.clientEditModalModel = new ClientEditModalModel();
        this.entityClient = new EntityClientImpl();
        this.entityPortfolioStock = new EntityPortfolioStockImpl();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            stage = (Stage) editClient.getScene().getWindow();
            clientController = (ClientController) stage.getUserData();
            clientEditModalModel.setClient(clientController.getClientModel().getClient());
            name.setText(clientEditModalModel.getClient().getName());
            symbol.setText(clientEditModalModel.getClient().getSymbol());
            strategyAlternativeLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getAltLower()));
            strategyAlternativeTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getAltTarget()));
            strategyAlternativeUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getAltUpper()));
            strategyIoanLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getIoanLower()));
            strategyIoanTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getIoanTarget()));
            strategyIoanUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getIoanUpper()));
            strategyStocksLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getStockLower()));
            strategyStocksTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getStockTarget()));
            strategyStocksUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getStockUpper()));
            strategyLiquidityLowerLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getLiquidityLower()));
            strategyLiquidityTargetValue.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getLiquidityTarget()));
            strategyLiquidityUpperLimit.setText(String.valueOf(clientEditModalModel.getClient().getStrategy().getLiquidityUpper()));
            capital.setText(String.valueOf(clientEditModalModel.getClient().getCapital()));
            comment.setText(clientEditModalModel.getClient().getComment());
        });

        close.setOnMouseClicked(e -> {
            clientController.getLabel().setText(clientEditModalModel.getClient().getName());
            stage.close();
        });

        comment.textProperty().addListener((observable, old, newValue) -> clientEditModalModel.getClient().setComment(newValue));

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
                .or(capital.textProperty().isEmpty())
        ));

        capital.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    capital.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().setCapital(BigDecimal.valueOf(Double.parseDouble(newValue)));
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
                    clientEditModalModel.getClient().getStrategy().setLiquidityLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setLiquidityTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setLiquidityUpper(Double.parseDouble(newValue));
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
                    clientEditModalModel.getClient().getStrategy().setStockLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setStockTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyStocksUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setStockUpper(Double.parseDouble(newValue));
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
                    clientEditModalModel.getClient().getStrategy().setAltLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setAltTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setAltUpper(Double.parseDouble(newValue));
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
                    clientEditModalModel.getClient().getStrategy().setIoanLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanTargetValue.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setIoanTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                if (!newValue.matches(regex)) {
                    strategyIoanUpperLimit.setText(oldValue);
                } else {
                    clientEditModalModel.getClient().getStrategy().setIoanUpper(Double.parseDouble(newValue));
                }
            }
        });
    }

    public void edit() {
        if (entityClient.update(clientEditModalModel.getClient())) {
            clientController.getComboBox().getItems().remove(clientEditModalModel.getClient());
            clientController.getClients();
            clientController.getComboBox().getSelectionModel().select(clientEditModalModel.getClient());
            entityPortfolioStock.getAll(clientEditModalModel.getClient()).forEach(clientStock -> {
                clientStock.calculate();
                entityPortfolioStock.update(clientStock);
            });
            SnackBar snackBar = new SnackBar(clientController.getPane());
            snackBar.show("Client wurde erfolgreich bearbeitet!");
            stage.close();
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Client konnte nicht bearbeitet werden!");
        }
    }
}

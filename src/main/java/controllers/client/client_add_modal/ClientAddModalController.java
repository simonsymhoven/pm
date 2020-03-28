package controllers.client.client_add_modal;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controllers.client.ClientController;
import entities.client.Client;
import entities.investment.Strategy;
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
    private JFXTextField capital;
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
        clientAddModalModel.setClient(new Client());
        clientAddModalModel.getClient().setStrategy(new Strategy());
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
                .and(capital.textProperty().isNotEmpty())
        );

        comment.textProperty().addListener((observable, old, newValue)
                -> clientAddModalModel.getClient().setComment(newValue)
        );

        name.textProperty().addListener((observable, old, newValue) -> {
            clientAddModalModel.getClient().setName(newValue);
            info.setText("Welche Strategie soll für " + newValue + " angewendet werden?");
        });

        symbol.textProperty().addListener((observable, old, newValue)
                -> clientAddModalModel.getClient().setSymbol(newValue)
        );

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
                .or(capital.textProperty().isEmpty())
        ));

        capital.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    capital.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().setCapital(BigDecimal.valueOf(Double.parseDouble(newValue)));
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
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setLiquidityLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setLiquidityTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyLiquidityUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyLiquidityUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setLiquidityUpper(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyStockInvestments() {
        strategyStocksLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyStocksLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setStockLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyStocksTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setStockTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyStocksUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyStocksUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setStockUpper(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyAlternativeInvestments() {
        strategyAlternativeLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setAltLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setAltTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyAlternativeUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyAlternativeUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setAltUpper(Double.parseDouble(newValue));
                }
            }
        });
    }

    private void textPropertyIoanInvestments() {
        strategyIoanLowerLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyIoanLowerLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setIoanLower(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanTargetValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyIoanTargetValue.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setIoanTarget(Double.parseDouble(newValue));
                }
            }
        });

        strategyIoanUpperLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!"".equals(newValue)) {
                if (!newValue.matches(regex)) {
                    strategyIoanUpperLimit.setText(oldValue);
                } else {
                    clientAddModalModel.getClient().getStrategy().setIoanUpper(Double.parseDouble(newValue));
                }
            }
        });
    }

    public void add() {
        if (entityClient.add(clientAddModalModel.getClient())) {
            clientController.getClients();
            clientController.getComboBox().getSelectionModel().select(clientAddModalModel.getClient());
            SnackBar snackBar = new SnackBar(clientController.getPane());
            snackBar.show("Client wurde erfolgreich hinzugefügt!");
            stage.close();
        } else {
            SnackBar snackBar = new SnackBar(pane);
            snackBar.show("Client konnte nicht hinzugefügt werden!");
        }
    }
}

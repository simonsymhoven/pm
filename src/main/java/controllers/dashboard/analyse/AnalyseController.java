package controllers.dashboard.analyse;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import lombok.extern.log4j.Log4j2;
import sql.EntityClientImpl;
import sql.EntityPortfolioAlternativeImpl;
import sql.EntityPortfolioStockImpl;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class AnalyseController implements Initializable {

    @FXML
    private JFXTreeTableView<TableItem> treeView;
    @FXML
    private JFXTextField input;

    private EntityClientImpl entityClient;
    private EntityPortfolioStockImpl entityPortfolioStock;
    private EntityPortfolioAlternativeImpl entityPortfolioAlternative;

    public AnalyseController() {
        this.entityClient = new EntityClientImpl();
        this.entityPortfolioStock = new EntityPortfolioStockImpl();
        this.entityPortfolioAlternative = new EntityPortfolioAlternativeImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        input.textProperty().addListener((observableValue, oldValue, newValue) ->
                treeView.setPredicate(tableItemTreeItem ->
                        tableItemTreeItem.getValue().getClientName().getValue().contains(newValue)));

        JFXTreeTableColumn<TableItem, String> nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getClientName());

        JFXTreeTableColumn<TableItem, String> depoValueCol = new JFXTreeTableColumn<>("Gesamtvermögen");
        depoValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getDepoValue());


        JFXTreeTableColumn<TableItem, String> headerStockCol = new JFXTreeTableColumn<>("Aktien");
        JFXTreeTableColumn<TableItem, String> stockValueCol = new JFXTreeTableColumn<>("Summe");
        stockValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockValue());
        JFXTreeTableColumn<TableItem, String> stockShareCol = new JFXTreeTableColumn<>("Ist");
        stockShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockShare());
        JFXTreeTableColumn<TableItem, String> stockStrategyCol = new JFXTreeTableColumn<>("Soll");
        stockStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockStrategy());
        headerStockCol.getColumns().addAll(stockValueCol, stockShareCol, stockStrategyCol);

        JFXTreeTableColumn<TableItem, String> headerAlternativeCol = new JFXTreeTableColumn<>("Alt. Investments");
        JFXTreeTableColumn<TableItem, String> alternativeValueCol = new JFXTreeTableColumn<>("Summe");
        alternativeValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeValue());
        JFXTreeTableColumn<TableItem, String> alternativeShareCol = new JFXTreeTableColumn<>("Ist");
        alternativeShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeShare());
        JFXTreeTableColumn<TableItem, String> alternativeStrategyCol = new JFXTreeTableColumn<>("Soll");
        alternativeStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeStrategy());
        headerAlternativeCol.getColumns().addAll(alternativeValueCol, alternativeShareCol, alternativeStrategyCol);

        JFXTreeTableColumn<TableItem, String> headerIoanCol = new JFXTreeTableColumn<>("Anleihen");
        JFXTreeTableColumn<TableItem, String> ioanValueCol = new JFXTreeTableColumn<>("Summe");
        ioanValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanValue());
        JFXTreeTableColumn<TableItem, String> ioanShareCol = new JFXTreeTableColumn<>("Ist");
        ioanShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanShare());
        JFXTreeTableColumn<TableItem, String> ioanStrategyCol = new JFXTreeTableColumn<>("Soll");
        ioanStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanStrategy());
        headerIoanCol.getColumns().addAll(ioanValueCol, ioanShareCol, ioanStrategyCol);

        JFXTreeTableColumn<TableItem, String> headerLiquidityCol = new JFXTreeTableColumn<>("Liquidität");
        JFXTreeTableColumn<TableItem, String> liquidityValueCol = new JFXTreeTableColumn<>("Summe");
        liquidityValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityValue());
        JFXTreeTableColumn<TableItem, String> liquidityShareCol = new JFXTreeTableColumn<>("Ist");
        liquidityShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityShare());
        JFXTreeTableColumn<TableItem, String> liquidityStrategyCol = new JFXTreeTableColumn<>("Soll");
        liquidityStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityStrategy());
        headerLiquidityCol.getColumns().addAll(liquidityValueCol, liquidityShareCol, liquidityStrategyCol);

        ObservableList<TableItem> tableItems = FXCollections.observableArrayList();

        entityClient.getAll().forEach(client -> {
            double stockValue = entityPortfolioStock.getAll(client)
                    .stream()
                    .mapToDouble(clientStock -> clientStock.getQuantity() * clientStock.getStock().getPrice().doubleValue())
                    .sum();

            double alternativeValue = entityPortfolioAlternative.getAll(client)
                    .stream()
                    .mapToDouble(clientAlternative -> clientAlternative.getQuantity() * clientAlternative.getAlternative().getPrice().doubleValue())
                    .sum();
           double ioanValue = 0;
           double liquidityValue = client.getCapital().doubleValue() - stockValue - alternativeValue - ioanValue;

            tableItems.add(
                    new TableItem(
                            ("[" + client.getSymbol() + "] " + client.getName()),
                            client.getCapital(),
                            client.getStrategy(),
                            stockValue,
                            alternativeValue,
                            ioanValue,
                            liquidityValue
                    )
            );
        });

        final TreeItem<TableItem> root = new RecursiveTreeItem<>(tableItems, RecursiveTreeObject::getChildren);
        treeView.getColumns().setAll(
                nameCol,
                depoValueCol,
                headerStockCol,
                headerAlternativeCol,
                headerIoanCol,
                headerLiquidityCol
        );
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        createCellFacotryStock(stockShareCol);
        createCellFactoryAlternative(alternativeShareCol);
        crateCellFactoryIoan(ioanShareCol);
        creatCellFacotryLiquidity(liquidityShareCol);
    }

    private void creatCellFacotryLiquidity(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); //This is mandatory

                if (item == null || empty) { //If the cell is empty
                    setText(null);
                    setStyle("");
                } else { //If the cell is not empty
                    setText(item); //Put the String data in the cell
                    //We get here all the info of the Person of this row
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());

                    if (tableItem.getValue().getLiquidityShare().getValue().equals(tableItem.getValue().getLiquidityStrategy().getValue())) {
                        setStyle("-fx-background-color: #BFFF00");
                    } else { //The text in red
                        setStyle("-fx-background-color: #ff4646"); //The background of the cell in yellow
                    }
                }
            }
        });
    }

    private void crateCellFactoryIoan(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); //This is mandatory

                if (item == null || empty) { //If the cell is empty
                    setText(null);
                    setStyle("");
                } else { //If the cell is not empty
                    setText(item); //Put the String data in the cell
                    //We get here all the info of the Person of this row
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());

                    if (tableItem.getValue().getIoanShare().getValue().equals(tableItem.getValue().getIoanStrategy().getValue())) {
                        setStyle("-fx-background-color: #BFFF00");
                    } else { //The text in red
                        setStyle("-fx-background-color: #ff4646");
                    }
                }
            }
        });
    }

    private void createCellFactoryAlternative(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); //This is mandatory

                if (item == null || empty) { //If the cell is empty
                    setText(null);
                    setStyle("");
                } else { //If the cell is not empty
                    setText(item); //Put the String data in the cell
                    //We get here all the info of the Person of this row
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());

                    if (tableItem.getValue().getAlternativeShare().getValue().equals(tableItem.getValue().getAlternativeStrategy().getValue())) {
                        setStyle("-fx-background-color: #BFFF00");
                    } else { //The text in red
                        setStyle("-fx-background-color: #ff4646"); //The background of the cell in yellow
                    }
                }
            }
        });
    }

    private void createCellFacotryStock(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); //This is mandatory

                if (item == null || empty) { //If the cell is empty
                    setText(null);
                    setStyle("");
                } else { //If the cell is not empty
                    setText(item); //Put the String data in the cell
                    //We get here all the info of the Person of this row
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());

                    if (tableItem.getValue().getStockShare().getValue().equals(tableItem.getValue().getStockStrategy().getValue())) {
                        setStyle("-fx-background-color: #BFFF00");
                    } else { //The text in red
                        setStyle("-fx-background-color: #ff4646"); //The background of the cell in yellow
                    }
                }
            }
        });
    }
}

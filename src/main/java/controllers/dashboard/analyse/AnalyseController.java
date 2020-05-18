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

    private JFXTreeTableColumn<TableItem, String> nameCol = new JFXTreeTableColumn<>("Name");
    private JFXTreeTableColumn<TableItem, String> depoValueCol = new JFXTreeTableColumn<>("Gesamtvermögen");
    private JFXTreeTableColumn<TableItem, String> headerStockCol = new JFXTreeTableColumn<>("Aktien");
    private JFXTreeTableColumn<TableItem, String> stockValueCol = new JFXTreeTableColumn<>("Summe");
    private JFXTreeTableColumn<TableItem, String> stockShareCol = new JFXTreeTableColumn<>("Ist");
    private JFXTreeTableColumn<TableItem, String> stockStrategyCol = new JFXTreeTableColumn<>("Soll");
    private JFXTreeTableColumn<TableItem, String> headerAlternativeCol = new JFXTreeTableColumn<>("Alt. Investments");
    private JFXTreeTableColumn<TableItem, String> alternativeValueCol = new JFXTreeTableColumn<>("Summe");
    private JFXTreeTableColumn<TableItem, String> alternativeShareCol = new JFXTreeTableColumn<>("Ist");
    private JFXTreeTableColumn<TableItem, String> alternativeStrategyCol = new JFXTreeTableColumn<>("Soll");
    private JFXTreeTableColumn<TableItem, String> headerIoanCol = new JFXTreeTableColumn<>("Anleihen");
    private JFXTreeTableColumn<TableItem, String> ioanValueCol = new JFXTreeTableColumn<>("Summe");
    private JFXTreeTableColumn<TableItem, String> ioanShareCol = new JFXTreeTableColumn<>("Ist");
    private JFXTreeTableColumn<TableItem, String> ioanStrategyCol = new JFXTreeTableColumn<>("Soll");
    private JFXTreeTableColumn<TableItem, String> headerLiquidityCol = new JFXTreeTableColumn<>("Liquidität");
    private JFXTreeTableColumn<TableItem, String> liquidityValueCol = new JFXTreeTableColumn<>("Summe");
    private JFXTreeTableColumn<TableItem, String> liquidityShareCol = new JFXTreeTableColumn<>("Ist");
    private JFXTreeTableColumn<TableItem, String> liquidityStrategyCol = new JFXTreeTableColumn<>("Soll");
    private EntityClientImpl entityClient;
    private EntityPortfolioStockImpl entityPortfolioStock;
    private EntityPortfolioAlternativeImpl entityPortfolioAlternative;
    private String successColor = "-fx-background-color: #BFFF00";
    private String  errorColor = "-fx-background-color: #FF4646";
    private double toleranz = 2.0;

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

        addBasicCellValueFactory();
        addStockCellValueFactory();
        addIonaCellValueFactory();
        addAlternativeCellValueFactory();
        addLiquidityCellValueFactory();
        createTable();
        createCellFactoryStock(stockShareCol);
        createCellFactoryAlternative(alternativeShareCol);
        crateCellFactoryIoan(ioanShareCol);
        createCellFactoryLiquidity(liquidityShareCol);
    }

    private void createTable() {
        final TreeItem<TableItem> root = new RecursiveTreeItem<>(getItems(), RecursiveTreeObject::getChildren);
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
    }

    private void addAlternativeCellValueFactory() {
        alternativeValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeValue());
        alternativeShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeShare());

        alternativeStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getAlternativeStrategy());
        headerAlternativeCol.getColumns().addAll(alternativeValueCol, alternativeShareCol, alternativeStrategyCol);
    }

    private void addIonaCellValueFactory() {
        ioanValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanValue());

        ioanShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanShare());

        ioanStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getIoanStrategy());
        headerIoanCol.getColumns().addAll(ioanValueCol, ioanShareCol, ioanStrategyCol);
    }

    private void addLiquidityCellValueFactory() {
        liquidityValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityValue());

        liquidityShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityShare());

        liquidityStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getLiquidityStrategy());
        headerLiquidityCol.getColumns().addAll(liquidityValueCol, liquidityShareCol, liquidityStrategyCol);
    }

    private void addStockCellValueFactory() {
        stockValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockValue());
        stockShareCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockShare());
        stockStrategyCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getStockStrategy());
        headerStockCol.getColumns().addAll(stockValueCol, stockShareCol, stockStrategyCol);
    }

    private void addBasicCellValueFactory() {
        nameCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getClientName());
        depoValueCol.setCellValueFactory(tableItemStringCellDataFeatures
                -> tableItemStringCellDataFeatures.getValue().getValue().getDepoValue());
    }

    private ObservableList<TableItem> getItems() {
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

        return tableItems;
    }

    private void createCellFactoryLiquidity(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null || !empty) {
                    setText(item);
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());
                    double soll = Double.parseDouble(tableItem.getValue().getLiquidityStrategy().getValue().replace(",", ".").replace(" %", ""));
                    double ist = Double.parseDouble(tableItem.getValue().getLiquidityShare().getValue().replace(",", ".").replace(" %", ""));

                    if (ist >= soll - toleranz && ist <= soll + toleranz) {
                        setStyle(successColor);
                    } else {
                        setStyle(errorColor);
                    }
                }
            }
        });
    }

    private void crateCellFactoryIoan(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());
                    double soll = Double.parseDouble(tableItem.getValue().getIoanStrategy().getValue().replace(",", ".").replace(" %", ""));
                    double ist = Double.parseDouble(tableItem.getValue().getIoanShare().getValue().replace(",", ".").replace(" %", ""));

                    if (ist >= soll - toleranz && ist <= soll + toleranz) {
                        setStyle(successColor);
                    } else {
                        setStyle(errorColor);
                    }
                }
            }
        });
    }

    private void createCellFactoryAlternative(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());

                    double soll = Double.parseDouble(tableItem.getValue().getAlternativeStrategy().getValue().replace(",", ".").replace(" %", ""));
                    double ist = Double.parseDouble(tableItem.getValue().getAlternativeShare().getValue().replace(",", ".").replace(" %", ""));

                    if (ist >= soll - toleranz && ist <= soll + toleranz) {
                        setStyle(successColor);
                    } else {
                        setStyle(errorColor);
                    }
                }
            }
        });
    }

    private void createCellFactoryStock(JFXTreeTableColumn<TableItem, String> col) {
        col.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item);
                    TreeItem<TableItem> tableItem = getTreeTableView().getTreeItem(getIndex());
                    double soll = Double.parseDouble(tableItem.getValue().getStockStrategy().getValue().replace(",", ".").replace(" %", ""));
                    double ist = Double.parseDouble(tableItem.getValue().getStockShare().getValue().replace(",", ".").replace(" %", ""));

                    if (ist >= soll - toleranz && ist <= soll + toleranz) {
                        setStyle(successColor);
                    } else {
                        setStyle(errorColor);
                    }
                }
            }
        });
    }
}

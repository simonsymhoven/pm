package controllers.dashboard.analyse;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entities.investment.Strategy;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.text.NumberFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableItem extends RecursiveTreeObject<TableItem> {
    private SimpleStringProperty clientName;
    private SimpleStringProperty depoValue;
    private SimpleStringProperty stockStrategy;
    private SimpleStringProperty alternativeStrategy;
    private SimpleStringProperty ioanStrategy;
    private SimpleStringProperty liquidityStrategy;
    private SimpleStringProperty stockValue;
    private SimpleStringProperty stockShare;
    private SimpleStringProperty alternativeValue;
    private SimpleStringProperty alternativeShare;
    private SimpleStringProperty ioanValue;
    private SimpleStringProperty ioanShare;
    private SimpleStringProperty liquidityValue;
    private SimpleStringProperty liquidityShare;
    private Strategy strategy;

    public TableItem(String name,
                     BigDecimal depoValue,
                     Strategy strategy,
                     double stockValue,
                     double alternativeValue,
                     double ioanValue,
                     double liquidityValue) {
        this.strategy = strategy;
        this.clientName = new SimpleStringProperty(name);
        this.depoValue = new SimpleStringProperty(NumberFormat.getCurrencyInstance()
                .format(depoValue));
        this.stockStrategy = new SimpleStringProperty(String.format("%.2f", strategy.getStockInvestment().getTarget()) + " %");
        this.alternativeStrategy = new SimpleStringProperty(String.format("%.2f", strategy.getAltInvestment().getTarget()) + " %");
        this.ioanStrategy = new SimpleStringProperty(String.format("%.2f", strategy.getIoanInvestment().getTarget()) + " %");
        this.liquidityStrategy = new SimpleStringProperty(String.format("%.2f", strategy.getLiquidityInvestment().getTarget()) + " %");
        this.stockValue = new SimpleStringProperty(NumberFormat.getCurrencyInstance().format(stockValue));
        this.stockShare = new SimpleStringProperty(String.format("%.2f", ((stockValue / depoValue.doubleValue()) * 100)) + " %");
        this.alternativeValue = new SimpleStringProperty(NumberFormat.getCurrencyInstance().format(alternativeValue));
        this.alternativeShare = new SimpleStringProperty(String.format("%.2f", ((alternativeValue / depoValue.doubleValue()) * 100)) + " %");
        this.ioanValue = new SimpleStringProperty(NumberFormat.getCurrencyInstance().format(ioanValue));
        this.ioanShare = new SimpleStringProperty(String.format("%.2f", ((ioanValue / depoValue.doubleValue()) * 100)) + " %");
        this.liquidityValue = new SimpleStringProperty(NumberFormat.getCurrencyInstance().format(liquidityValue));
        this.liquidityShare = new SimpleStringProperty(String.format("%.2f", ((liquidityValue / depoValue.doubleValue()) * 100)) + " %");
    }
}

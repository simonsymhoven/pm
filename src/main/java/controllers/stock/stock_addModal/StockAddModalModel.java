package controllers.stock.stock_addModal;

import entities.Stock;
import lombok.Data;

@Data
public class StockAddModalModel {
    private String symbol;
    private Stock stock;
    private double amount = 100.0;
}

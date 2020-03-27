package controllers.investments.stocks.stock_add_modal;

import entities.stock.Stock;
import lombok.Data;

@Data
class StockAddModalModel {
    private String symbol;
    private Stock stock;
    private double amount = 100.0;
}

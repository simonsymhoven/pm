package controllers.investments.stocks;

import entities.stock.Stock;
import yahooapi.History;
import lombok.Data;
import java.util.List;

@Data
public class StockModel {
    private List<Stock> stocks;
    private Stock stock;
    private List<History> history;
}

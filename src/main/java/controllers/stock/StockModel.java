package controllers.stock;

import yahooapi.History;
import entities.Stock;
import lombok.Data;
import java.util.List;

@Data
public class StockModel {
    private List<Stock> stocks;
    private Stock stock;
    private List<History> history;
}

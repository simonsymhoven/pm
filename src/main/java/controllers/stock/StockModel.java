package controllers.stock;

import YahooAPI.History;
import entities.Stock;
import lombok.Data;
import java.util.List;

@Data
public class StockModel {
    private List<Stock> aktien;
    private Stock stock;
    private List<History> history;
}

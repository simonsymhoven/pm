package controllers.stock;

import YahooAPI.History;
import entities.Stock;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class StockModel {
    Map<String, Stock> aktien;
    Stock stock = new Stock();
    List<History> history = new ArrayList<>();
}

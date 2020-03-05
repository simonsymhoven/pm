package controllers.aktien;

import YahooAPI.StockDTO;
import entities.Aktie;
import lombok.Data;
import java.util.Map;

@Data
public class AktienModel {
    Map<String, Aktie> aktien;
    Aktie aktie = new Aktie();
    StockDTO stock = new StockDTO();
}

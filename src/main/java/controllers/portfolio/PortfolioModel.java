package controllers.portfolio;

import entities.Client;
import entities.ClientStock;
import entities.Stock;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioModel {
    private List<Stock> stockList;
    private List<Client> clients;
    private List<ClientStock> clientStocks;
    private Client client;
    private Stock stock;
    private ClientStock clientStock;


    private DataFormat dataFormat;
    DataFormat getFormat() {
        String dateFormat = "StockList";
        if (DataFormat.lookupMimeType(dateFormat) == null) {
            this.dataFormat = new DataFormat(dateFormat);
        }
        return DataFormat.lookupMimeType(dateFormat);
    }

    private int quantity;
    private double shareTarget;
    private double shareActual;
    private double diffRelative;
    private int diffAbsolute;
}

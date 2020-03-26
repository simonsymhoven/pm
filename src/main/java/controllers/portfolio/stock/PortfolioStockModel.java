package controllers.portfolio.stock;

import entities.client.Client;
import entities.client.clientStock.ClientStock;
import entities.stock.Stock;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioStockModel {
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

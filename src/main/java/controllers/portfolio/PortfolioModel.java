package controllers.portfolio;

import entities.Client;
import entities.ClientStock;
import entities.Stock;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioModel {
    private List<Stock> aktienList;
    private List<Client> clients;
    private List<ClientStock> clientStocks;
    private Client client;
    private Stock stock;
    private ClientStock clientStock;


    private DataFormat dataFormat;
    DataFormat getFormat(){
        if (DataFormat.lookupMimeType("AktieList") == null)
            this.dataFormat = new DataFormat("AktieList");

        return DataFormat.lookupMimeType("AktieList");
    }

    private int quantity;
    private double shareSoll;
    private double shareIst;
    private double diffRelativ;
    private int diffAbsolut;
}

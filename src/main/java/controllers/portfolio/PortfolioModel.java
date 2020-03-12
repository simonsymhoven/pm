package controllers.portfolio;

import entities.Client;
import entities.Stock;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioModel {
    private List<Stock> aktienList;
    private List<Client> clients;
    private Client client;
    private Stock stock;

    private DataFormat dataFormat;
    DataFormat getFormat(){
        if (DataFormat.lookupMimeType("AktieList") == null)
            this.dataFormat = new DataFormat("AktieList");

        return this.dataFormat;
    }
}

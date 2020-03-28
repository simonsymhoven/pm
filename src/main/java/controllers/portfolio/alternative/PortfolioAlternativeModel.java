package controllers.portfolio.alternative;

import entities.alternative.Alternative;
import entities.client.Client;
import entities.client.client_alternative.ClientAlternative;
import javafx.scene.input.DataFormat;
import lombok.Data;

import java.util.List;

@Data
public class PortfolioAlternativeModel {
    private List<Alternative> alternatives;
    private List<Client> clients;
    private List<ClientAlternative> clientAlternatives;
    private Client client;
    private Alternative alternative;
    private ClientAlternative clientAlternative;
    private DataFormat dataFormat;
    private int quantity;

    DataFormat getFormat() {
        String dateFormat = "AlternativeList";
        if (DataFormat.lookupMimeType(dateFormat) == null) {
            this.dataFormat = new DataFormat(dateFormat);
        }
        return DataFormat.lookupMimeType(dateFormat);
    }
}

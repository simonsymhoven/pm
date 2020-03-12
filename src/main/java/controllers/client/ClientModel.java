package controllers.client;

import entities.Client;
import lombok.Data;
import java.util.List;

@Data
public class ClientModel {
    private List<Client> clients;
    private Client client;
}

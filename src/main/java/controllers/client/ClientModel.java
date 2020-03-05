package controllers.client;

import entities.Client;
import enums.ClientState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClientModel {
    List<Client> clients = new ArrayList<>();
    ClientState clientState;

    Client client = new Client();
}

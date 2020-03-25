package client;

import entities.Client;
import org.junit.Test;
import org.junit.Assert;

public class BasicTest {

    private Client client = new Client();

    @Test
    public void clientNotNull(){
        Assert.assertNotNull(client);
    }


}
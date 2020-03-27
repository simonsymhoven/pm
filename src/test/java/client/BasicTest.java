package client;

import entities.client.Client;
import org.junit.Test;
import org.junit.Assert;

public class BasicTest {
    @Test
    public void clientNotNull() {
        Client client = new Client();
        client.setName("Captain Blaubär");
        Assert.assertEquals("Captain Blaubär", client.getName());
    }


}
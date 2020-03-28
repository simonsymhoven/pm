package client;

import entities.client.Client;
import org.junit.Test;
import org.junit.Assert;

public class BasicTest {
    @Test
    public void clientNotNull() {
        Client client = new Client();
        client.setName("Captain Blaubär");
        client.setComment("Test of Coverage");
        Assert.assertEquals("Test of Coverage", client.getComment());
        Assert.assertEquals("Captain Blaubär", client.getName());
    }

}
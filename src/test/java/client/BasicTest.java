package client;

import entities.Client;
import org.junit.Test;
import org.junit.Assert;

import java.math.BigDecimal;

public class BasicTest {

    private Client client = new Client(
            "Forename Surname",
            "FoSu",
            65.0,
            new BigDecimal(100.34)
    );

    @Test
    public void clientNotNull(){
        Assert.assertNotNull(client);
    }


}
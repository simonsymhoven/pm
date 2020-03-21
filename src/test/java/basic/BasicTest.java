package basic;

import entities.Client;
import org.junit.Test;
import org.junit.Assert;

public class BasicTest {

    @Test
    public void firsTest(){
        Client client = new Client();
        client.setName("Hans Dieter");

        Assert.assertEquals(client.name, "Hans Dieter");
    }
}
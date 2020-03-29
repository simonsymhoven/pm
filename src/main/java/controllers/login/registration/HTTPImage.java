package controllers.login.registration;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.io.IOUtils;
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

@Log4j2
public class HTTPImage extends Task<byte[]> {

    private RegistrationModel registrationModel;
    private Random obj = new Random();

    HTTPImage(RegistrationModel registrationModel) {
        this.registrationModel = registrationModel;
    }

    @Override
    protected byte[] call() {
        return getImage();
    }

    private byte[] getImage() {
        byte[] o = null;
        String httpsURL = "https://eu.ui-avatars.com/api/?rounded=true&size=128&format=png&background=F9AA33&color=fff"
                + "&name="
                + registrationModel.getForename()
                + "+" + registrationModel.getSurname();
        try {
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            InputStream is = conn.getInputStream();
            o = IOUtils.toByteArray(is);
            registrationModel.setImage(o);
        } catch (Exception e) {
            log.error("Can not get profilePicture");
            log.error(e);
        }

        return o;
    }
}

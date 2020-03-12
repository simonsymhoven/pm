package json;

import java.io.IOException;

public interface JSONInterface {

    Object read(String key);

    void write(String key, String value) throws IOException;

}

package json;

import org.json.simple.parser.JSONParser;

import java.io.IOException;

public class JSONReaderImpl extends AbstractJSONReader {

    public JSONReaderImpl(JSONParser jsonParser) {
        super(jsonParser);
    }

    @Override
    public Object read(String key) {
        return super.read(key);
    }

    @Override
    public void write(String key, String value) throws IOException {
        super.write(key, value);
    }
}

package json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AbstractJSONReader implements JSONInterface {

    protected JSONParser jsonParser;
    protected JSONObject jsonObject;
    private String pathToJSON = System.getProperty("user.dir") + "/src/db/settings.json";

    public AbstractJSONReader(JSONParser jsonParser){
        this.jsonParser = jsonParser;

        try (FileReader reader = new FileReader(pathToJSON)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            this.jsonObject = jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object read(String key) {
        return jsonObject.get(key);
    }

    @Override
    public void write(String key, String value) throws IOException {
        jsonObject.put(key, value);

        try (FileWriter file = new FileWriter(pathToJSON)) {
            file.write(jsonObject.toJSONString());
        }
    }
}

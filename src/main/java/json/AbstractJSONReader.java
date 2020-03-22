package json;

import org.jetbrains.annotations.NotNull;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Log4j2
public class AbstractJSONReader implements JSONInterface {

    private JSONObject jsonObject;
    private String pathToJSON = System.getProperty("user.dir") + "/db/settings.json";

    AbstractJSONReader(@NotNull JSONParser jsonParser) {
        try (FileReader reader = new FileReader(pathToJSON)) {
            Object obj = jsonParser.parse(reader);
            this.jsonObject = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            log.error(e);
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

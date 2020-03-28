package json;

import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Log4j2
public class JSONReader {

    private JSONObject jsonObject;
    private String pathToJSON = System.getProperty("user.dir") + "/db/settings.json";

    public JSONReader() {
        if (!new File(pathToJSON).exists()) {
            try {
                if (new File(pathToJSON).createNewFile()) {
                    FileWriter file = new FileWriter(pathToJSON);
                    JSONObject user = new JSONObject();
                    user.put("user", "");
                    user.put("lastUpdateDate", "");
                    user.put("lastUpdateStatus", "");

                    file.write(user.toJSONString());
                    file.flush();
                }
            } catch (IOException e) {
                log.error("Could not initialize");
            }
        }
        Object obj = null;
        try (FileReader reader = new FileReader(pathToJSON)) {
            obj = new JSONParser().parse(reader);
            this.jsonObject = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            log.error(e);
        }
    }

    public Object read(String key) {
        return jsonObject.get(key);
    }

    public void write(String key, String value) throws IOException {
        jsonObject.put(key, value);

        try (FileWriter file = new FileWriter(pathToJSON)) {
            file.write(jsonObject.toJSONString());
            file.flush();
        }
    }
}

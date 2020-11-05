package eu.revamp.collectors.file;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

@Getter @Setter
public class JsonFile
{
    private File location;
    private String name;
    private JSONObject json;

    public JsonFile(File location, String name) {
        setLocation(location);
        setName(name);
        setup();
    }

    private void setup() {
        try {
            Object jsonFile = new JSONParser().parse(new FileReader(new File(getLocation().getPath(), getName())));
            setJson((JSONObject)jsonFile);
        }
        catch (FileNotFoundException error) {
            createJsonFile();
        }
        catch (ParseException | IOException ex2) {
            ex2.printStackTrace();
        }
    }

    private void createJsonFile() {
        try (FileWriter file = new FileWriter(new File(getLocation().getPath(), getName()))) {
            setJson(new JSONObject());
            file.write(getJson().toString());
            file.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (FileWriter file = new FileWriter(new File(getLocation().getPath(), getName()))) {
            file.write(getJson().toString());
            file.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

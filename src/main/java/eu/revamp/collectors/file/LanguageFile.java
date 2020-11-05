package eu.revamp.collectors.file;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.CC;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class LanguageFile {

    private File file;
    private YamlConfiguration configuration;

    public LanguageFile() {
        this.load();
    }

    public void load() {
        this.file = new File(RevampCollectors.getInstance().getDataFolder(), "language.yml");
        if (!this.file.exists()) {
            RevampCollectors.getInstance().saveResource("language.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        try {
            this.configuration.set(path, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return CC.translate(this.configuration.getString(path));
        }
        return null;
    }

    public long getLong(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getLong(path);
        }
        return 0L;
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            final ArrayList<String> strings = new ArrayList<>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(CC.translate(string));
            }
            return strings;
        }
        return null;
    }
}

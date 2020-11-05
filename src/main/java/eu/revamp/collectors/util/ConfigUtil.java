package eu.revamp.collectors.util;

import eu.revamp.collectors.file.JsonFile;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import eu.revamp.collectors.file.ConfigFile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class ConfigUtil
{
    private ItemStack chunkCollector;
    private Map<ItemStack, Integer> defaultCollectorItems;
    private boolean loaded;
    private JsonFile chunkCollectorConfig;
    private File chunkCollectorFile;

    public ConfigUtil() {
        setLoaded(false);
        if (!RevampCollectors.getInstance().getDataFolder().exists()) {
            RevampCollectors.getInstance().getDataFolder().mkdir();
        }
        setChunkCollectorConfig(new JsonFile(RevampCollectors.getInstance().getDataFolder(), "collectors.json"));
        setChunkCollector(ItemBuilder.createItem(RevampCollectors.getInstance().getConfigFile().getMaterial("COLLECTOR.MATERIAL"), RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR.NAME"), 1, 0, RevampCollectors.getInstance().getConfigFile().getStringList("COLLECTOR.LORE")));
    }

    public String getMessage(String args, MessageType messageType) {
        StringBuilder sb = new StringBuilder();
        switch (messageType) {
            case SUCCESS: {
                sb.append(Language.PREFIX_SUCCESS.toString());
                break;
            }
            case FAILURE: {
                sb.append(Language.PREFIX_FAILURE.toString());
                break;
            }
        }
        return sb.toString() + args;
    }

    public void setup() {
        this.setDefaultCollectorItems(RevampCollectors.getInstance().getConfigFile());
        RevampCollectors.getInstance().getChunkCollectorManager().loadChunkCollectors();
    }

    private void setDefaultCollectorItems(ConfigFile config) {
        this.defaultCollectorItems = new HashMap<>();
        for (String element : config.getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false)) {
            int slot = config.getInt("COLLECTOR_GUI.ITEMS." + element + ".SLOT");
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            String upperCase = element.toUpperCase();
            String owner;
            switch (upperCase) {
                case "IRON_GOLEM": {
                    owner = "MHF_Golem";
                    break;
                }
                case "PIG_ZOMBIE": {
                    owner = "MHF_PigZombie";
                    break;
                }
                case "SUGAR_CANE": {
                    owner = "Sugar_Cane_";
                    break;
                }
                case "WITCH": {
                    owner = "MHF_Witch";
                    break;
                }
                case "TNT": {
                    owner = "MHF_TNT";
                    break;
                }
                default: {
                    owner = "MHF_" + StringUtils.capitalize(element);
                    break;
                }
            }
            SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
            skullMeta.setOwner(owner);
            skull.setItemMeta(skullMeta);
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR_GUI.ITEMS." + element + ".NAME").replace("%amount%", String.valueOf(0)));
            skull.setItemMeta(meta);
            this.defaultCollectorItems.put(skull, slot);
        }
        for (String element : config.getConfigurationSection("COLLECTOR_GUI.COMPONENTS").getKeys(false)) {
            int slot = config.getInt("COLLECTOR_GUI.COMPONENTS." + element + ".SLOT");
            String string;
            String configType = string = config.getString("COLLECTOR_GUI.COMPONENTS." + element + ".TYPE");
            Material material;
            switch (string) {
                case "XP": {
                    material = Material.EXP_BOTTLE;
                    break;
                }
                default: {
                    material = Material.getMaterial(configType);
                    break;
                }
            }
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR_GUI.COMPONENTS." + element + ".NAME"));
            List<String> lore = new ArrayList<>();
            if (RevampCollectors.getInstance().getConfigFile().getConfiguration().contains("COLLECTOR_GUI.COMPONENTS." + element + ".LORE")) {
                for (String line : RevampCollectors.getInstance().getConfigFile().getStringList("COLLECTOR_GUI.COMPONENTS." + element + ".LORE")) {
                    lore.add(line.replace("%value%", String.valueOf(0)));
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            this.defaultCollectorItems.put(item, slot);
        }
    }

    public ItemMeta getCollectorMeta() {
        return this.chunkCollector.getItemMeta();
    }
}

package eu.revamp.collectors.util;

import com.massivecraft.factions.*;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import eu.revamp.collectors.file.ConfigFile;
import eu.revamp.collectors.util.General;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter @Setter
public class ChunkCollector {
    private Map<String, Integer> data;

    private Inventory inventory;
    private Location location;
    private Faction faction;
    private double total;
    private double totalValueLogs;
    private int id;
    private int exp;
    private HashMap<String, Double> sellHistory = new HashMap<>();

    public ChunkCollector(Faction faction, Location location) {
        setData(new HashMap<>());
        setFaction(faction);
        setLocation(location);
        setTotal(0.0);
        setTotalValueLogs(0.0);
        setId(-1);
        setExp(0);
        this.setup();
    }

    private void setup() {
        setInventory(Bukkit.createInventory(null, RevampCollectors.getInstance().getConfigFile().getInt("COLLECTOR_GUI.SIZE"), RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR_GUI.TITLE")));
        this.addDefaultItems();
        this.addBorder(DyeColor.valueOf(RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR_GUI.GLASS_COLOR")));
    }

    private void addDefaultItems() {
        Map<ItemStack, Integer> items = RevampCollectors.getInstance().getConfigUtil().getDefaultCollectorItems();
        for (ItemStack item : items.keySet()) {
            getInventory().setItem(items.get(item), item);
        }
    }

    private void addBorder(DyeColor color) {
        for (int i = 0; i < getInventory().getSize(); ++i) {
            if (getInventory().getItem(i) == null || getInventory().getItem(i).getType() == Material.AIR) {
                ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) color.ordinal());
                getInventory().setItem(i, glass);
            }
        }
    }

    public void save() {
        try {
            JSONObject jsonObject = RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig().getJson();
            JSONObject factionObject = (JSONObject) jsonObject.get(getFaction().getTag());
            if (getId() == -1) {
                setId(1);
                if (factionObject != null) {
                    while (factionObject.containsKey("collector" + getId())) {
                        ++this.id;
                    }
                } else {
                    factionObject = new JSONObject();
                }
            }
            HashMap<String, Integer> collectorContent = new HashMap<>();
            //JSONObject collectorContent = new JSONObject();
            for (String element : RevampCollectors.getInstance().getConfigFile().getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false)) {
                collectorContent.put(element, this.data.getOrDefault(element, 0));
            }
            HashMap<String, Object> collectorObject = new HashMap<>();
            //JSONObject collectorObject = new JSONObject();
            collectorObject.put("x", getLocation().getX());
            collectorObject.put("y", getLocation().getY());
            collectorObject.put("z", getLocation().getZ());
            collectorObject.put("exp", getExp());
            //collectorObject.put("total_value", getWorth());
            collectorObject.put("world", getLocation().getWorld().getName());
            collectorObject.put("content", collectorContent);
            factionObject.put("collector" + getId(), collectorObject);
            jsonObject.put(getFaction().getTag(), factionObject);
            RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig().save();
        } catch (NullPointerException ignored) {
        }
    }

    public void destroy() {
        JSONObject jsonObject = RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig().getJson();
        JSONObject factionObject = (JSONObject) jsonObject.get(getFaction().getTag());
        factionObject.remove("collector" + getId());
        RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig().save();
        getLocation().getBlock().setType(Material.AIR);
        getLocation().getWorld().dropItemNaturally(getLocation(), RevampCollectors.getInstance().getConfigUtil().getChunkCollector());
    }

    public void loadContents() {
        try {
            if (getFaction() == null) return;
            JSONObject jsonObject = RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig().getJson();
            JSONObject factionObject = (JSONObject) jsonObject.get(getFaction().getTag());
            if (factionObject == null) return;
            JSONObject collectorObject = (JSONObject) factionObject.get("collector" + getId());
            JSONObject contentObject = (JSONObject) collectorObject.get("content");
            for (Object o : contentObject.keySet()) {
                String name = String.valueOf(o);
                Object contentName = contentObject.get(name);
                if (General.isInt(contentName)) {
                    this.setAmount(name, Integer.parseInt(String.valueOf(contentName)));
                } else {
                    this.setAmount(name, ((Long) contentName).intValue());
                }
            }
            if (General.isInt(collectorObject.get("exp"))) {
                setExp(Integer.parseInt(String.valueOf(collectorObject.get("exp"))));
            } else {
                setExp(((Long) collectorObject.get("exp")).intValue());
            }
            this.update();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void update() {
        this.total = 0.0;
        ConfigFile config = RevampCollectors.getInstance().getConfigFile();
        for (int i = 0; i < getInventory().getSize(); ++i) {
            if (!getInventory().getViewers().isEmpty()) {
                for (String element : config.getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false)) {
                    int slot = config.getInt("COLLECTOR_GUI.ITEMS." + element + ".SLOT");
                    if (i == slot) {
                        String name = config.getString("COLLECTOR_GUI.ITEMS." + element + ".NAME").replace("%amount%", String.valueOf((this.data.get(element) == null) ? 0 : this.data.get(element)));
                        ItemStack item = getInventory().getItem(i);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(name);
                        item.setItemMeta(meta);
                        this.total += RevampCollectors.getInstance().getConfigFile().getInt("COLLECTOR_GUI.ITEMS." + element + ".PRICE") * ((this.data.get(element) == null) ? 0 : this.data.get(element));
                    }
                }
                for (String element : config.getConfigurationSection("COLLECTOR_GUI.COMPONENTS").getKeys(false)) {
                    int slot = config.getInt("COLLECTOR_GUI.COMPONENTS." + element + ".SLOT");
                    if (i == slot) {
                        ItemStack item2 = getInventory().getItem(i);
                        ItemMeta meta2 = item2.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        double value = 0.0;
                        switch (element) {
                            case "TOTAL_VALUE":
                                value = this.total;
                                break;
                            case "TOTAL_XP":
                                value = getExp();
                                break;
                            case "LOGS":
                                value = this.totalValueLogs;
                                break;
                        }
                        for (String s : RevampCollectors.getInstance().getConfigFile().getStringList("COLLECTOR_GUI.COMPONENTS." + element + ".LORE")) {
                            lore.add(s.replace("%value%", String.format("%,.0f", value)));
                        }
                        meta2.setLore(lore);
                        item2.setItemMeta(meta2);
                    }
                }
            }
        }
    }

    public boolean hasPermission(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction locationFaction = Board.getInstance().getFactionAt(new FLocation(getLocation()));
        return locationFaction.isWilderness() || ((fPlayer.hasFaction() || fPlayer.isAdminBypassing()) && (locationFaction.equals(fPlayer.getFaction()) || fPlayer.isAdminBypassing()) && (fPlayer.getRole().value >= RevampCollectors.getInstance().getConfigFile().getInt("FACTION_PERMISSION_LEVEL") || fPlayer.isAdminBypassing()));
    }

    public void setAmount(String data, int amount) {
        this.data.put(data, amount);
    }

    public int getAmount(String data) {
        if (this.data.get(data) == null) {
            return 0;
        }
        return this.data.get(data);
    }

    public void sellAll(Player player, double multiplier, boolean screen) {
        ConfigFile config = RevampCollectors.getInstance().getConfigFile();
        double total = 0.0;
        boolean empty = true;
        for (String data : config.getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false)) {
            double price = config.getDouble("COLLECTOR_GUI.ITEMS." + data + ".PRICE");
            int amount = this.getAmount(data);
            /*
            switch (data.toUpperCase()){

                case "IRON_GOLEM":

                    break;
                default:
                    break;
            }
             */
            if (data.equalsIgnoreCase("CREEPER") || data.equalsIgnoreCase("TNT")) {
                this.depositTnt(player, amount, data);
            } else {
                if (amount <= 0) {
                    continue;
                }
                empty = false;
                total += amount * price;

                RevampCollectors.economy.depositPlayer(player, amount * price * multiplier);
                this.setAmount(data, 0);
                this.update();
            }
        }
        this.totalValueLogs += total;
        if (!empty && screen) {
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + RevampCollectors.getInstance().getConfigFile().getString("SELL_TITLE").replace("%price%", String.format("%,.2f", total * multiplier)) + "\"}"), 5, 5, 5);
            PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + RevampCollectors.getInstance().getConfigFile().getString("SELL_SUBTITLE").replace("%price%", String.format("%,.2f", total * multiplier)) + "\"}"), 5, 5, 5);
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(title);
            playerConnection.sendPacket(subtitle);
        }
    }

    public void open(Player player) {
        player.openInventory(getInventory());
    }


    public boolean contains(String data) {
        return this.data.containsKey(data);
    }

    public boolean compareInventory(Inventory inventory) {
        return getInventory().equals(inventory);
    }

    public void addExp(int amount) {
        this.exp += amount;
    }

    public void redeemExp(Player player) {
        if (getExp() <= 30) {
            player.giveExpLevels(getExp());
            setExp(0);
        } else {
            player.giveExpLevels(30);
            this.exp -= 30;
        }
    }

    public void updateType() {
        Block block = new Location(getLocation().getWorld(), getLocation().getX(), getLocation().getY(), getLocation().getZ()).getBlock();
        block.setType(Material.AIR);
        block.setType(Material.valueOf(RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR.MATERIAL")));
        block.getState().update();
    }


    public void depositTnt(Player player, int amount, String data) {
        int amplifier = 1;
        if (RevampCollectors.getInstance().getConfigFile().getConfiguration().contains("TNT_AMOUNT_CREEPER")) {
            amplifier = RevampCollectors.getInstance().getConfigFile().getInt("TNT_AMOUNT_CREEPER");
        }
        player.sendMessage(RevampCollectors.getInstance().getConfigUtil().getMessage(Language.COLLECTOR_TNT_BANK_DEPOSIT.toString(), MessageType.SUCCESS).replace("%amount%", String.format("%,d", amount * amplifier)));
        FPlayers.getInstance().getByPlayer(player).getFaction().addTnt(amount * amplifier);
        this.setAmount(data, 0);
        this.update();
    }
    /*
    *
        LOGS:
      TYPE: PAPER
      NAME: "&bSell History"
      LORE:
        - ""
        - "&aCreepers &7» %creepers%"
        - "&fIron Golems &7» %golems%"
        - "&fChickens &7» %chickens%"
        - "&dPigmen &7» %pigmen%"
        - "&aCreeper &7» %value%"
        - "&8Cows &7» %cows%"
        - "&0Endermen &7» %endermen%"
        - "&bVillagers &7» %villagers%"
        - "&dPigs &7» %pigs%"
        - "&2Cactus &7» %cactus%"
        - "&aSugar Canes &7» %canes%"
        - ""
        - "&bTotal Value&7» $%value%"
        - ""
      SLOT: 38
    *
     */
}
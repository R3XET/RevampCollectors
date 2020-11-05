package eu.revamp.collectors.events;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import eu.revamp.collectors.file.ConfigFile;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Iterator;

public class CollectorClickEvent implements Listener
{
    private RevampCollectors plugin;

    public CollectorClickEvent(RevampCollectors instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onCollectorClick(InventoryClickEvent event) {
        Iterator<ChunkCollector> chunkCollectors = this.plugin.getChunkCollectorManager().getChunkCollectors();
        Label_0539:
        while (chunkCollectors.hasNext()) {
            ChunkCollector temp = chunkCollectors.next();
            if (temp.compareInventory(event.getInventory()) && event.getWhoClicked() instanceof Player) {
                event.setCancelled(true);
                Player player = (Player)event.getWhoClicked();
                if (!temp.hasPermission(player)) {
                    return;
                }
                ConfigFile config = this.plugin.getConfigFile();
                for (String data : config.getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false)) {
                    if (config.getInt("COLLECTOR_GUI.ITEMS." + data + ".SLOT") == event.getSlot()) {
                        double price = config.getDouble("COLLECTOR_GUI.ITEMS." + data + ".PRICE");
                        int amount = temp.getAmount(data);
                        if (amount <= 0) {
                            return;
                        }
                        if (data.equalsIgnoreCase("CREEPER") || data.equalsIgnoreCase("TNT")) {
                            temp.depositTnt(player, amount, data);
                            break Label_0539;
                        }
                        RevampCollectors.economy.depositPlayer(player, amount * price);
                        player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_SELL.toString(), MessageType.SUCCESS).replace("%amount%", String.valueOf(amount)).replace("%name%", WordUtils.capitalizeFully(data.replace("_", " "))).replace("%price%", String.format("%,.2f", amount * price)));
                        temp.setAmount(data, 0);
                        temp.update();
                        break Label_0539;
                    }
                }
                for (String data : config.getConfigurationSection("COLLECTOR_GUI.COMPONENTS").getKeys(false)) {
                    if (config.getInt("COLLECTOR_GUI.COMPONENTS." + data + ".SLOT") == event.getSlot()) {
                        if (data.equals("TOTAL_XP")) {
                            if (temp.getExp() <= 0) {
                                continue;
                            }
                            temp.redeemExp(player);
                            temp.update();
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                            player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_XP_REDEEMED.toString(), MessageType.SUCCESS).replace("%level%", String.valueOf(player.getLevel())));
                        }
                        else if (data.equals("LOGS")) {}
                    }
                }
            }
        }
    }
}

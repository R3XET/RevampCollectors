package eu.revamp.collectors.events;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CollectorInteractEvent implements Listener
{
    @EventHandler
    public void onCollectorInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ChunkCollector chunkCollector;
        if ((chunkCollector = RevampCollectors.getInstance().getChunkCollectorManager().getChunkCollectorAtLocation(event.getClickedBlock().getLocation())) != null) {
            event.setCancelled(true);
            FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
            if (!RevampCollectors.getInstance().getConfigUtil().isLoaded()) {
                event.getPlayer().sendMessage(RevampCollectors.getInstance().getConfigUtil().getMessage(Language.CONFIG_LOADING.toString(), MessageType.FAILURE));
                return;
            }
            if (!chunkCollector.hasPermission(player.getPlayer())) {
                player.sendMessage(RevampCollectors.getInstance().getConfigUtil().getMessage(Language.COLLECTOR_NO_BREAK_PLACE_INTERACT_PERMISSION.toString(), MessageType.FAILURE));
                return;
            }
            if (player.getPlayer().isSneaking()) {
                if (!chunkCollector.hasPermission(player.getPlayer())) {
                    return;
                }
                chunkCollector.sellAll(player.getPlayer(), 1.0, true);
                player.sendMessage(Language.COLLECTOR_SELL_ALL.toString());
            }
            else {
                double multiplier = this.usingWand(player.getPlayer());
                if (multiplier < 1.0) {
                    chunkCollector.open(event.getPlayer());
                    new BukkitRunnable() {
                        public void run() {
                            chunkCollector.update();
                            event.getPlayer().updateInventory();
                        }
                    }.runTaskLater(RevampCollectors.getInstance(), 3L);
                    return;
                }
                chunkCollector.sellAll(player.getPlayer(), multiplier, true);
            }
        }
    }

    private double usingWand(Player player) {
        ItemStack hand = player.getItemInHand();
        if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
            for (String wand : RevampCollectors.getInstance().getConfigFile().getConfigurationSection("WANDS").getKeys(false)) {
                if (hand.getItemMeta().getDisplayName().equals(RevampCollectors.getInstance().getConfigFile().getString("WANDS." + wand + ".NAME"))) {
                    return RevampCollectors.getInstance().getConfigFile().getDouble("WANDS." + wand + ".PRICE_MULTIPLIER");
                }
            }
        }
        return 0.0;
    }
}

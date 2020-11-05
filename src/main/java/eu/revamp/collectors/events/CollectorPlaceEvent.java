package eu.revamp.collectors.events;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorPlaceEvent implements Listener
{
    private RevampCollectors plugin;

    public CollectorPlaceEvent(RevampCollectors instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        if (this.isPlacingChunkCollector(hand)) {
            if (!this.plugin.getConfigUtil().isLoaded()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.plugin.getConfigUtil().getMessage(Language.CONFIG_LOADING.toString(), MessageType.FAILURE));
                return;
            }
            FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
            if (!player.hasFaction()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_CANNOT_PLACE.toString(), MessageType.FAILURE));
                return;
            }
            if (player.getRole().value < this.plugin.getConfigFile().getInt("FACTION_PERMISSION_LEVEL") && !player.isAdminBypassing()) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_NO_BREAK_PLACE_INTERACT_PERMISSION.toString(), MessageType.FAILURE));
                return;
            }
            if (Board.getInstance().getFactionAt(new FLocation(event.getBlockPlaced().getLocation())).isWilderness() && !this.plugin.getConfigFile().getBoolean("WILDERNESS_PLACE_ENABLED") && !player.isAdminBypassing()) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_CANNOT_PLACE_WILDERNESS.toString(), MessageType.FAILURE).replace("%collectorname%", this.plugin.getConfigUtil().getCollectorMeta().getDisplayName()));
                return;
            }
            if (this.plugin.getChunkCollectorManager().getChunkCollector(event.getBlockPlaced().getChunk()) != null) {
                event.setCancelled(true);
                player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_MAX_COLLECTORS_PER_CHUNK.toString(), MessageType.FAILURE));
                return;
            }
            if (!event.isCancelled()) {
                ChunkCollector chunkCollector = new ChunkCollector(player.getFaction(), event.getBlockPlaced().getLocation());
                this.plugin.getChunkCollectorManager().addChunkCollector(chunkCollector);
                player.sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_PLACE.toString(), MessageType.SUCCESS).replace("%collectorname%", this.plugin.getConfigUtil().getCollectorMeta().getDisplayName()));
            }
        }
    }

    private boolean isPlacingChunkCollector(ItemStack hand) {
        return hand != null && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().getDisplayName().equals(this.plugin.getConfigUtil().getCollectorMeta().getDisplayName()) && hand.getType() == RevampCollectors.getInstance().getConfigFile().getMaterial("COLLECTOR.MATERIAL");
    }
}

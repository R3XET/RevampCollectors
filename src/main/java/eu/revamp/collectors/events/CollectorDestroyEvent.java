package eu.revamp.collectors.events;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.managers.ChunkCollectorManager;
import eu.revamp.collectors.enums.MessageType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;

public class CollectorDestroyEvent implements Listener
{
    private RevampCollectors plugin;

    public CollectorDestroyEvent(RevampCollectors instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onCollectorBreak(BlockBreakEvent event) {
        ChunkCollectorManager manager = this.plugin.getChunkCollectorManager();
        ChunkCollector chunkCollector;
        if ((chunkCollector = manager.getChunkCollectorAtLocation(event.getBlock().getLocation())) != null) {
            Faction faction = Board.getInstance().getFactionAt(new FLocation(chunkCollector.getLocation()));
            FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
            if (!chunkCollector.hasPermission(event.getPlayer())) {
                event.setCancelled(true);
                String message = this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_NO_BREAK_PLACE_INTERACT_PERMISSION.toString(), MessageType.FAILURE);
                event.getPlayer().sendMessage(message);
                return;
            }
            event.getPlayer().sendMessage(this.plugin.getConfigUtil().getMessage(Language.COLLECTOR_BREAK.toString(), MessageType.SUCCESS).replace("%collectorname%", this.plugin.getConfigUtil().getCollectorMeta().getDisplayName()));
            chunkCollector.sellAll(event.getPlayer(), 1.0, true);
            manager.removeChunkCollector(chunkCollector);
        }
    }

    @EventHandler
    public void onDisband(FactionDisbandEvent event) {
        if (event.getFaction() == null) {
            return;
        }
        Iterator<ChunkCollector> collectors = this.plugin.getChunkCollectorManager().getCollectorsInFaction(event.getFaction());
        while (collectors.hasNext()) {
            ChunkCollector temp = collectors.next();
            temp.sellAll(event.getPlayer(), 1.0, false);
            this.plugin.getChunkCollectorManager().removeChunkCollector(temp);
        }
    }

    @EventHandler
    public void onLeaveDisband(FPlayerLeaveEvent event) {
        if (event.getFaction().getFPlayers().size() == 1) {
            Iterator<ChunkCollector> collectors = this.plugin.getChunkCollectorManager().getCollectorsInFaction(event.getFaction());
            while (collectors.hasNext()) {
                ChunkCollector temp = collectors.next();
                temp.sellAll(event.getfPlayer().getPlayer(), 1.0, false);
                this.plugin.getChunkCollectorManager().removeChunkCollector(temp);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        ChunkCollectorManager manager = this.plugin.getChunkCollectorManager();
        for (Block block : event.blockList()) {
            ChunkCollector chunkCollector;
            if (block.getType() == Material.valueOf(this.plugin.getConfigFile().getString("COLLECTOR.MATERIAL")) && (chunkCollector = manager.getChunkCollectorAtLocation(block.getLocation())) != null) {
                manager.removeChunkCollector(chunkCollector);
                break;
            }
        }
    }
}

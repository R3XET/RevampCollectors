package eu.revamp.collectors.events;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;

public class CollectorCropEvent implements Listener
{
    private RevampCollectors plugin;
    private List<String> disableNaturalFarms;

    public CollectorCropEvent(RevampCollectors instance) {
        this.plugin = instance;
        this.disableNaturalFarms = this.plugin.getConfigFile().getStringList("DISABLED_NATURAL_FARMS");
    }

    @EventHandler
    public void onCollectorCropEvent(BlockBreakEvent event) {
        ChunkCollector chunkCollector = this.plugin.getChunkCollectorManager().getChunkCollector(event.getBlock().getChunk());
        if (chunkCollector == null) {
            return;
        }
        Material blockType = event.getBlock().getType();
        int amount = 0;
        if (blockType == Material.SUGAR_CANE_BLOCK) {
            event.setCancelled(true);
            List<Block> blocks = new ArrayList<>();
            Location location;
            while ((location = event.getBlock().getLocation().add(0.0, amount, 0.0)).getBlock().getType() == blockType) {
                blocks.add(location.getBlock());
                ++amount;
            }
            for (int i = blocks.size() - 1; i >= 0; --i) {
                blocks.get(i).setType(Material.AIR);
            }
            String name = "SUGAR_CANE";
            Set<String> possibleMobs = this.plugin.getConfigFile().getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false);
            if (possibleMobs.contains(name)) {
                chunkCollector.setAmount(name, chunkCollector.getAmount(name) + amount);
                chunkCollector.update();
            }
        }
        if (blockType == Material.CACTUS) {
            event.setCancelled(true);
            List<Block> blocks = new ArrayList<>();
            Location location;
            while ((location = event.getBlock().getLocation().add(0.0, amount, 0.0)).getBlock().getType() == blockType) {
                blocks.add(location.getBlock());
                ++amount;
            }
            for (int i = blocks.size() - 1; i >= 0; --i) {
                blocks.get(i).setType(Material.AIR);
            }
            String name = "CACTUS";
            Set<String> possibleMobs = this.plugin.getConfigFile().getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false);
            if (possibleMobs.contains(name)) {
                chunkCollector.setAmount(name, chunkCollector.getAmount(name) + amount);
                chunkCollector.update();
            }
        }
    }

    @EventHandler
    public void onCactusGrow(BlockGrowEvent event) {
        ChunkCollector chunkCollector = this.plugin.getChunkCollectorManager().getChunkCollector(event.getBlock().getChunk());
        if (chunkCollector == null) {
            if (this.disableNaturalFarms.contains(event.getNewState().getType().name())) {
                event.setCancelled(true);
            }
            return;
        }
        Material blockType = event.getNewState().getType();
        if (blockType == Material.CACTUS) {
            event.setCancelled(true);
            String name = "CACTUS";
            Set<String> possibleMobs = this.plugin.getConfigFile().getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false);
            if (possibleMobs.contains(name)) {
                chunkCollector.setAmount(name, chunkCollector.getAmount(name) + 1);
                chunkCollector.update();
            }
        }
    }
}
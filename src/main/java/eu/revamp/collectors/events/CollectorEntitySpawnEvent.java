package eu.revamp.collectors.events;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.ChunkCollector;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.util.Random;
import java.util.Set;

public class CollectorEntitySpawnEvent implements Listener
{
    private RevampCollectors plugin;

    public CollectorEntitySpawnEvent(RevampCollectors instance) {
        this.plugin = instance;
    }

    @EventHandler @SuppressWarnings("deprecation")
    public void onCollectorEntitySpawn(SpawnerSpawnEvent event) {
        ChunkCollector chunkCollector = this.plugin.getChunkCollectorManager().getChunkCollector(event.getSpawner().getChunk());
        if (chunkCollector == null || (event.getEntityType() == EntityType.DROPPED_ITEM)) return;
        String name = event.getEntityType().getName().toUpperCase();
        Set<String> possibleMobs = this.plugin.getConfigFile().getConfigurationSection("COLLECTOR_GUI.ITEMS").getKeys(false);
        if (possibleMobs.contains(name)) {
            event.setCancelled(true);
            Random random = new Random();
            int xpChance = this.plugin.getConfigFile().getConfiguration().contains("COLLECTOR_EXPERIENCE_CHANCE") ? this.plugin.getConfigFile().getInt("COLLECTOR_EXPERIENCE_CHANCE") : 2;
            if (random.nextInt(100) + 1 <= xpChance) {
                chunkCollector.addExp(1);
            }
            int from = chunkCollector.contains(name) ? chunkCollector.getAmount(name) : 0;
            chunkCollector.setAmount(name, from + 1);
            chunkCollector.update();
        }
        else if (name.equalsIgnoreCase("VILLAGERGOLEM")){
            name = "IRON_GOLEM";
            event.setCancelled(true);
            Random random = new Random();
            int xpChance = this.plugin.getConfigFile().getConfiguration().contains("COLLECTOR_EXPERIENCE_CHANCE") ? this.plugin.getConfigFile().getInt("COLLECTOR_EXPERIENCE_CHANCE") : 2;
            if (random.nextInt(100) + 1 <= xpChance) {
                chunkCollector.addExp(1);
            }
            int from = chunkCollector.contains(name) ? chunkCollector.getAmount(name) : 0;
            chunkCollector.setAmount(name, from + 1);
            chunkCollector.update();
        }
    }
}


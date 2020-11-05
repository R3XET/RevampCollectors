package eu.revamp.collectors.managers;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.file.JsonFile;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChunkCollectorManager
{
    private List<ChunkCollector> chunkCollectors;

    public ChunkCollectorManager() {
        this.chunkCollectors = new ArrayList<>();
    }

    public void start() {
        new BukkitRunnable() {
            public void run() {
                Iterator<ChunkCollector> chunkCollectors = RevampCollectors.getInstance().getChunkCollectorManager().getChunkCollectors();
                int amount = 0;
                while (chunkCollectors.hasNext()) {
                    ChunkCollector temp = chunkCollectors.next();
                    temp.save();
                    ++amount;
                }
                if (amount > 0) {
                    System.out.print(Language.COLLECTOR_SAVED.toString().replace("%amount%", String.valueOf(amount)));
                }
            }
        }.runTaskTimerAsynchronously(RevampCollectors.getInstance(), 18000L, 18000L);
    }

    public void loadChunkCollectors() {
        this.chunkCollectors.clear();
        JsonFile jsonFile = RevampCollectors.getInstance().getConfigUtil().getChunkCollectorConfig();
        JSONObject json = jsonFile.getJson();
        for (Object o : json.keySet()) {
            String factionName = String.valueOf(o).trim();
            JSONObject faction = (JSONObject) json.get(factionName);
            for (Object collector : faction.keySet()) {
                JSONObject collectorObject = (JSONObject) faction.get(collector);
                double x = (double) collectorObject.get("x");
                double y = (double) collectorObject.get("y");
                double z = (double) collectorObject.get("z");
                String world = (String) collectorObject.get("world");
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                ChunkCollector chunkCollector = new ChunkCollector(Factions.getInstance().getByTag(factionName), location);
                chunkCollector.setId(Integer.parseInt(String.valueOf(collector).substring(9)));
                chunkCollector.loadContents();
                this.chunkCollectors.add(chunkCollector);
            }
        }
    }

    public ChunkCollector getChunkCollector(Chunk chunk) {
        for (ChunkCollector chunkCollector : this.chunkCollectors) {
            if (chunkCollector.getLocation().getChunk().equals(chunk)) {
                return chunkCollector;
            }
        }
        return null;
    }

    public Iterator<ChunkCollector> getCollectorsInFaction(Faction faction) {
        List<ChunkCollector> collectors = new ArrayList<>();
        for (ChunkCollector chunkCollector : this.chunkCollectors) {
            try {
                if (!chunkCollector.getFaction().getTag().equals(faction.getTag())) {
                    continue;
                }
                collectors.add(chunkCollector);
            }
            catch (NullPointerException ignored) {}
        }
        return collectors.iterator();
    }

    public ChunkCollector getChunkCollectorAtLocation(Location location) {
        for (ChunkCollector chunkCollector : this.chunkCollectors) {
            if (chunkCollector.getLocation().equals(location)) {
                return chunkCollector;
            }
        }
        return null;
    }

    public void addChunkCollector(ChunkCollector chunkCollector) {
        this.chunkCollectors.add(chunkCollector);
        chunkCollector.save();
    }

    public void removeChunkCollector(ChunkCollector chunkCollector) {
        this.chunkCollectors.remove(chunkCollector);
        chunkCollector.destroy();
    }

    public Iterator<ChunkCollector> getChunkCollectors() {
        return this.chunkCollectors.iterator();
    }
}

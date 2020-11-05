package eu.revamp.collectors.events;

import eu.revamp.collectors.RevampCollectors;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class CollectorHopperEvent implements Listener
{
    private RevampCollectors plugin;

    public CollectorHopperEvent(RevampCollectors instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onCollectorHopper(InventoryPickupItemEvent event) {
        if (event.getInventory() != null && event.getInventory().getType() == InventoryType.HOPPER) {
            Hopper hopper = (Hopper)event.getInventory().getHolder();
            if (this.plugin.getChunkCollectorManager().getChunkCollectorAtLocation(hopper.getLocation()) != null) {
                event.setCancelled(true);
            }
        }
    }
}

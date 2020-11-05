package eu.revamp.collectors.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    public static ItemStack createItem(Material material, String name, int amount, int durability, List<String> path) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metalore = new ArrayList<>();
        for (String lores : path) {
            metalore.add(CC.translate(lores));
        }
        meta.setLore(metalore);
        item.setItemMeta(meta);
        item.setDurability((short) durability);
        return item;
    }
}

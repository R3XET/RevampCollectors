package eu.revamp.collectors.commands;

import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.enums.MessageType;
import eu.revamp.collectors.util.ConfigUtil;
import eu.revamp.collectors.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class RevampCollectorsCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConfigUtil configUtil = RevampCollectors.getInstance().getConfigUtil();
        if (!sender.hasPermission("revampcollector.give") && !sender.isOp()) {
            sender.sendMessage(Language.COMMANDS_NO_PERMISSION_MESSAGE.toString());
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            RevampCollectors.getInstance().getConfigFile().save();
            RevampCollectors.getInstance().getConfigUtil().setChunkCollector(ItemBuilder.createItem(RevampCollectors.getInstance().getConfigFile().getMaterial("COLLECTOR.MATERIAL"), RevampCollectors.getInstance().getConfigFile().getString("COLLECTOR.NAME"), 1, 0, RevampCollectors.getInstance().getConfigFile().getStringList("COLLECTOR.LORE")));
            RevampCollectors.getInstance().getConfigUtil().setup();
            Iterator<ChunkCollector> collectors = RevampCollectors.getInstance().getChunkCollectorManager().getChunkCollectors();
            while (collectors.hasNext()) {
                collectors.next().updateType();
            }
            sender.sendMessage(Language.CONFIG_RELOADED.toString());
            return true;
        }
        if (args.length != 3) {
            sender.sendMessage(configUtil.getMessage(Language.COMMAND_USAGE.toString(), MessageType.FAILURE));
            return false;
        }
        Player target;
        if ((target = Bukkit.getPlayer(args[1])) == null) {
            sender.sendMessage(configUtil.getMessage(Language.COMMANDS_PLAYER_NOT_FOUND.toString(), MessageType.FAILURE));
            return false;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                sender.sendMessage(configUtil.getMessage(Language.COMMANDS_MUST_BE_INTEGER.toString(), MessageType.FAILURE));
                return false;
            }
            sender.sendMessage(configUtil.getMessage(Language.COLLECTOR_GIVE.toString(), MessageType.SUCCESS).replace("%player%", target.getName()).replace("%amount%", String.valueOf(amount)).replace("%collectorname%", configUtil.getCollectorMeta().getDisplayName()));
            ItemStack item = configUtil.getChunkCollector().clone();
            item.setAmount(amount);
            target.getInventory().addItem(item);
            return true;
        }
        catch (NumberFormatException error) {
            sender.sendMessage(configUtil.getMessage(Language.COMMANDS_MUST_BE_INTEGER.toString(), MessageType.FAILURE));
            return false;
        }
    }
}

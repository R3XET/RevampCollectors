package eu.revamp.collectors.license;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getConsoleSender;

public class AsyncCheckLicense {

    private String license_key;

    public AsyncCheckLicense() {
        this.license_key = RevampCollectors.getInstance().getConfigFile().getString("LICENSE");
    }

    public void asyncCheck(){
        new BukkitRunnable() {
            @Override
            public void run() {
                RevampLicense.ValidationType vt = new RevampLicense(license_key, "https://dev.revampmc.eu/license/verify.php", RevampCollectors.getInstance()).isValid();
                switch (vt) {
                    case VALID:
                        break;
                    case INVALID_PLUGIN:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            This license is for another plugin! &b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                    case URL_ERROR:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            HTTP request error during License verification! &b  "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                    case WRONG_RESPONSE:
                    case PAGE_ERROR:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            The License system has an error! &b                 "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                    case KEY_NOT_FOUND:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            This license key doesn't exists! &b                 "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                    case KEY_OUTDATED:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            The license key has been expired! &b                "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                    case NOT_VALID_IP:
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Your server's IP is invalid! &b                     "));
                        getConsoleSender().sendMessage(CC.translate("  #|       &4            Please contact the author R3XET#0852&b              "));
                        getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                        getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                        Bukkit.getPluginManager().disablePlugin(RevampCollectors.getInstance());
                        break;
                }
            }
        }.runTaskTimerAsynchronously((RevampCollectors.getInstance()), 6000L, 6000L);
    }
}

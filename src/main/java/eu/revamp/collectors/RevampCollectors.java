package eu.revamp.collectors;

import eu.revamp.collectors.commands.RevampCollectorsCommand;
import eu.revamp.collectors.enums.Language;
import eu.revamp.collectors.events.*;
import eu.revamp.collectors.file.ConfigFile;
import eu.revamp.collectors.file.LanguageFile;
import eu.revamp.collectors.license.AsyncCheckLicense;
import eu.revamp.collectors.license.RevampLicense;
import eu.revamp.collectors.managers.ChunkCollectorManager;
import eu.revamp.collectors.util.ChunkCollector;
import eu.revamp.collectors.util.CC;
import eu.revamp.collectors.util.ConfigUtil;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Iterator;

import static org.bukkit.Bukkit.getConsoleSender;

@Getter @Setter
public class RevampCollectors extends JavaPlugin {
    @Getter
    @Setter
    private static RevampCollectors instance;
    private ChunkCollectorManager chunkCollectorManager;
    public static Economy economy;
    private ConfigUtil configUtil;
    private ConfigFile configFile;
    private LanguageFile languageFile;
    private boolean valid;


    public void onEnable() {
        setInstance(this);
        setValid(false);
        setConfigFile(new ConfigFile());
        setLanguageFile(new LanguageFile());
        this.checkLicense();
        if (!(isValid())) {
            try {
                Bukkit.getPluginManager().disablePlugin(this);
            } catch (Exception ignored) {
            }
        } else {
            new AsyncCheckLicense().asyncCheck();
            this.loadLanguages();
            if (!this.setupEconomy()) {
                this.getLogger().severe("Cannot find Vault economy... disabling plugin.");
                this.getServer().getPluginManager().disablePlugin(this);
            }
            setChunkCollectorManager(new ChunkCollectorManager());
            getChunkCollectorManager().start();
            this.setupConfig();
            this.getCommand("revampcollectors").setExecutor(new RevampCollectorsCommand());
            this.registerHandlers();
        }
    }

    public void onDisable() {
        Iterator<ChunkCollector> collectors = this.getChunkCollectorManager().getChunkCollectors();
        while (collectors.hasNext()) {
            collectors.next().save();
        }
        Bukkit.getScheduler().cancelTasks(this);
    }


    private void registerHandlers() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new CollectorPlaceEvent(this), this);
        pluginManager.registerEvents(new CollectorInteractEvent(), this);
        pluginManager.registerEvents(new CollectorDestroyEvent(this), this);
        pluginManager.registerEvents(new CollectorEntitySpawnEvent(this), this);
        pluginManager.registerEvents(new CollectorCropEvent(this), this);
        pluginManager.registerEvents(new CollectorClickEvent(this), this);
        pluginManager.registerEvents(new CollectorHopperEvent(this), this);
    }

    private void setupConfig() {
        setConfigUtil(new ConfigUtil());
        getConfigUtil().setup();
        getConfigUtil().setLoaded(true);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            RevampCollectors.economy = economyProvider.getProvider();
        }
        return RevampCollectors.economy != null;
    }

    private void loadLanguages() {
        if (getLanguageFile() == null) {
            return;
        }
        Arrays.stream(Language.values()).forEach(language -> {
            if (getLanguageFile().getString(language.getPath()) == null) {
                if (language.getValue() != null) {
                    getLanguageFile().set(language.getPath(), language.getValue());
                } else if (language.getListValue() != null && getLanguageFile().getStringList(language.getPath()) == null) {
                    getLanguageFile().set(language.getPath(), language.getListValue());
                }
            }
        });
        getLanguageFile().save();
        getLanguageFile().load();
    }

    private void checkLicense() {
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &e            This plugin is protected using a licence system! &b "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &e            Checking Licence... Please Wait...&b                "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
        String license_key = getConfigFile().getString("LICENSE");
        if (license_key.equals("XXXX-XXXX-XXXX-XXXX")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Go to config.yml to put your license key!");
            setValid(false);
            try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
        } else {
            RevampLicense.ValidationType vt = new RevampLicense(license_key, "https://api.revampdev.tk/verify.php", this).isValid();
            String clientName = new RevampLicense(license_key, "https://api.revampdev.tk/verify.php", this).clientName();
            switch (vt) {
                case VALID:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &a     License check successfully passed! &b                      "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &a     Thanks to " + clientName + " for purchasing! &b            "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &a     If you have issues please contact the author R3XET#0852&b  "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    setValid(true);
                    break;
                case INVALID_PLUGIN:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     This license is for another plugin! &b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
                    break;
                case URL_ERROR:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     HTTP request error during License verification! &b         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
                    break;
                case WRONG_RESPONSE:
                case PAGE_ERROR:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     The License system has an error! &b                        "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
                    break;
                case KEY_NOT_FOUND:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     This license key doesn't exists! &b                        "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
                    break;
                case KEY_OUTDATED:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     The license key has been expired! &b                       "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
                    break;
                case NOT_VALID_IP:
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Your server's IP is invalid! &b                            "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|       &4     Please contact the author R3XET#0852&b                     "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("  #|                                                                         "));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&b&m#+++______------______------______------______------______------______+++"));
                    try { Bukkit.getPluginManager().disablePlugin(this); } catch (Exception ignored) { }
                    setValid(false);
            }
        }
    }
}


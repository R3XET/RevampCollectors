package eu.revamp.collectors.enums;

import eu.revamp.collectors.RevampCollectors;
import eu.revamp.collectors.file.LanguageFile;
import eu.revamp.collectors.util.CC;
import eu.revamp.collectors.util.Replacement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum Language {
    //----PREFIX----//
    PREFIX("PREFIX", "&3&lRevampMC &8» "),
    PREFIX_SUCCESS("PREFIX_SUCCESS", "&a&l(!)&r "),
    PREFIX_FAILURE("PREFIX_FAILURE", "&4&l(x)&r "),
    //----USAGE-----//
    COMMAND_USAGE("COMMAND_USAGE", "&c/revampcollector give (player) (amount)."),
    //----COMMANDS-----//
    COMMANDS_FOR_PLAYER_USE_ONLY("COMMANDS.FOR_PLAYER_USE_ONLY", "%prefix% &cNo console."),
    COMMANDS_FOR_CONSOLE_ONLY("COMMANDS.FOR_CONSOLE_USE_ONLY", "%prefix% &cFor console only."),
    COMMANDS_NO_PERMISSION_MESSAGE("COMMANDS.NO_PERMISSION_MESSAGE", "%prefix% &cNo permission."),
    COMMANDS_PLAYER_NOT_FOUND("COMMANDS.PLAYER_NOT_FOUND", "%prefix% &cPlayer not found."),
    COMMANDS_MUST_BE_INTEGER("COMMANDS.MUST_BE_INTEGER", "%prefix% &cThis must be an integer!"),
    //----CONFIG-----//
    CONFIG_LOADING("CONFIG.LOADING", "&cPlease wait, the config file is still loading..."),
    CONFIG_RELOADED("CONFIG.RELOADED", "&aConfig has been reloaded."),
    //----COLLECTOR-----//
    COLLECTOR_GIVE("COLLECTOR.GIVE", "&aYou gave %player%: &f%amount%x %collectorname%"),
    COLLECTOR_PLACE("COLLECTOR.PLACE", "&7You have placed the %collectorname%"),
    COLLECTOR_BREAK("COLLECTOR.BREAK", "&7You have broken the %collectorname%"),
    COLLECTOR_SELL("COLLECTOR.SELL", "&aSuccessfully sold &f%amount%x %name% &afor &f$%price%."),
    COLLECTOR_SELL_ALL("COLLECTOR.SELL_ALL", "&aYou sold all items inside the Collector"),
    COLLECTOR_XP_REDEEMED("COLLECTOR.XP_REDEEMED", "&aYou are now level: &f%level%."),
    COLLECTOR_CANNOT_PLACE("COLLECTOR.CANNOT_PLACE", "&cYou must be in a faction to place a chunk collector."),
    COLLECTOR_CANNOT_PLACE_WILDERNESS("COLLECTOR.CANNOT_PLACE_WILDERNESS", "&cYou cannot place a %collectorname% &cin the &2Wilderness&c."),
    COLLECTOR_NO_BREAK_PLACE_INTERACT_PERMISSION("COLLECTOR.NO_PLACE_PERMISSION", "&cYou do not have permission to perform this action."),
    COLLECTOR_MAX_COLLECTORS_PER_CHUNK("COLLECTOR.MAX_COLLECTORS_PER_CHUNK", "&cYou cannot place two collectors in the same chunk."),
    COLLECTOR_TNT_BANK_DEPOSIT("COLLECTOR.TNT_BANK_DEPOSIT", "&aYou have deposited &f%amount%x TNT&a into your faction bank."),
    COLLECTOR_SAVED("COLLECTOR.SAVED", "&aSuccessfully saved %amount% chunk collectors."),

    END("", "");

    private String path;
    private String value;
    private List<String> listValue;

    private LanguageFile languageFile = RevampCollectors.getInstance().getLanguageFile();

    Language(String path, String value) {
        this.path = path;
        this.value = value;
        this.listValue = new ArrayList<>(Collections.singletonList(value));
    }

    public String toString() {
        Replacement replacement = new Replacement(CC.translate(languageFile.getString(this.path)));
        replacement.add("%prefix% ", languageFile.getString("PREFIX"));
        return replacement.toString().replace(" %newline% ", "\n");
    }
}

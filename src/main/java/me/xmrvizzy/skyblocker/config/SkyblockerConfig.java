package me.xmrvizzy.skyblocker.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

@Config(name = "skyblocker")
public class SkyblockerConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    @ConfigEntry.Category("locations")
    @ConfigEntry.Gui.TransitiveObject
    public Locations locations = new Locations();

    @ConfigEntry.Category("messages")
    @ConfigEntry.Gui.TransitiveObject
    public Messages messages = new Messages();

    public static class General {
        public String apiKey;

        @ConfigEntry.Category("bars")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Bars bars = new Bars();

        @ConfigEntry.Gui.Excluded
        public List<Integer> lockedSlots = new ArrayList<>();
    }

    public static class Bars {
        public boolean enableBars = true;
    }

    public static class Locations {
        @ConfigEntry.Category("dungeons")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Dungeons dungeons = new Dungeons();

        @ConfigEntry.Category("dwarvenmines")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public DwarvenMines dwarvenMines = new DwarvenMines();
    }

    public static class Dungeons {
        public boolean enableMap = true;
        public boolean solveThreeWeirdos = true;
    }

    public static class DwarvenMines {
        public boolean enableDrillFuel = true;
        public boolean solveFetchur = true;
        public boolean solvePuzzler = true;
    }

    public static class Messages {
	    @ConfigEntry.Gui.TransitiveObject
	    public ChatFilter blocksInTheWay;
	    @ConfigEntry.Gui.TransitiveObject
	    public ChatFilter abilityCooldown;
	    @ConfigEntry.Gui.TransitiveObject
	    public ChatFilter aoeDamage;
	    @ConfigEntry.Gui.TransitiveObject
	    public ChatFilter healing;
	    public Messages() {
		    String number = "[0-9]{1-3}(,[0-9]{3})*(.[0-9])?";
		    blocksInTheWay = new ChatFilter("^There are blocks in the way!$");
		    abilityCooldown = new ChatFilter("^This ability is( currently)? on cooldown for " + number + "( more )?s(econds?)?\\.$|^No more charges, next one in " + number + "s!$");
		    aoeDamage = new ChatFilter("^Your [a-zA-Z ]+ hit " + number + " enem(y|ies) for  " + number + " damage\\.$");
		    healing = new ChatFilter("^(You|[a-zA-Z0-9_]{1,16}) healed you(rself)? for " + number + " health!$");
	    }
	    public boolean shouldFilter(String msg) {
		for(Field f : this.getClass().getFields()) {
		    try {
			if(f.get(this) instanceof ChatFilter && ((ChatFilter)f.get(this)).shouldFilter(msg))
			    return true;
		    }
		    catch(IllegalAccessException e) {
		    }
		}
		return false;
	    }
    }

    public static void init() {
        AutoConfig.register(SkyblockerConfig.class, GsonConfigSerializer::new);
    }

    public static SkyblockerConfig get() {
        return AutoConfig.getConfigHolder(SkyblockerConfig.class).getConfig();
    }
}

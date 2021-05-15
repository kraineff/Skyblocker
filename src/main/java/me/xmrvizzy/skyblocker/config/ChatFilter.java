package me.xmrvizzy.skyblocker.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ChatFilter {
	@ConfigEntry.Gui.Excluded
	private final String regex;
	public boolean enabled;

	public ChatFilter(String regex) {
		this.regex = regex;
		enabled = false;
	}
	
	public boolean shouldFilter(String message) {
		return enabled && message.matches(regex);
	}
}


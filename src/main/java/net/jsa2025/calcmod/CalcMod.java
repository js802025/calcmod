package net.jsa2025.calcmod;

import io.papermc.lib.PaperLib;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.jsa2025.calcmod.commands.CalcCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CalcMod extends JavaPlugin {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("calcmod");

	@Override
	public void onEnable() {
		PaperLib.suggestPaper(this);
		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			// register your commands here ...
			CalcCommand.register(commands.registrar().getDispatcher());
		});

		saveDefaultConfig();
	}

}

package net.jsa2025.calcmod;



import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.subcommands.Basic;

import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
//import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = CalcMod.MODID)
public class CalcMod  {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "calcmod";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public CalcMod() {
		LOGGER.debug("Registering CalcMod");
	}
	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@Mod.EventHandler
	public void start(FMLServerStartingEvent event) {
		event.registerServerCommand(new CalcCommand());
	}

}

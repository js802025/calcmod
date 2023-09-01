package net.jsa2025.calcmod;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jsa2025.calcmod.commands.CalcCommand;
import net.jsa2025.calcmod.commands.subcommands.Basic;
import net.minecraft.command.CommandSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(CalcMod.MODID)
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
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class ClientModEvents
	{
		@SubscribeEvent
		public static void registerCommands(FMLServerStartingEvent event) {

			LOGGER.debug("Registering CalcMod");
			CalcCommand.registerServer(event.getCommandDispatcher(), false);
		}
	}
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
	public static class ServerModEvents
	{
		@SubscribeEvent
		public static void onServerStart(FMLServerStartingEvent event)
		{
			LOGGER.debug("Registering CalcMod");
			CalcCommand.registerServer(event.getServer().getCommands().getDispatcher(), true);
		}
	}

}

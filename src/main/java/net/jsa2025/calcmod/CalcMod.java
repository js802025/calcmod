package net.jsa2025.calcmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
//import net.fabricmc.fabric.api.command.v1;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.jsa2025.calcmod.commands.CalcCommand;

import java.util.logging.Logger;


public class CalcMod implements DedicatedServerModInitializer, ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final Logger LOGGER = Logger.getLogger("calcmod");
	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//CommandRegistrationCallback.DISPATCHER.register(CalcCommand.register());
		CommandRegistrationCallback.EVENT.register(CalcCommand::registerServer);
	}
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		CommandRegistrationCallback.EVENT.register(CalcCommand::registerServer);
		
	}
}

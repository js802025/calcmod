package net.jsa2025.calcmod;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.jsa2025.calcmod.commands.CalcCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcMod implements ClientModInitializer, DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
			ClientCommandRegistrationCallback.EVENT.register(CalcCommand::register);

	}
	@Override
	
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
			CommandRegistrationCallback.EVENT.register(CalcCommand::registerServer);

	}

}

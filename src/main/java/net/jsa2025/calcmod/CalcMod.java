package net.jsa2025.calcmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.jsa2025.calcmod.commands.CalcCommand;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.contents.KeybindResolver;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcMod implements ClientModInitializer, DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
//	public static final Logger LOGGER = LoggerFactory.getLogger("calcmod");

	private static KeyMapping keyBinding;
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("calcmod", "category"));	@Override

	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
			ClientCommandRegistrationCallback.EVENT.register(CalcCommand::register);
		keyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"Open CalcMod", // The translation key of the keybinding's name
				InputConstants.Type.KEYBOARD, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				InputConstants.UNKNOWN.getValue(), // The keycode of the key
				CATEGORY // The category of the key - you'll need to add a translation for this!
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.consumeClick()) {
				client.gui.setScreen(new ChatScreen("/calc ", false));
			}
		});

	}
	@Override
	
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
			CommandRegistrationCallback.EVENT.register(CalcCommand::registerServer);

	}

}

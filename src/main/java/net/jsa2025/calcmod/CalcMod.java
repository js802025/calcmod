package net.jsa2025.calcmod;

import net.jsa2025.calcmod.commands.CalcCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("calcmod")
public class CalcMod {
    public static final String MODID = "calcmod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public CalcMod() {
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
            LOGGER.debug("Registering CalcMod");
            CalcCommand.registerServer(event.getDispatcher());
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents {
        @SubscribeEvent
        public static void onServerStart(ServerStartingEvent event) {
            LOGGER.debug("Registering CalcMod");
            CalcCommand.registerServer(event.getServer().getCommands().getDispatcher());
        }
    }
}

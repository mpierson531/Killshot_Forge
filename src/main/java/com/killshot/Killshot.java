package com.killshot;

import com.killshot.config.KillshotConfigModel;
import com.killshot.config.KillshotConfigScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Mod(Killshot.MODID)
public class Killshot {
    public static final String MODID = "killshot_forge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void logInfo(final String info) {
        LOGGER.info(info);
    }

    public static void logWarning(final String warning) {
        LOGGER.warn(warning);
    }

    public static void logError(final String error) {
        LOGGER.error(error);
    }

    private ComplexKey binding;
    private Entity playerEntity;
    private String playerName;
    private boolean isInWorld;
    public final KillshotConfigModel config;
    private static Killshot INSTANCE;

    public static Killshot getInstance() {
        return INSTANCE;
    }

    public Killshot() {
        binding = null;
        playerEntity = null;
        playerName = null;
        isInWorld = false;

        final ModLoadingContext loadingContext = ModLoadingContext.get();

        final Pair<KillshotConfigModel, ForgeConfigSpec> pair = KillshotConfigModel.init();
        config = pair.getLeft();
        loadingContext.registerConfig(ModConfig.Type.CLIENT, pair.getRight(), KillshotConfigModel.CONFIG_FILE_NAME);

        loadingContext.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, previousScreen) -> new KillshotConfigScreen(previousScreen))
        );

        INSTANCE = this;
    }

    private void initPlayerName() {
        Killshot.logInfo("Initializing player entity...");

        playerName = Minecraft.getInstance().getUser().getName();

        Killshot.logInfo("Player entity initialized!");
    }

    private void initBinding(RegisterKeyMappingsEvent event) {
        Killshot.logInfo("Initializing kill key...");

        binding = ComplexKey.getDefaultBinding().register(event);

        Killshot.logInfo("Kill key initialized!");
    }

    public void respawn(final MinecraftServer server) {
        final GameRules.BooleanValue respawnImmediately = server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN);

        if (!respawnImmediately.get()) {
            respawnImmediately.set(true, server);
            kill(server);
            respawnImmediately.set(false, server);
        } else {
            kill(server);
        }
    }

    public void kill(final MinecraftServer server) {
        playerEntity = getPlayer(server);
        playerEntity.kill();
    }

    public static MinecraftServer getServer() {
        return Minecraft.getInstance().getSingleplayerServer();
    }

    private Entity getPlayer(final MinecraftServer server) {
        return server.getPlayerList().getPlayerByName(playerName);
    }

    public boolean isInWorld() {
        return isInWorld;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Killshot.getInstance().initPlayerName();
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            Killshot.getInstance().initBinding(event);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeModEvents {
        @SubscribeEvent
        public static void onLoad(LevelEvent.Load loadEvent) {
            Killshot.getInstance().isInWorld = true;
        }

        @SubscribeEvent
        public static void onUnload(LevelEvent.Unload unloadEvent) {
            Killshot.getInstance().isInWorld = false;
        }

        @SubscribeEvent
        public static void onClientTick(final ClientTickEvent tickEvent) {
            if (!Killshot.getInstance().isInWorld) {
                return;
            }

            if (tickEvent.phase != TickEvent.Phase.END) {
                return;
            }

            if (Killshot.getInstance().binding.wasPressed()) {
                if (Killshot.getInstance().config.respawnImmediately()) {
                    Killshot.getInstance().respawn(getServer());
                } else {
                    Killshot.getInstance().kill(getServer());
                }
            }
        }
    }
}
package com.lunofe.teamlapenfaction2luckpermsgroup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mod(Teamlapenfaction2luckpermsgroup.MODID)
public class Teamlapenfaction2luckpermsgroup {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "teamlapenfaction2luckpermsgroup";

    public Teamlapenfaction2luckpermsgroup() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
    }

    public void onPlayerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {

        // wait a couple of seconds
        if (ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer server) {
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {

                // get player name
                String playerName = event.getEntity().getName().getString();

                // load player data
                CompoundTag playerData = new CompoundTag();
                event.getEntity().saveWithoutId(playerData);

                // extract levels
                String factionData = "";
                int levelData = 0, lordData = 0;
                try {
                    factionData = playerData.getCompound("ForgeCaps").getCompound("vampirism:ifactionplayerhandler").getString("faction");
                } catch (Exception ignored) { }
                try {
                    levelData = playerData.getCompound("ForgeCaps").getCompound("vampirism:ifactionplayerhandler").getInt("level");
                } catch (Exception ignored) { }
                try {
                    lordData = playerData.getCompound("ForgeCaps").getCompound("vampirism:ifactionplayerhandler").getInt("lord_level");
                } catch (Exception ignored) { }
                int aegingData = playerData.getCompound("ForgeCaps").getCompound("vampiricageing:ageing").getInt("age");

                // map faction and color
                String faction;
                String suffix = "";
                switch (factionData) {
                    case "vampirism:vampire" -> {
                        faction = "vampire";
                        suffix = "&5";
                    }
                    case "vampirism:hunter" -> {
                        faction = "hunter";
                        suffix = "&9";
                    }
                    case "werewolves:werewolf" -> {
                        faction = "werewolf";
                        suffix = "&6";
                    }
                    default -> faction = "human";
                }

                // unset all groups
                String[] groups = {"human", "vampire", "werewolf", "hunter"};
                for (String group : groups) {
                    if (!group.equals(faction)) {
                        server.handleConsoleInput("lp user " + playerName + " permission unset group." + group, server.createCommandSourceStack());
                    }
                }
                // set the appropriate group
                server.handleConsoleInput("lp user " + playerName + " permission set group." + faction, server.createCommandSourceStack());

                // map levels and ranks
                String[] levels = { "❶", "❷", "❸", "❹", "❺", "❻", "❼","❽", "❾", "❿", "⓫", "⓬", "⓭", "⓮" };
                String[] lords = { "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ" };
                String[] ages = {"𝟙", "𝟚", "𝟛", "𝟜", "𝟝"};
                if (levelData != 0) {
                    suffix += levels[levelData-1];
                }
                if (lordData != 0) {
                    suffix += lords[lordData-1];
                }
                if (aegingData != 0) {
                    suffix += ages[aegingData-1];
                }
                if (faction.equals("human")) {
                    server.handleConsoleInput("lp user " + playerName + " meta setsuffix 1 &r", server.createCommandSourceStack());
                } else {
                    server.handleConsoleInput("lp user " + playerName + " meta setsuffix 1 " + suffix, server.createCommandSourceStack());
                }
            });
        }
    }
}

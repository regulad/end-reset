package me.teakivy.endreset.Utils;

import me.teakivy.endreset.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;
import java.util.Random;

public class EndReset {

    static Main main = Main.getPlugin(Main.class);

    public static void resetEnd(CommandSender sender, String worldName) {
        Objects.requireNonNull(Bukkit.getWorld(worldName)).save();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(worldName)) {
                Location spawn = player.getBedSpawnLocation();
                if (spawn == null) spawn = Objects.requireNonNull(Bukkit.getWorld(worldName)).getSpawnLocation();
                player.teleport(spawn);
            }
        }
        if (Main.devMode) Main.logger.info("Removed players from " + worldName);
        for (
                Chunk chunk : Objects.requireNonNull(Bukkit.getWorld(worldName)).getLoadedChunks()) {
            chunk.unload();
        }
        if (Main.devMode) Main.logger.info("Unloaded all chunks in " + worldName);

        Bukkit.getServer().unloadWorld(worldName, true);
        if (Main.devMode) Main.logger.info("Unloaded " + worldName);

        File worldTheEnd = new File(Main.getPlugin(Main.class).getServer().getWorldContainer(), worldName);

        File regionDir = new File(worldTheEnd + "/DIM1/region");

        if (!regionDir.exists()) {
            if (sender != null)
                sender.sendMessage(ChatColor.RED + "The world " + worldName + " has an improper file scheme!");
            Main.logger.severe("The world " + worldName + " has an improper file scheme!");
            return;
        }

        if (Main.devMode) Main.logger.info("Found Region Folder " + worldName);

        for (File file1 : Objects.requireNonNull(regionDir.listFiles()))
                if (!file1.isDirectory())
                if (!main.getConfig().getStringList("regions-to-save").contains(file1.getName())) {
                    file1.delete();
                    if (Main.devMode) Main.logger.info("Deleted " + file1.getName());
                } else {
                    if (Main.devMode) Main.logger.info("Skipped " + file1.getName());
                }

        File entityDir = new File(worldTheEnd + "/DIM1/entities");

        if (!entityDir.exists()) {
            if (sender != null) sender.sendMessage(ChatColor.RED + "The world " + worldName + " has an improper file scheme!");
            Main.logger.severe("The world " + worldName + " has an improper file scheme!");
            return;
        }

        if (Main.devMode) Main.logger.info("Found Entity Folder " + worldName);

        for (File file1 : Objects.requireNonNull(entityDir.listFiles()))
            if (!file1.isDirectory())
                if (!main.getConfig().getStringList("regions-to-save").contains(file1.getName())) {
                    file1.delete();
                    if (Main.devMode) Main.logger.info("Deleted " + file1.getName());
                } else {
                    if (Main.devMode) Main.logger.info("Skipped " + file1.getName());
                }

        File levelDat = new File(worldTheEnd, "level.dat");
        levelDat.delete();
        if (Main.devMode) Main.logger.info("Deleted " + levelDat);

        File levelDatOld = new File(worldTheEnd, "level.dat_old");
        levelDatOld.delete();
        if (Main.devMode) Main.logger.info("Deleted " + levelDatOld);

        WorldCreator wcEnd = new WorldCreator(worldName);
        wcEnd.seed((new Random()).nextLong());  // make sure new chunks have a new seed
        wcEnd.environment(World.Environment.THE_END);
        wcEnd.createWorld();
        if (Main.devMode) Main.logger.info("Loaded " + worldName);

        if (sender != null) {
            sender.sendMessage(ChatColor.GREEN + "The End has been reset!");
        }

        Main.logger.info("Reset " + worldName);
    }
}

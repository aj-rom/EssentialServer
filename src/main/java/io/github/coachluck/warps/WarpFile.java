package io.github.coachluck.warps;

import io.github.coachluck.EssentialServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class WarpFile {
    private EssentialServer plugin;
    private YamlConfiguration warpData;
    public WarpFile(EssentialServer instance) {
        plugin = instance;
        warpData = YamlConfiguration.loadConfiguration(plugin.warpDataFile);
    }

    public Location getLocation(String warpName) {
        return (Location) warpData.get("warps." + warpName.toLowerCase() + ".location");
    }

    public void setLocation(String warpName, Location location) {
        warpData.set("warps." + warpName.toLowerCase() + ".location", location);
        saveFile();
    }

    public boolean addWarp(String name, Location location) {
        if(warpData.contains("warps." + name)) return false;
        else {
            warpData.createSection("warps." + name);
            warpData.set("warps." + name + ".name", name);
            warpData.set("warps." + name + ".on-warp-message", "&7Successfully warped to &e" + name);
            warpData.set("warps." + name + ".on-warp-sound", Sound.BLOCK_PORTAL_TRAVEL.toString());
            warpData.set("warps." + name + ".location", location);
            saveFile();
            return true;
        }
    }

    public Sound getSound(String name) {
        String sound = warpData.getString("warps." + name + ".on-warp-sound");
        return Sound.valueOf(sound);
    }

    public Set<String> getAllWarps() {
        return warpData.getConfigurationSection("warps").getKeys(false);
    }

    public String getWarpMessage(String name) {
        return warpData.getString("warps." + name + ".on-warp-message");
    }

    public void removeWarp(String name) {
        warpData.set("warps." + name, null);
        saveFile();
    }

    /**
     * Saves the message file
     */
    public void saveFile() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                warpData.save(plugin.warpDataFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save the warps.yml file!");
            }
        });
    }
}

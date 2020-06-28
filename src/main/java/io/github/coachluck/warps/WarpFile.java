/*
 *     File: WarpFile.java
 *     Last Modified: 6/28/20, 2:54 PM
 *     Project: EssentialServer
 *     Copyright (C) 2020 CoachL_ck
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.coachluck.warps;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class WarpFile {

    private final EssentialServer plugin;
    @Getter final YamlConfiguration warpData;
    private final File warpSaveFile;

    public WarpFile() {
        plugin = EssentialServer.getPlugin(EssentialServer.class);
        warpSaveFile = new File(plugin.getDataFolder(), "warps.yml");
        if(!plugin.getDataFolder().exists()) {
            if(!plugin.getDataFolder().mkdirs()) {
                ChatUtils.logMsg("&cError creating plugin files!");
            }
        }
        if(!warpSaveFile.exists()) {
            plugin.saveResource("warps.yml", false);
        }
        warpData = YamlConfiguration.loadConfiguration(warpSaveFile);
    }

    public Location getLocation(String warpName) {
        return (Location) warpData.get("warps." + warpName.toLowerCase() + ".location");
    }

    public String getDisplayName(String warpName) {
        return warpData.getString("warps." + warpName.toLowerCase() + ".name");
    }

    public void setLocation(String warpName, Location location) {
        warpData.set("warps." + warpName.toLowerCase() + ".location", location);
        saveFile();
    }

    public boolean addWarp(String name, Location location) {
        String key = name.toLowerCase();
        if(warpData.contains("warps." + key)) return false;
        else {
            warpData.createSection("warps." + key);
            warpData.set("warps." + key + ".name", name);
            warpData.set("warps." + key + ".on-warp-message", warpData.getString("messages.default-warp"));
            warpData.set("warps." + key + ".on-warp-sound", warpData.getString("default-sound"));
            warpData.set("warps." + key + ".location", location);
            saveFile();
            return true;
        }
    }

    public Sound getSound(String name) {
        String sound = warpData.getString("warps." + name + ".on-warp-sound");
        return Sound.valueOf(sound);
    }

    public List<WarpHolder> getAllWarps() {
        List<WarpHolder> warpList = new ArrayList<>();
        for(String name : getAllWarpKeys()) {
            WarpHolder warp = new WarpHolder();
            warp.setDisplayName(getDisplayName(name));
            warp.setLocation(getLocation(name));
            warp.setWarpSound(getSound(name));
            warp.setWarpMessage(getWarpMessage(name));
            warpList.add(warp);
        }

        return warpList;
    }

    public Set<String> getAllWarpKeys() {
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
                warpData.save(warpSaveFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save the warps.yml file!");
            }
        });
    }
}

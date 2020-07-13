/*
 *     File: EssentialServer.java
 *     Last Modified: 7/13/20, 1:42 AM
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

package io.github.coachluck;

import io.github.coachluck.commands.Burn;
import io.github.coachluck.commands.Clear;
import io.github.coachluck.commands.DelWarp;
import io.github.coachluck.commands.Feed;
import io.github.coachluck.commands.Fly;
import io.github.coachluck.commands.God;
import io.github.coachluck.commands.Heal;
import io.github.coachluck.commands.Help;
import io.github.coachluck.commands.IGameMode;
import io.github.coachluck.commands.InvSee;
import io.github.coachluck.commands.Kill;
import io.github.coachluck.commands.SetSpawn;
import io.github.coachluck.commands.SetWarp;
import io.github.coachluck.commands.Smite;
import io.github.coachluck.commands.Spawn;
import io.github.coachluck.commands.Teleport;
import io.github.coachluck.commands.Vanish;
import io.github.coachluck.commands.Warp;
import io.github.coachluck.events.PlayerJoinLeave;
import io.github.coachluck.tabcompleters.PlayerTabList;
import io.github.coachluck.tabcompleters.TabList;
import io.github.coachluck.utils.ChatUtils;
import io.github.coachluck.utils.UpdateChecker;
import io.github.coachluck.warps.WarpFile;
import io.github.coachluck.warps.WarpHolder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EssentialServer extends JavaPlugin {
    public boolean updateMsg = false;
    public final String pMsg = ChatUtils.format(this.getConfig().getString("permission-message"));
    public final String offlinePlayerMessage = ChatUtils.format(this.getConfig().getString("offline-player"));
    public ArrayList<UUID> vanish_players = new ArrayList<>();

    public HashMap<String, WarpHolder> warpMap = new HashMap<>();
    @Getter @Setter private WarpFile warpFile;

    @Override
    public void onEnable() {
        createDirectories();
        reloadWarpsMap();
        registerEvents();
        enableCommands();
        enableCommandP();
        enableCommandTabs();
        checkUpdate();
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
        warpMap.clear();
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinLeave(this), this);
    }

    private void enableCommands() {
        this.getCommand("esHelp").setExecutor(new Help(this));
        this.getCommand("es").setExecutor(new Help(this));
        this.getCommand("Spawn").setExecutor(new Spawn(this));
        this.getCommand("SetSpawn").setExecutor(new SetSpawn());
        this.getCommand("Smite").setExecutor(new Smite(this));
        this.getCommand("Fly").setExecutor(new Fly(this));
        this.getCommand("Feed").setExecutor(new Feed(this));
        this.getCommand("Heal").setExecutor(new Heal(this));
        this.getCommand("God").setExecutor(new God(this));
        this.getCommand("Kill").setExecutor(new Kill(this));
        this.getCommand("Clear").setExecutor(new Clear(this));
        this.getCommand("Burn").setExecutor(new Burn(this));
        this.getCommand("GameMode").setExecutor(new IGameMode(this));
        this.getCommand("Teleport").setExecutor(new Teleport(this));
        this.getCommand("Vanish").setExecutor(new Vanish(this));
        this.getCommand("SetSpawn").setExecutor(new Spawn(this));
        this.getCommand("InvSee").setExecutor(new InvSee(this));
        this.getCommand("warp").setExecutor(new Warp(this));
        this.getCommand("setwarp").setExecutor(new SetWarp(this));
        this.getCommand("delwarp").setExecutor(new DelWarp(this));
    }

    /**
     * Sets the permission message
     */
    private void enableCommandP() {
        this.getServer().getPluginCommand("es").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("esHelp").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Clear").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Smite").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Fly").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("God").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Kill").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Heal").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Feed").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("gameMode").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Teleport").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Vanish").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Burn").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("Spawn").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("SetSpawn").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("InvSee").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("setWarp").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("warp").setPermissionMessage(pMsg);
        this.getServer().getPluginCommand("delwarp").setPermissionMessage(pMsg);
    }

    /**
     * Enables command Tab Completion
     */
    private void enableCommandTabs() {
        this.getCommand("gameMode").setTabCompleter(new TabList(this));
        this.getCommand("esHelp").setTabCompleter(new TabList(this));
        this.getCommand("InvSee").setTabCompleter(new TabList(this));
        this.getCommand("Teleport").setTabCompleter(new TabList(this));
        this.getCommand("SetSpawn").setTabCompleter(new TabList(this));
        this.getCommand("Clear").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Spawn").setTabCompleter(new PlayerTabList(this));
        this.getCommand("God").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Fly").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Heal").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Feed").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Vanish").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Burn").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Smite").setTabCompleter(new PlayerTabList(this));
        this.getCommand("Kill").setTabCompleter(new PlayerTabList(this));
    }

    /**
     * Checks for plugin updates
     */
    private void checkUpdate() {
        if(!getConfig().getBoolean("check-updates"))
            return;

        new UpdateChecker(this, 71299).getVersion(version -> {
            int old = Integer.parseInt(this.getDescription().getVersion().replaceAll("\\.", ""));
            int newVer = Integer.parseInt(version.replaceAll("\\.", ""));
            if (old >= newVer) {
                ChatUtils.logMsg("&bYou are running the latest version.");
                return;
            }

            ChatUtils.logMsg("&aThere is a new update available.");
        });
    }

    /**
     * Creates the configuration and warps files
     */
    private void createDirectories() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        warpFile = new WarpFile();
    }

    /**
     * Reloads the active warp list for the server
     */
    public void reloadWarpsMap() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if(!warpMap.isEmpty()) warpMap.clear();
            for(String s : warpFile.getAllWarpKeys()) {
                warpMap.put(s, new WarpHolder(
                                warpFile.getLocation(s),
                                warpFile.getSound(s),
                                warpFile.getWarpMessage(s),
                                warpFile.getDisplayName(s)));
            }
        });
    }

    public String getOfflinePlayerMessage(String name) {
        return offlinePlayerMessage.replaceAll("%player%", name);
    }
}
/*
 *     File: Spawn.java
 *     Last Modified: 7/14/20, 12:48 AM
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

package io.github.coachluck.commands;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Spawn implements CommandExecutor {
    private final EssentialServer plugin;
    public Spawn(EssentialServer plugin) {
        this.plugin = plugin;
        if(plugin.getConfig().getString("spawn.world") == null) {
            World world = Bukkit.getServer().getWorlds().get(0);
            setLocation(world.getSpawnLocation());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("setspawn")) {
            if(!(sender instanceof Player)) {
                ChatUtils.logMsg("&cYou must be a player to do this!");
                return true;
            }

            Player p = (Player) sender;

            plugin.getConfig().set("spawn.world", p.getWorld().getName());
            plugin.getConfig().set("spawn.x", p.getLocation().getX());
            plugin.getConfig().set("spawn.y", p.getLocation().getY());
            plugin.getConfig().set("spawn.z", p.getLocation().getZ());
            plugin.getConfig().set("spawn.yaw", p.getLocation().getYaw());
            plugin.getConfig().set("spawn.pitch", p.getLocation().getPitch());
            plugin.getConfig().set("spawn.last-saved-by", p.getName());
            plugin.saveConfig();

            World world = p.getWorld();
            world.setSpawnLocation(p.getLocation());
            ChatUtils.msg(p, "&7You have &asuccessfully set the spawn.");
            return true;
        }

        String spawnMsg = plugin.getConfig().getString("spawn.spawn-message");
        String spawnOtherMsg = plugin.getConfig().getString("spawn.other-message");
        final boolean enableMessage = plugin.getConfig().getBoolean("spawn.enable-message");

        Location spawn_loc = getSpawnLocation();
        switch (args.length) {
            case 0:
                if(!(sender instanceof Player)) {
                    ChatUtils.logMsg("&cIncorrect usage, try &e/spawn &7[&bplayer&7]");
                    return true;
                }
                ((Player) sender).teleport(spawn_loc);
                if(enableMessage) {
                    ChatUtils.msg(sender, spawnMsg);
                }
                return true;
            case 1:
                if (!sender.hasPermission("essentialserver.spawn.others")) {
                    ChatUtils.sendMessage(sender, plugin.pMsg);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }

                target.teleport(spawn_loc);
                if (enableMessage) {
                    ChatUtils.msg(target, spawnMsg);
                    ChatUtils.msg(sender, spawnOtherMsg.replaceAll("%player%", target.getName()));
                }
                return true;
            default:
                String syntax = "&cIncorrect Syntax! &eTry /spawn";
                if(sender.hasPermission("essentialserver.spawn.others")) {
                    syntax = syntax + " or /spawn <player>";
                }
                ChatUtils.msg(sender, syntax);
                return true;
        }
    }

    /**
     * Gets the spawn location from the config
     * @return the Bukkit Location of the spawn
     */
    private Location getSpawnLocation() {
        EssentialServer plugin = EssentialServer.getPlugin(EssentialServer.class);
        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));
        double X = plugin.getConfig().getDouble("spawn.x");
        double Y = plugin.getConfig().getDouble("spawn.y");
        double Z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");
        return new Location(world, X, Y, Z, yaw, pitch);
    }

    /**
     * Sets the location for the spawnpoint in the config
     * @param location the location to set it too.
     */
    private void setLocation(Location location) {
        EssentialServer plugin = EssentialServer.getPlugin(EssentialServer.class);
        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }
}

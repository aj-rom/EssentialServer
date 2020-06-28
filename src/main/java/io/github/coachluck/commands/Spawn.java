/*
 *     File: Spawn.java
 *     Last Modified: 6/28/20, 2:59 PM
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
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String spawnMsg = plugin.getConfig().getString("spawn.spawn-message");
        String spawnOtherMsg = plugin.getConfig().getString("spawn.other-message");
        boolean enableMessage = plugin.getConfig().getBoolean("spawn.enable-message");
        String offlinePlayer = plugin.getConfig().getString("offline-player");

        if(cmd.getName().equalsIgnoreCase("setspawn")) {
            if(!(sender instanceof Player)) {
                ChatUtils.logMsg("&cYou must be ingame to use this command!");
                return true;
            }

            Player p = (Player) sender;
            plugin.getConfig().set("spawn.world", p.getWorld().getName());
            plugin.getConfig().set("spawn.x", p.getLocation().getX());
            plugin.getConfig().set("spawn.y", p.getLocation().getY());
            plugin.getConfig().set("spawn.z", p.getLocation().getZ());
            plugin.getConfig().set("spawn.yaw", p.getLocation().getYaw());
            plugin.getConfig().set("spawn.pitch", p.getLocation().getPitch());
            plugin.saveConfig();
            ChatUtils.msg(p, "&aYou have successfully set the spawn.");
        }
        else if(cmd.getName().equalsIgnoreCase("spawn") && sender.hasPermission("essentialserver.spawn")) {

            if(plugin.getConfig().getString("spawn.world") == null) {
                ChatUtils.msg(sender, "&cPlease do &b/setspawn &cbefore you try and spawn!");
                return true;
            }

            World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));
            double X = plugin.getConfig().getDouble("spawn.x");
            double Y = plugin.getConfig().getDouble("spawn.y");
            double Z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");
            Location spawn_loc = new Location(world, X, Y, Z, yaw, pitch);

            if(args.length == 0) {
                if(!(sender instanceof Player)) {
                    ChatUtils.logMsg("&cInccorect usage, try &a/spawn [&bplayer&a]");
                    return true;
                }

                Player player = (Player) sender;
                if(enableMessage) ChatUtils.msg(player, spawnMsg);
                player.teleport(spawn_loc);
                return true;
            }
            if(args.length == 1) {
                if(!sender.hasPermission("essentialserver.spawn.others")) {
                    ChatUtils.msg(sender, plugin.pMsg);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null) {
                    ChatUtils.msg(sender, offlinePlayer.replaceAll("%player%", args[0]));
                }

                target.teleport(spawn_loc);
                if(enableMessage) {
                    ChatUtils.msg(target, spawnMsg);
                    ChatUtils.msg(sender, spawnOtherMsg.replaceAll("%player%", target.getName()));
                }
                return true;
            }
        }
        return true;
    }
}

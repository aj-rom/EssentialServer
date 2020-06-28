/*
 *     File: SetSpawn.java
 *     Last Modified: 6/28/20, 4:49 PM
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
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    private final EssentialServer plugin;

    public SetSpawn() {
        plugin = EssentialServer.getPlugin(EssentialServer.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            ChatUtils.logMsg("&cYou must be a player to use this command!");
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
}

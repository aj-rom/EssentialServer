/*
 *     File: SetWarp.java
 *     Last Modified: 6/28/20, 12:45 PM
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static io.github.coachluck.utils.ChatUtils.format;
import static io.github.coachluck.utils.ChatUtils.logMsg;

public class SetWarp implements TabCompleter, CommandExecutor {
    private EssentialServer plugin;

    public SetWarp(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 1) {
                String warpName = args[0].toLowerCase();
                if(plugin.warpMap.get(warpName) != null) {
                    ChatUtils.msg(p, "&e" + args[0] + " &7already exists! Please use a different name.");
                    return true;
                }
                if(plugin.getWarpFile().addWarp(args[0], p.getLocation())) {
                    ChatUtils.msg(p, plugin.getWarpFile().getWarpData().getString("messages.create-warp").replaceAll("%warp%", warpName));
                } else {
                    ChatUtils.msg(p, plugin.getWarpFile().getWarpData().getString("messages.warp-already-exists").replaceAll("%warp%", warpName));
                }
                plugin.reloadWarpsMap();

            } else {
                ChatUtils.msg(p, "&cIncorrect usage: &e/setwarp &b<WarpName>");
            }
        } else {
            logMsg("&cYou must be a player to use this command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1 && sender instanceof Player) {
            ArrayList<String> name = new ArrayList<>();
            name.add(format("&7<&eWarpName&7>"));
            return name;
        }
        return new ArrayList<>();
    }
}

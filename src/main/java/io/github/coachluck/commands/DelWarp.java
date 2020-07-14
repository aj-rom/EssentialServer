/*
 *     File: DelWarp.java
 *     Last Modified: 7/13/20, 11:34 PM
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelWarp implements CommandExecutor, TabCompleter {
    private final EssentialServer plugin;

    public DelWarp(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length != 1) {
            ChatUtils.msg(sender, "&cIncorrect Syntax. &eTry /delwarp &7<&bWarpName&7>");
            return true;
        }

        final String warpName = args[0].toLowerCase();
        if(!plugin.warpMap.containsKey(warpName)) {
            final String warpNotFound = plugin.getWarpFile().getWarpData().getString("messages.warp-not-found");
            ChatUtils.msg(sender, warpNotFound.replaceAll("%warp%", args[0]));
            return true;
        }

        plugin.getWarpFile().removeWarp(warpName);
        plugin.reloadWarpsMap();
        ChatUtils.msg(sender, "&7You have &cremoved &7warp: &e" + warpName);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length == 1) {
            ArrayList<String> warps = new ArrayList<>(plugin.warpMap.keySet());
            Collections.sort(warps);
            return warps;
        }

        return new ArrayList<>();

    }
}

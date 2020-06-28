/*
 *     File: PlayerTabList.java
 *     Last Modified: 1/25/20, 6:44 PM
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

package io.github.coachluck.tabcompleters;

import io.github.coachluck.EssentialServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerTabList implements TabCompleter {
    private EssentialServer plugin;

    public PlayerTabList(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tabs = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission(cmd.getPermission() + ".others")) {
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(!cmd.getName().equalsIgnoreCase("vanish")) {
                    if(!plugin.vanish_players.contains(p.getUniqueId())) tabs.add(p.getDisplayName());
                } else {
                    tabs.add(p.getDisplayName());
                }
            }
            Collections.sort(tabs);
            StringUtil.copyPartialMatches(args[0], tabs, new ArrayList<>());
            return tabs;
        }
        else return new ArrayList<>();
    }
}

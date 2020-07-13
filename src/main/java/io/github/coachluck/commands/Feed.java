/*
 *     File: Feed.java
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

package io.github.coachluck.commands;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Feed implements CommandExecutor {
    private final EssentialServer plugin;
    public Feed(EssentialServer plugin) {
        this.plugin = plugin; //stores plugin

    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String feedMsg = plugin.getConfig().getString("feed.message");
        String feedOtherMsg = plugin.getConfig().getString("feed.other-message");
        boolean enableMsg = plugin.getConfig().getBoolean("feed.message-enable");
        int amt = plugin.getConfig().getInt("feed.amount");

        if (args.length == 0) {
            if (sender instanceof Player && sender.hasPermission("essentialserver.feed")) {
                Player player = (Player) sender;
                if (enableMsg) ChatUtils.msg(player, feedMsg);
                int foodLvL = player.getFoodLevel();
                int finalAmt = foodLvL + amt;
                player.setFoodLevel(finalAmt);
            } else ChatUtils.logMsg("&cYou must be a player to execute this command!");
        } else if (args.length == 1 && sender.hasPermission("essentialserver.feed.others")) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                return true;
            }
            int foodLvL = target.getFoodLevel();
            int finalAmt = foodLvL + amt;
            target.setFoodLevel(finalAmt);
            ChatUtils.sendMessages(sender, feedMsg, feedOtherMsg, feedMsg, enableMsg, target);
        } else if (args.length > 1) {
            ChatUtils.msg(sender, "&cToo many arguments! Try /feed <player> or /feed.");
        }
        return true;
    }
}
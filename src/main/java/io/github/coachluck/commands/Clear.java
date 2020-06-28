/*
 *     File: Clear.java
 *     Last Modified: 4/10/20, 6:21 PM
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

public class Clear implements CommandExecutor {
    private EssentialServer plugin;

    public Clear(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String clearMsg = plugin.getConfig().getString("clear.message");
        String clearOtherMsg = plugin.getConfig().getString("clear.others-message");
        boolean enableMsg = plugin.getConfig().getBoolean("clear.message-enable");

        if (args.length == 0 && sender.hasPermission("essentialserver.clear")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.getInventory().clear();
                if (enableMsg)
                    ChatUtils.msg(player, clearMsg);
            } else
                ChatUtils.logMsg("&cYou must be a player to execute this command!");
        } else if (args.length == 1 && sender.hasPermission("essentialserver.clear.others")) {
            try {
                Player target = Bukkit.getPlayerExact(args[0]);
                target.getInventory().clear();
                ChatUtils.sendMessages(sender, clearMsg, clearOtherMsg, clearMsg, enableMsg, target);
            }
            catch (NullPointerException e) {
                ChatUtils.msg(sender, "&cThe specified player could not be found!");
            }
        } else if (args.length > 1) ChatUtils.msg(sender, "&cToo many arguments! Try /clear <player> or /clear.");
        return true;
    }
}
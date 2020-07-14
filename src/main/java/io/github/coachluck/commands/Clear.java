/*
 *     File: Clear.java
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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Clear implements CommandExecutor {
    private final EssentialServer plugin;

    public Clear(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String clearMsg = plugin.getConfig().getString("clear.message");
        String clearOtherMsg = plugin.getConfig().getString("clear.others-message");
        boolean enableMsg = plugin.getConfig().getBoolean("clear.message-enable");
        final String otherPerm = "essentialserver.clear.others";

        switch(args.length) {
            case 0:
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    player.getInventory().clear();
                    if (enableMsg)
                        ChatUtils.msg(player, clearMsg);
                    return true;
                }
                ChatUtils.logMsg( "&cYou must be a player to do this! &eTry /clear <player>");
                return true;
            case 1:
                if(!sender.hasPermission(otherPerm)) {
                    ChatUtils.sendMessage(sender, plugin.pMsg);
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }

                target.getInventory().clear();
                ChatUtils.sendMessages(sender, clearMsg, clearOtherMsg, clearMsg, enableMsg, target);
                return true;
            default:
                String syntax = "&cIncorrect syntax! &eTry /clear";
                if(sender.hasPermission(otherPerm)) {
                    syntax = syntax + " &cor &e/clear <player>";
                }
                ChatUtils.msg(sender, syntax);
                return true;
        }
    }
}
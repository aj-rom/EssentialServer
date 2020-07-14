/*
 *     File: Fly.java
 *     Last Modified: 7/13/20, 11:35 PM
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

public class Fly implements CommandExecutor {
    private final EssentialServer plugin;
    public Fly(EssentialServer plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String flyOtherOnMsg = plugin.getConfig().getString("fly.other-on-message");
        String flyOtherOffMsg = plugin.getConfig().getString("fly.other-off-message");
        final boolean enableMsg = plugin.getConfig().getBoolean("fly.message-enable");

        switch (args.length) {
            case 0:
                if(!(sender instanceof Player)) {
                    ChatUtils.logMsg("&cYou must be a player to do this! &eTry /fly <player>");
                    return true;
                }
                flightCheck((Player) sender);
                return true;
            case 1:
                if(!sender.hasPermission("essentialserver.fly.others")) {
                    ChatUtils.sendMessage(sender, plugin.pMsg);
                    return true;
                }
                Player tP = Bukkit.getPlayerExact(args[0]);
                if(tP == null) {
                    ChatUtils.msg(sender,plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }
                flightCheck(tP);
                if (enableMsg && !tP.getName().equals(sender.getName())) {
                    if (tP.getAllowFlight()) {
                        ChatUtils.msg(sender, flyOtherOnMsg.replace("%player%", tP.getDisplayName()));
                        return true;
                    }

                    ChatUtils.msg(sender, flyOtherOffMsg.replace("%player%", tP.getDisplayName()));
                }
                return true;
            default:
                String syntax = "&cIncorrect Syntax! &eTry /fly";
                if(sender.hasPermission("essentialserver.fly.others")) {
                    syntax = syntax + " or /fly <player>";
                }
                ChatUtils.msg(sender, syntax);
                return true;
        }
    }

    private void flightCheck(Player player) {
        String flyMsg = plugin.getConfig().getString("fly.on-message");
        String flyOffMsg = plugin.getConfig().getString("fly.off-message");
        final boolean enableMsg = plugin.getConfig().getBoolean("fly.message-enable");

        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            if (enableMsg)
                ChatUtils.msg(player, flyOffMsg.replace("%player%", player.getDisplayName()));
            return;
        }

        player.setAllowFlight(true);
        if (enableMsg)
            ChatUtils.msg(player, flyMsg.replace("%player%", player.getDisplayName()));

    }
}
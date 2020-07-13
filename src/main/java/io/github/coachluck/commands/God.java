/*
 *     File: God.java
 *     Last Modified: 7/13/20, 1:48 AM
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

public class God implements CommandExecutor {
    private final EssentialServer plugin;
    public God(EssentialServer plugin) {
        this.plugin = plugin; //stores plugin
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String godOtherMsg = plugin.getConfig().getString("god.others-on-message");
        String godOtherOffMsg = plugin.getConfig().getString("god.others-off-message");
        boolean enableMsg = plugin.getConfig().getBoolean("god.message-enable");

        if(args.length == 0 && sender.hasPermission("essentialserver.god")) {
            if(sender instanceof Player) godCheck((Player) sender);
            else ChatUtils.msg(sender, ChatUtils.format("&cYou must be a player to execute this command!"));
        }
        else if (args.length == 1 && sender.hasPermission("essentialserver.god.others")) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                return true;
            }

            godCheck(target);

            if(enableMsg && !target.getName().equals(sender.getName())) {
                if (target.isInvulnerable()) {
                    ChatUtils.msg(sender, godOtherMsg.replace("%player%", target.getDisplayName()));
                    return true;
                }

                ChatUtils.msg(sender, godOtherOffMsg.replace("%player%", target.getDisplayName()));
                return true;
            }
        }
        else if (args.length > 1) ChatUtils.msg(sender, "&cToo many arguments! Try /god <player> or /god.");
        return true;
    }

    private void godCheck(Player player) {
        String godMsg = plugin.getConfig().getString("god.on-message");
        String godOffMsg = plugin.getConfig().getString("god.off-message");
        boolean enableMsg = plugin.getConfig().getBoolean("god.message-enable");

        if(player.isInvulnerable()) {
            player.setInvulnerable(false);
            if (enableMsg) ChatUtils.msg(player, godOffMsg.replace("%player%", player.getDisplayName()));
        } else {
            player.setInvulnerable(true);
            if (enableMsg) ChatUtils.msg(player, godMsg.replace("%player%", player.getDisplayName()));
        }
    }
}

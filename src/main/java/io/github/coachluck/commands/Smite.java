/*
 *     File: Smite.java
 *     Last Modified: 7/13/20, 10:26 PM
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Smite implements CommandExecutor {
    private final EssentialServer plugin;

    public Smite(EssentialServer ins) {
        this.plugin = ins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String smiteMsg = plugin.getConfig().getString("smite.message");
        String smiteOtherMsg = plugin.getConfig().getString("smite.others-message");
        String selfMsg = plugin.getConfig().getString("smite.self-message");
        final boolean enableMsg = plugin.getConfig().getBoolean("smite.message-enable");
        final String permOther = "essentialserver.smite.others";

        switch (args.length) {
            case 0:
                if(!(sender instanceof Player)) {
                    ChatUtils.msg(sender, "&cYou must be a player to do this! &eTry /smite <player>");
                    return true;
                }

                Player player = (Player) sender;
                Location pLoc = player.getLocation();

                player.getWorld().strikeLightning(pLoc);
                if (enableMsg) ChatUtils.msg(player, selfMsg);
                return true;
            case 1:
                if(!sender.hasPermission(permOther)) {
                    ChatUtils.msg(sender, plugin.pMsg);
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null) {
                    ChatUtils.msg(sender, "&cThe specified player could not be found!");
                    return true;
                }

                Location tLoc = target.getLocation();
                target.getWorld().strikeLightning(tLoc);
                ChatUtils.sendMessages(sender, smiteMsg, smiteOtherMsg, selfMsg, enableMsg, target);
                return true;
            default:
                String syntax = "&cIncorrect Syntax! &eTry /smite";
                if(sender.hasPermission(permOther)) {
                    syntax = syntax + " or /smite <player>";
                }

                ChatUtils.msg(sender, syntax);
                return true;
        }
    }
}
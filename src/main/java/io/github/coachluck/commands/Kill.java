/*
 *     File: Kill.java
 *     Last Modified: 7/13/20, 11:38 PM
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

public class Kill implements CommandExecutor {
    private final EssentialServer ins;

    public Kill(EssentialServer ins) {
        this.ins = ins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String killMsg = ins.getConfig().getString("kill.message");
        String killOtherMsg = ins.getConfig().getString("kill.others-message");
        String suicideMsg = ins.getConfig().getString("kill.suicide-message");
        final boolean enableMsg = ins.getConfig().getBoolean("kill.message-enable");

        switch(args.length) {
            case 0:
                if(!(sender instanceof Player)) {
                    ChatUtils.msg(sender, "&cYou must be a player to do this! &eTry /kill <player>");
                    return true;
                }
                Player player = (Player) sender;
                if (enableMsg) ChatUtils.msg(player, suicideMsg);
                player.setHealth(0);
                return true;
            case 1:
                if(!sender.hasPermission("essentialserver.kill.others")) {
                    ChatUtils.sendMessage(sender, ins.pMsg);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null) {
                    ChatUtils.msg(sender, ins.getOfflinePlayerMessage(args[0]));
                    return true;
                }

                target.setHealth(0);
                ChatUtils.sendMessages(sender, killMsg, killOtherMsg, suicideMsg, enableMsg, target);
                return true;
            default:
                String syntax = "&cIncorrect Syntax! &eTry /kill";
                if(sender.hasPermission("essentialserver.kill.others")) {
                    syntax = syntax + " or /kill <player>";
                }
                ChatUtils.msg(sender, syntax);
                return true;
        }
    }
}
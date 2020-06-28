/*
 *     File: InvSee.java
 *     Last Modified: 4/10/20, 6:49 PM
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
import org.bukkit.inventory.PlayerInventory;

public class InvSee implements CommandExecutor {
    private EssentialServer plugin;

    public InvSee(EssentialServer ins) {
        this.plugin = ins;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String invSeeMsg = plugin.getConfig().getString("invsee.message");
        String offlinePlayer = plugin.getConfig().getString("offline-player");
        boolean enableMsg = plugin.getConfig().getBoolean("invsee.message-enable");
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(label.equalsIgnoreCase("invsee") && sender.hasPermission("essentialserver.invsee")) { // handle invsee
                if(args.length == 0) { // open player inventory
                    ChatUtils.msg(p, "&cPlease specify a player. &a/invsee [&bplayer&a]");
                }
                else if(args.length == 1) {
                    try {
                        Player tP = Bukkit.getServer().getPlayer(args[0]);
                        PlayerInventory tInv = tP.getInventory();
                        p.openInventory(tInv);
                        if(enableMsg) ChatUtils.msg(p, invSeeMsg.replaceAll("%player%", tP.getDisplayName()));
                    } catch(NullPointerException e) {
                        ChatUtils.msg(p, offlinePlayer.replaceAll("%player%", args[0]));
                    }
                }
                else {
                    ChatUtils.msg(p, "&cIncorrect usage! Try &a/invsee [&bplayer&a]");
                }
            }
        }
        else {
            ChatUtils.msg(sender, "&cYou must be a player to use this command!");
        }
        return true;
    }
}

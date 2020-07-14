/*
 *     File: InvSee.java
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
import org.bukkit.inventory.PlayerInventory;

public class InvSee implements CommandExecutor {
    private final EssentialServer plugin;

    public InvSee(EssentialServer ins) {
        this.plugin = ins;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String invSeeMsg = plugin.getConfig().getString("invsee.message");
        boolean enableMsg = plugin.getConfig().getBoolean("invsee.message-enable");
        if(!(sender instanceof Player)) {
            ChatUtils.msg(sender, "&cYou must be a player to do this!");
            return true;
        }

        Player p = (Player) sender;
        if(args.length != 1) {
            ChatUtils.msg(p, "&cIncorrect Syntax! &eTry /invsee [player]");
            return true;
        }

        Player tP = Bukkit.getServer().getPlayer(args[0]);
        if(tP == null) {
            ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
            return true;
        }

        PlayerInventory tInv = tP.getInventory();
        p.openInventory(tInv);
        if(enableMsg)
            ChatUtils.msg(p, invSeeMsg.replaceAll("%player%", tP.getDisplayName()));
        return true;
    }
}

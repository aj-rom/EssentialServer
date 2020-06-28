/*
 *     File: Heal.java
 *     Last Modified: 6/28/20, 6:22 PM
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
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal implements CommandExecutor {
    private EssentialServer plugin;
    private int healAmount;

    public Heal(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String healMsg = plugin.getConfig().getString("heal.message");
        healAmount = plugin.getConfig().getInt("heal.amount");
        String healOtherMsg = plugin.getConfig().getString("heal.other-message");
        boolean enableMsg = plugin.getConfig().getBoolean("heal.message-enable");

        if (args.length == 1) {
            if (!sender.hasPermission("essentialserver.heal.others")) {
                sender.sendMessage(ChatUtils.format(plugin.pMsg));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                ChatUtils.msg(sender, plugin.getConfig().getString("offline-player"));
                return true;
            }
            setHealth(target);
            ChatUtils.sendMessages(sender, healMsg, healOtherMsg, healMsg, enableMsg, target);
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            setHealth(player);
            if (enableMsg) ChatUtils.msg(player, healMsg);
            return true;
        }
        ChatUtils.msg(sender, "&cYou must be a player to execute this command!");
        return true;
    }

    private void setHealth(Player p) {
        double h = p.getHealth();
        int f = p.getFoodLevel();
        double hAmt = h + healAmount;
        double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if(hAmt > maxHealth) hAmt = maxHealth;
        int fAmt = f + healAmount;
        if(fAmt > 20) fAmt = 20;
        p.setHealth(hAmt);
        p.setFoodLevel(fAmt);
        p.setFireTicks(0);
    }

}
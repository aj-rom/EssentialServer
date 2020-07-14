/*
 *     File: Heal.java
 *     Last Modified: 7/14/20, 12:34 AM
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
        final boolean enableMsg = plugin.getConfig().getBoolean("heal.message-enable");

        switch (args.length) {
            case 0:
                if(!(sender instanceof Player)) {
                    ChatUtils.msg(sender, "&cYou must be a player to do this! &eTry /heal <player>");
                    return true;
                }

                Player player = (Player) sender;
                setHealth(player);
                if (enableMsg) ChatUtils.msg(player, healMsg);
                return true;
            case 1:
                if (!sender.hasPermission("essentialserver.heal.others")) {
                    ChatUtils.sendMessage(sender, plugin.pMsg);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }
                setHealth(target);
                ChatUtils.sendMessages(sender, healMsg, healOtherMsg, healMsg, enableMsg, target);
                return true;
            default:
                String syntax = "&cIncorrect Syntax! &eTry /heal";
                if(sender.hasPermission("essentialserver.heal.others")) {
                    syntax = syntax + " or /heal <player>";
                }
                ChatUtils.msg(sender, syntax);
                return true;
        }
    }

    private void setHealth(Player p) {
        double h = p.getHealth();
        int f = p.getFoodLevel();
        h = h + healAmount;
        double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if(h > maxHealth) h = maxHealth;
        f = f + healAmount;
        if(f > 20) f = 20;
        p.setHealth(h);
        p.setFoodLevel(f);
        p.setFireTicks(0);
    }

}
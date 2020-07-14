/*
 *     File: Teleport.java
 *     Last Modified: 7/13/20, 10:34 PM
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
import io.github.coachluck.utils.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Teleport implements CommandExecutor {
    public static HashMap<UUID, Cooldown> cooldowns = new HashMap<>();
    private final EssentialServer plugin;

    public Teleport(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch(args.length) {
            case 1:
                if(!(sender instanceof Player)) {
                    ChatUtils.logMsg("&cYou must be a player to do this! &eTry /tp <player1> <player2>");
                    return true;
                }
                final Player player = (Player) sender;
                final Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }
                if (sender.getName().equals(target.getName())) {
                    ChatUtils.msg(sender, plugin.getConfig().getString("teleport.self"));
                    return true;
                }
                if(hasCooldown(player)) {
                    ChatUtils.msg(player, plugin.getConfig().getString("teleport.cooldown-message")
                            .replaceAll("%time%", Integer.toString(cooldowns.get(player.getUniqueId()).getTimeRemaining())));
                    return true;
                }

                tPTask(player, target);
                return true;
            case 2:
                if(!sender.hasPermission("essentialserver.tp.others")) {
                    sendUsage(sender);
                    return true;
                }

                final Player playerToSend = Bukkit.getPlayer(args[0]);
                final Player target1 = Bukkit.getPlayer(args[1]);
                if(playerToSend == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[0]));
                    return true;
                }
                if(target1 == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[1]));
                    return true;
                }
                if(playerToSend.getDisplayName().equalsIgnoreCase(target1.getDisplayName())) {
                    ChatUtils.msg(sender, "&cDid you really mean to do that? Try again...");
                    return true;
                }
                if(sender instanceof Player && hasCooldown((Player) sender)) {
                    final Player player1 = (Player) sender;
                    ChatUtils.msg(player1, plugin.getConfig().getString("teleport.cooldown-message")
                            .replaceAll("%time%",
                                    Integer.toString(cooldowns.get(player1.getUniqueId()).getTimeRemaining())));
                    return true;
                }

                tPTask(playerToSend, target1, sender);
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    /**
     * Sends the usage messages to the player
     * @param sender the CommandSender to send messages too
     */
    private void sendUsage(CommandSender sender) {
        ChatUtils.msg(sender, "&cInsufficient arguments! &7Please try again.");
        ChatUtils.msg(sender, "&cTo teleport yourself: &e/tp &c<&7otherplayer&c>");
        if (sender.hasPermission("essentialserver.tp.others")) {
            ChatUtils.msg(sender, "&cTo teleport others: &e/tp &c<&7player&c> <&7otherplayer&c>");
        }
    }

    /**
     * Check whether or not they have Teleport cooldown
     * @param player player to check
     * @return true if in cooldown, false if not
     */
    private boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if(player.hasPermission("essentialserver.tp.bypass-cooldown"))
            return false;

        return Cooldown.checkCooldown(uuid, cooldowns);
    }


    /**
     * Teleport one player to another
     * @param playerToSend the player to teleport
     * @param target the player to teleport to
     */
    private void tPTask(Player playerToSend, Player target) {
        final boolean enableMsg = plugin.getConfig().getBoolean("teleport.message-enable");
        final String tpMsg = plugin.getConfig().getString("teleport.message");
        if(enableMsg) {
            ChatUtils.msg(playerToSend, tpMsg.replaceAll("%player%", target.getDisplayName()));
        }
        playerToSend.teleport(target.getLocation());

    }

    /**
     * Teleport a player to another
     * @param playerToSend the player who is teleporting
     * @param target the player to teleport TO
     * @param sender the person initiating the Teleport
     */
    private void tPTask(Player playerToSend, Player target, CommandSender sender) {
        final boolean enableMsg = plugin.getConfig().getBoolean("teleport.message-enable");
        final String tpOtherMsg = plugin.getConfig().getString("teleport.others-message");
        if(enableMsg) {
            ChatUtils.msg(sender, tpOtherMsg
                    .replaceAll("%player1%", playerToSend.getDisplayName())
                    .replaceAll("%player2%", target.getDisplayName()));
        }
        tPTask(playerToSend, target);
    }

}


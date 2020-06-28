/*
 *     File: Teleport.java
 *     Last Modified: 6/28/20, 4:10 PM
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
import io.github.coachluck.utils.Util;
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
        String offlinePlayer = plugin.getConfig().getString("offline-player");

        if(!(sender instanceof Player)) {
            ChatUtils.logMsg("&cYou must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0 ) {
            sendUsage(player);
            return true;
        }

        if(args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[0]));
                return true;
            }
            if (player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                ChatUtils.msg(player, plugin.getConfig().getString("teleport.self"));
                return true;
            }
            if(hasCooldown(player)) {
                ChatUtils.msg(player, plugin.getConfig().getString("teleport.cooldown-message")
                        .replaceAll("%time%", Integer.toString(cooldowns.get(player.getUniqueId()).getTimeRemaining())));
                return true;
            }

            tPTask(player, target);
            return true;
        }
        if(args.length == 2) {
            if(!player.hasPermission("essentialserver.tp.others")) {
                sendUsage(player);
                return true;
            }

            final Player playerToSend = Bukkit.getPlayer(args[0]);
            final Player target = Bukkit.getPlayer(args[1]);
            if(playerToSend == null) {
                ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[0]));
                return true;
            }
            if(target == null) {
                ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[1]));
                return true;
            }
            if(playerToSend.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                ChatUtils.msg(player, "&cDid you really mean to do that? Try again...");
                return true;
            }
            if(hasCooldown(player)) {
                ChatUtils.msg(player, plugin.getConfig().getString("teleport.cooldown-message")
                        .replaceAll("%time%", Integer.toString(cooldowns.get(player.getUniqueId()).getTimeRemaining())));
                return true;
            }

            tPTask(playerToSend, target, player);
            return true;
        }

        sendUsage(player);
        return true;
    }

    /**
     * Sends the usage messages to the player
     * @param player the player to send messages too
     */
    private void sendUsage(Player player) {
        ChatUtils.msg(player, "&cInsufficient arguments! &7Please try again.");
        ChatUtils.msg(player, "&cTo teleport yourself: &e/tp &c<&botherplayer&c>");
        if (player.hasPermission("essentialserver.tp.others")) {
            ChatUtils.msg(player, "&cTo teleport others: &e/tp &c<&bplayer&c> <&botherplayer&c>");
        }
    }

    /**
     * Check whether or not they have Teleport cooldown
     * @param player player to check
     * @return true if in cooldown, false if not
     */
    private boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if(player.hasPermission("essentialserver.tp.bypass"))
            return false;

        return Util.checkCooldown(uuid, cooldowns);
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
    private void tPTask(Player playerToSend, Player target, Player sender) {
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


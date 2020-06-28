/*
 *     File: Teleport.java
 *     Last Modified: 6/28/20, 2:28 PM
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
        String tpOtherMsg = plugin.getConfig().getString("teleport.others-message");
        String tpMsg = plugin.getConfig().getString("teleport.message");

        String offlinePlayer = plugin.getConfig().getString("offline-player");
        boolean enableMsg = plugin.getConfig().getBoolean("teleport.message-enable");
        if(!(sender instanceof Player)) {
            ChatUtils.logMsg("&cYou must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0 ) {
            ChatUtils.msg(player, "&cInsufficient arguments! &7Please try again.");
            ChatUtils.msg(player, "&cTo teleport yourself: &e/tp &c<&botherplayer&c>");
            if (player.hasPermission("essentialserver.tp.others")) {
                ChatUtils.msg(player, "&cTo teleport others: &e/tp &c<&bplayer&c> <&botherplayer&c>");
            }
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
            if(tPTask(player, target, player) && enableMsg) {
                ChatUtils.msg(player, tpMsg.replaceAll("%player%", target.getDisplayName()));
                return true;
            }
        }
        else if(args.length == 2) {
            if(!player.hasPermission("essentialserver.tp.others")) {
                ChatUtils.msg(sender, "&cToo many arguments! Try /tp <player>");
                return true;
            }

            Player playerToSend = Bukkit.getPlayer(args[0]);
            Player target = Bukkit.getPlayer(args[1]);
            if(playerToSend == null) {
                ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[0]));
                return true;
            }
            if(target == null) {
                ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[1]));
                return true;
            }
            if(playerToSend.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                ChatUtils.msg(sender, "&cDid you really mean to do that? Try again...");
                return true;
            }

            if(tPTask(playerToSend, target, player) && enableMsg) {
                ChatUtils.msg(sender, tpOtherMsg
                        .replaceAll("%player1%", playerToSend.getDisplayName())
                        .replaceAll("%player2%", target.getDisplayName()));
                return true;
            }
        } else {
            if (sender.hasPermission("essentialserver.tp"))
                ChatUtils.msg(sender, "&cToo many arguments! Try /tp <player>");
            if (sender.hasPermission("essentialserver.tp.others"))
                ChatUtils.msg(sender, "&cToo many arguments! Try /tp <player> <player>");

            return true;
        }
        return true;
    }

    /**
     * Teleport a player
     * @param playerToSend the player who is teleporting
     * @param target the player to teleport TO
     * @param sender the person initiating the Teleport
     */
    private boolean tPTask(Player playerToSend, Player target, Player sender) {
        if(sender.hasPermission("essentialserver.tp.bypass")) {
            playerToSend.teleport(target.getLocation());
            return true;
        }

        UUID sUUID = sender.getUniqueId();
        if(cooldowns.containsKey(sUUID)) {
            if(cooldowns.get(sUUID).getTimeRemaining() > 0) {
                int rem = cooldowns.get(sUUID).getTimeRemaining();
                ChatUtils.msg(sender, plugin.getConfig().getString("teleport.cooldown-message")
                        .replaceAll("%time%", "" + rem));
                return false;
            }
            cooldowns.remove(sUUID);
        }
        playerToSend.teleport(target.getLocation());
        cooldowns.put(sUUID, new Cooldown(Cooldown.CooldownType.TELEPORT));
        return true;
    }

}


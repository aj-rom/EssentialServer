/*
 *     File: IGameMode.java
 *     Last Modified: 6/28/20, 8:55 PM
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
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class IGameMode implements CommandExecutor {

    private final EssentialServer plugin;
    public IGameMode(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean enableMsg = plugin.getConfig().getBoolean("gamemode.message-enable");
        String gOtherMsg = plugin.getConfig().getString("gamemode.message-others");
        String offlinePlayer = plugin.getConfig().getString("offline-player");

        if(sender instanceof Player) {
            Player p = (Player) sender;
            switch(args.length) {
                case 0:
                    ChatUtils.msg(p, "&cPlease specify a gamemode! &e/gamemode &c<&7mode&c>&c.");
                    return true;
                case 1:
                    changeGM(args[0], p, sender);
                    return true;
                case 2:
                    if(!sender.hasPermission("essentialserver.gamemode.others")) {
                        ChatUtils.msg(sender, "&cIncorrect Syntax: &e/gamemode &7[&bmode&7]");
                        return true;
                    }

                    Player target = Bukkit.getPlayerExact(args[1]);
                    if(target == null) {
                        ChatUtils.msg(sender, offlinePlayer);
                        return true;
                    }
                    if (enableMsg && !p.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                        ChatUtils.msg(sender, gOtherMsg
                                .replaceAll("%player%", target.getDisplayName())
                                .replaceAll("%mode%", args[0].toLowerCase()));
                    }
                    changeGM(args[0], target, sender);
                    return true;
                default:
                    badUse(sender);
                    return true;
            }
        }
        else if (sender instanceof ConsoleCommandSender) {
            if(args.length != 2) {
                badUse(sender);
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                ChatUtils.msg(sender, offlinePlayer);
                return true;
            }
            if (enableMsg) {
                ChatUtils.msg(sender, gOtherMsg
                        .replaceAll("%player%", target.getDisplayName())
                        .replaceAll("%mode%", args[0].toLowerCase()));
            }
            changeGM(args[0], target, sender);
            return true;
        }

        return true;
    }

    /**
     * Changes the gamemode of the player w the args
     * @param GAMEMODE the gamemode to switch too
     * @param target the player to change the gamemode of
     * @param s the player changing the gamemode
     */
    private void changeGM(String GAMEMODE, Player target, CommandSender s) {
        GAMEMODE = GAMEMODE.toLowerCase();
        boolean enableMsg = plugin.getConfig().getBoolean("gamemode.message-enable");
        String gMsg = plugin.getConfig().getString("gamemode.message");
        if (target != null) {
            if (GAMEMODE.startsWith("s") || GAMEMODE.equalsIgnoreCase("0")) {
                target.setGameMode(GameMode.SURVIVAL);
                if (enableMsg)
                    ChatUtils.msg(target, gMsg.replaceAll("%mode%", "Survival"));
            }
            else if (GAMEMODE.startsWith("c") || GAMEMODE.equalsIgnoreCase("1")) {
                target.setGameMode(GameMode.CREATIVE);
                if (enableMsg)
                    ChatUtils.msg(target, gMsg.replaceAll("%mode%", "Creative"));
            }
            else if (GAMEMODE.startsWith("a") || GAMEMODE.equalsIgnoreCase("2")) {
                target.setGameMode(GameMode.ADVENTURE);
                if (enableMsg)
                    ChatUtils.msg(target, gMsg.replaceAll("%mode%", "Adventure"));
            } else badUse(s);
        }
    }

    /**
     * Send the bad usage message
     * @param s the sender of the command
     */
    private void badUse(CommandSender s) {
        if(s.hasPermission("essentialserver.tp.others")) ChatUtils.msg(s, "&cIncorrect usage! &7Try &e/gamemode &b<&7mode&b> &c[&7player&7]");
        else ChatUtils.msg(s, "&cIncorrect usage! Try &a/gamemode &b[&7mode&b]");
    }

}

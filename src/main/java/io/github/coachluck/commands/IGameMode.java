/*
 *     File: IGameMode.java
 *     Last Modified: 7/14/20, 12:50 AM
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
import org.bukkit.entity.Player;


public class IGameMode implements CommandExecutor {

    private final EssentialServer plugin;
    public IGameMode(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final boolean enableMsg = plugin.getConfig().getBoolean("gamemode.message-enable");
        String gOtherMsg = plugin.getConfig().getString("gamemode.message-others");

        switch(args.length) {
            case 1:
                if(!(sender instanceof Player)) {
                    badUse(sender);
                    return true;
                }
                if(!isValidGamemode(args[0])) {
                    badGamemode(sender);
                    return true;
                }
                changeGM(args[0], (Player) sender);
                return true;
            case 2:
                if(!sender.hasPermission("essentialserver.gamemode.others")) {
                    ChatUtils.sendMessage(sender, plugin.pMsg);
                    return true;
                }
                if(!isValidGamemode(args[0])) {
                    badGamemode(sender);
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) {
                    ChatUtils.msg(sender, plugin.getOfflinePlayerMessage(args[1]));
                    return true;
                }
                if (enableMsg && !sender.getName().equalsIgnoreCase(target.getName())) {
                    ChatUtils.msg(sender, gOtherMsg
                            .replaceAll("%player%", target.getDisplayName())
                            .replaceAll("%mode%", getGamemodeName(args[0])));
                }
                changeGM(args[0], target);
                return true;
            default:
                badUse(sender);
                return true;
        }
    }

    /**
     * Changes the gamemode of the player w the args
     * @param gamemode the gamemode to switch too
     * @param target the player to change the gamemode of
     */
    private void changeGM(String gamemode, Player target) {
        String GAMEMODE = gamemode.toLowerCase();
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
            }
        }
    }

    /**
     * Check whether or not the gamemode is valid
     * @param gm the gamemode to attempt and change too
     * @return whether or not it is a valid gamemode
     */
    private boolean isValidGamemode(String gm) {
        String gamemode = gm.toLowerCase();
        if(gamemode.startsWith("s") || gamemode.startsWith("0")) {
            return true;
        }
        if(gamemode.startsWith("c") || gamemode.startsWith("1")) {
            return true;
        }
        return gamemode.startsWith("a") || gamemode.startsWith("2");
    }

    private String getGamemodeName(String gm) {
        String gamemode = gm.toLowerCase();
        if(gamemode.startsWith("s") || gamemode.startsWith("0")) {
            return "Survival";
        }
        if(gamemode.startsWith("c") || gamemode.startsWith("1")) {
            return "Creative";
        }
        return "Adventure";
    }

    /**
     * Send the bad usage message
     * @param s the sender of the command
     */
    private void badUse(CommandSender s) {
        if(s.hasPermission("essentialserver.gamemode.others")) {
            ChatUtils.msg(s, "&cIncorrect Syntax! &eTry /gamemode &b<&7mode&b> &c[&7player&c]");
            return;
        }

        ChatUtils.msg(s, "&cIncorrect Syntax! &eTry /gamemode &b[&7mode&b]");
    }

    private void badGamemode(CommandSender sender) {
        ChatUtils.msg(sender, "&cPlease specify a valid GameMode!");
        ChatUtils.msg(sender, "&eTry /gm &8<&eSurvival &7(0) &8| &eCreative &7(1) &8| &eAdventure &7(2)&8>");
    }

}

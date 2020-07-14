/*
 *     File: ChatUtils.java
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

package io.github.coachluck.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static final String SMALL_ARROWS_RIGHT = "»";

    /**
     * Simple string formatter
     * @param format the string to translate color codes
     * **/
    public static String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    /**
     * Translates color codes and chooses how to message the sender, includes prefix
     * @param s determines if it is a player message or a console message
     * @param message the message to send
     * **/
    public static void msg(CommandSender s, String message) {
        if(s instanceof Player) s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c" + SMALL_ARROWS_RIGHT + "&r " + message));
        else logMsg(message);
    }
    public static void sendMessage(CommandSender s, String message) {
        s.sendMessage(format(message));
    }
    /**
     * Logs the message to console with plugin prefix
     * @param message the message to color code and send to console
     * **/
    public static void logMsg(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&eEssentialServer&7]&r " + message));
    }

    public static void sendPluginMessage(Player p, String message) {
        p.sendMessage(format("&8[&7Essential&eServer&8]&e " + message));
    }

    /**
     * Basic Message handler
     * @param sender who to send the messages too
     * @param mainMsg the main command usage message
     * @param otherMsg the message for usage on other players
     * @param selfMsg the message to be used when performed on themselves
     * @param enableMsg if the messages should even send
     * @param target who the action is being done too
     * **/
    public static void sendMessages(CommandSender sender, String mainMsg, String otherMsg, String selfMsg, boolean enableMsg, Player target) {
        if (enableMsg) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if (!p.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                    msg(target, mainMsg);
                    msg(p, otherMsg.replace("%player%", target.getDisplayName()));
                } else msg(p, selfMsg);
            } else if (sender instanceof ConsoleCommandSender) {
                msg(target, mainMsg);
                msg(sender, otherMsg.replace("%player%", target.getDisplayName()));
            }
        }
    }

    /**
     * Gets the base component for the command containing hover and click events
     * @param cmd the command to get the component for
     * @return the completed component with hover and click actions
     */
    public static BaseComponent getClickHelp(Command cmd) {
        BaseComponent cmdText = new TextComponent(cmd.getName());
        cmdText.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

        cmdText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(format("&c" + cmd.getUsage() + "\n" + "&7" + cmd.getDescription())).create()));
        cmdText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd.getName().toLowerCase() + " "));
        return cmdText;
    }


}
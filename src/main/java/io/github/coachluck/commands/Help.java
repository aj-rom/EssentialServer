/*
 *     File: Help.java
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
import io.github.coachluck.warps.WarpFile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;

import java.util.List;

public class Help implements CommandExecutor {
    private final EssentialServer plugin;
    public Help(EssentialServer plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("eshelp")) {
            sendHelp(sender);
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("es")) {
            if(args.length != 1) {
                if (sender instanceof ConsoleCommandSender) {
                    ChatUtils.logMsg("&ev" + plugin.getDescription().getVersion() + " &7created by &bCoachL_ck");
                    ChatUtils.logMsg("&7 - &ehttps://github.com/CoachLuck/EssentialServer/wiki");
                    return true;
                }

                ComponentBuilder msg = new ComponentBuilder(ChatUtils.format("&8[&cEssential Server&8]&e v" + plugin.getDescription().getVersion() + " &7created by "));
                BaseComponent info = new TextComponent(ChatUtils.format("&cCoachL_ck"));
                info.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bit.ly/346mO6j"));
                info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format("&eClick Me&7!")).create()));
                msg.append(info);

                sender.spigot().sendMessage(msg.create());
                return true;


            } else {
                if(args[0].equalsIgnoreCase("reload")) {
                    if(!sender.hasPermission("essentialserver.reload")) {
                        ChatUtils.sendMessage(sender, plugin.pMsg);
                        return true;
                    }
                    plugin.reloadConfig();
                    plugin.setWarpFile(new WarpFile());
                    plugin.reloadWarpsMap();
                    ChatUtils.msg(sender, "&aSuccessfully configuration files & warps!");
                }
            }
        }
        return true;
    }

    /**
     * Gets the players specific help pages and sends them
     * @param sender : Who is running the command.
     * **/
    public void sendHelp(CommandSender sender) {
        if(!(sender instanceof Player)) {
            ChatUtils.logMsg("&cYou must be a player to do this! &eTry /esinfo or /es reload");
            return;
        }

        Player p = (Player) sender;
        List<Command> cmdList = PluginCommandYamlParser.parse(plugin);
        p.sendMessage("");
        p.sendMessage(ChatUtils.format("&7&m                                   &r&8[ &c&lHelp&r &8]&7&m                                  "));
        TextComponent header = new TextComponent(ChatUtils.format(" &7Hover over &ecommands &7for more info, &eclick &7to run the command"));
        header.setColor(ChatColor.GRAY);
        header.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                ChatUtils.format("&7Usage with &b<> &7 is required. &c[] &7is optional"))
                                .create()));
        p.spigot().sendMessage(header);
        p.sendMessage("");

        ComponentBuilder main = new ComponentBuilder();
        main.append(" ");
        for(Command cmd : cmdList) {
            if(p.hasPermission(cmd.getPermission())) {
                main.append(ChatUtils.getClickHelp(cmd));
                main.append(", ");
            }
        }

        p.spigot().sendMessage(main.create());
        p.sendMessage("");
        p.sendMessage(ChatUtils.format("&7&m                           &r&8[ &cEssential Server&r &8]&7&m                           "));
        p.sendMessage("");
    }
}

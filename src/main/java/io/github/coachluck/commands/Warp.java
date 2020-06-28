/*
 *     File: Warp.java
 *     Last Modified: 6/28/20, 2:58 PM
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
import io.github.coachluck.warps.WarpHolder;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;


public class Warp implements CommandExecutor, TabCompleter {

    public static HashMap<UUID, Cooldown> cooldowns = new HashMap<>();
    private final EssentialServer plugin;

    public Warp(EssentialServer ins) {
        this.plugin = ins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final YamlConfiguration warpData = plugin.getWarpFile().getWarpData();
        final String noPermWarp = warpData.getString("messages.no-perm-for-warp");
        final String warpNotFound = warpData.getString("messages.warp-not-found");
        final String warpListColor = warpData.getString("messages.warp-list-color");

        if(!(sender instanceof Player)) {
            ChatUtils.logMsg("&cYou must be a player to use this command.");
            return true;
        }

        Player p = (Player) sender;
        if(args.length > 1) {
            ChatUtils.msg(p, "&cIncorrect usage: &e/warp &b<WarpName>");
            return true;
        }
        if(args.length == 1) {
            String warpName = args[0].toLowerCase();
            if(plugin.warpMap.get(warpName) == null) {
                ChatUtils.msg(p, warpNotFound.replaceAll("%warp%", warpName));
                return true;
            }
            if(!p.hasPermission("warps.*") && !p.hasPermission("warps." + warpName)) {
                ChatUtils.msg(p, noPermWarp.replaceAll("%warp%", warpName));
                return true;
            }
            if(hasCooldown(p)) {
                ChatUtils.msg(p, warpData.getString("messages.cooldown")
                        .replaceAll("%time%", "" + cooldowns.get(p.getUniqueId()).getTimeRemaining()));
                return true;
            }

            WarpHolder warp = plugin.warpMap.get(warpName);
            p.playSound(warp.getLocation(), warp.getWarpSound(), 1.0F, 1.0F);
            p.teleport(warp.getLocation());
            ChatUtils.msg(p, warp.getWarpMessage().replaceAll("%name%", warp.getDisplayName()));

            return true;
        }

        final List<String> currentWarps = new ArrayList<>(plugin.warpMap.keySet());
        ComponentBuilder warpList = new ComponentBuilder();
        Collections.sort(currentWarps);
        for(String s : currentWarps) {
            if(p.hasPermission("warps." + s)) {
                WarpHolder warpHolder = plugin.warpMap.get(s);
                TextComponent warp = new TextComponent(ChatUtils.format(warpListColor + s + warpData.getString("messages.warp-list-separator")));
                        warp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + s));
                        warp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder(ChatUtils.format("&7Click me to warp to &e" + warpHolder.getDisplayName())).create()));
                warpList.append(warp);
            }
        }
        sendWarpList(p, warpList.create());

        return true;
    }

    /**
     * Check whether or not a player has a cooldown remaining
     * @param player the players to check the cooldown of
     * @return true if they do have a cooldown, false if not
     */
    private boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if(player.hasPermission("warps.bypass-cooldown"))
            return false;

        if(cooldowns.get(uuid) != null) {
            Cooldown cooldown = cooldowns.get(uuid);
            if(cooldown.getTimeRemaining() != 0)
                return true;

            cooldowns.remove(uuid);
            return false;
        }

        cooldowns.put(uuid, new Cooldown(Cooldown.CooldownType.WARP));
        return false;
    }

    private void sendWarpList(Player p, BaseComponent[] warpList) {
        final List<String> warpListHeader = plugin.getWarpFile().getWarpData().getStringList("messages.warp-list-header");
        final List<String> warpListFooter = plugin.getWarpFile().getWarpData().getStringList("messages.warp-list-footer");
        for(String s : warpListHeader)
            p.sendMessage(ChatUtils.format(s));

        p.spigot().sendMessage(warpList);

        for(String s : warpListFooter)
            p.sendMessage(ChatUtils.format(s));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1 && sender instanceof Player) {
            ArrayList<String> allwarps = new ArrayList<>(plugin.warpMap.keySet());
            ArrayList<String> allowedWarps = new ArrayList<>();
            for(String s : allwarps) {
                if(sender.hasPermission("warps." + s)) {
                    allowedWarps.add(s);
                }
            }
            Collections.sort(allowedWarps);
            return allowedWarps;

        } else {
            return new ArrayList<>();
        }
    }
}

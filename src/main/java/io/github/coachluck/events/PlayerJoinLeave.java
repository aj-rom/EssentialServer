/*
 *     File: PlayerJoinLeave.java
 *     Last Modified: 7/14/20, 12:12 AM
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

package io.github.coachluck.events;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeave implements Listener {
    private final EssentialServer plugin;

    public PlayerJoinLeave(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final boolean enableMsg = plugin.getConfig().getBoolean("Join-Leave.enabled");
        String joinMsg = plugin.getConfig().getString("Join-Leave.join-message");
        Player player = e.getPlayer();
        if(!plugin.vanish_players.isEmpty()) {
            for (int i = 0; i < plugin.vanish_players.size(); i++) {
                player.hidePlayer(plugin, Bukkit.getPlayer(plugin.vanish_players.get(i)));
            }
        }
        if(enableMsg) {
            e.setJoinMessage(
                    ChatUtils.format(joinMsg.replace("%player%", player.getDisplayName())));
        }
        if(plugin.updateMsg) {
            if(player.isOp() || player.hasPermission("essentialserver.*")) {
                BaseComponent pre = new TextComponent(ChatUtils.format("&8[&cEssential&7Server&8] "));

                BaseComponent front = new TextComponent("A new update is available! Click ");
                front.setColor(ChatColor.YELLOW);

                BaseComponent button = new TextComponent("here");
                button.setColor(ChatColor.GOLD);
                button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format("&7Click Me!")).create()));
                button.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bit.ly/346mO6j"));
                BaseComponent end = new TextComponent(" to download the newest version.");
                end.setColor(ChatColor.YELLOW);

                ComponentBuilder comp = new ComponentBuilder();
                comp.append(pre);
                comp.append(front);
                comp.append(button);
                comp.append(end);
                player.spigot().sendMessage(comp.create());
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        final boolean enableMsg = plugin.getConfig().getBoolean("Join-Leave.enabled");
        String quitMsg = plugin.getConfig().getString("Join-Leave.leave-message");
        if(enableMsg) {
            e.setQuitMessage(
                    ChatUtils.format(quitMsg.replace("%player%", e.getPlayer().getDisplayName())));
        }
        if(plugin.vanish_players.contains(e.getPlayer().getUniqueId())) {
            plugin.vanish_players.remove(e.getPlayer().getUniqueId());
            e.getPlayer().setInvulnerable(false);
        }
    }
}

/*
 *     File: PlayerJoinLeave.java
 *     Last Modified: 6/28/20, 5:48 PM
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static io.github.coachluck.utils.ChatUtils.format;

public class PlayerJoinLeave implements Listener {
    private final EssentialServer plugin;

    private boolean enableMsg;

    public PlayerJoinLeave(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        enableMsg = plugin.getConfig().getBoolean("Join-Leave.enable-message");
        String joinMsg = plugin.getConfig().getString("Join-Leave.join-message");
        Player player = e.getPlayer();
        if(!plugin.vanish_players.isEmpty()) {
            for (int i = 0; i < plugin.vanish_players.size(); i++) {
                player.hidePlayer(plugin, Bukkit.getPlayer(plugin.vanish_players.get(i)));
            }
        }
        if(enableMsg) e.setJoinMessage(format(joinMsg.replace("%player%", player.getDisplayName())));
        if(plugin.updateMsg && player.isOp()) {
            ChatUtils.sendPluginMessage(player, "&eA new update has been downloaded. Please restart to get the newest version.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        enableMsg = plugin.getConfig().getBoolean("Join-Leave.enable-message");
        String quitMsg = plugin.getConfig().getString("Join-Leave.leave-message");
        if(enableMsg) e.setQuitMessage(format(quitMsg.replace("%player%", e.getPlayer().getDisplayName())));
        if(plugin.vanish_players.contains(e.getPlayer().getUniqueId())) {
            plugin.vanish_players.remove(e.getPlayer().getUniqueId());
            e.getPlayer().setInvulnerable(false);
        }
    }
}

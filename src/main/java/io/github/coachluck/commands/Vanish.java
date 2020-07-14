/*
 *     File: Vanish.java
 *     Last Modified: 7/13/20, 11:25 PM
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Vanish implements CommandExecutor {

    private final EssentialServer plugin;
    private String vanishMsg;
    private String vanishOffMsg;
    private boolean enableMsg;

    public Vanish(EssentialServer ins) {
        this.plugin = ins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String vanishOtherMsg = plugin.getConfig().getString("vanish.other-on-message");
        String vanishOtherOffMsg = plugin.getConfig().getString("vanish.other-off-message");
        enableMsg = plugin.getConfig().getBoolean("vanish.message-enable");
        vanishMsg = plugin.getConfig().getString("vanish.on-message");
        vanishOffMsg = plugin.getConfig().getString("vanish.off-message");
        final String offlinePlayer = plugin.getConfig().getString("offline-player");

        // TODO : Add permission message and change to switch statement
        if(args.length == 0 && sender.hasPermission("essentialserver.vanish")) {
            if(!(sender instanceof Player)) {
                ChatUtils.logMsg("&cYou must be a player to do this! &eTry /vanish <player>");
                return true;

            }
            Player p = (Player) sender;
            vanishCheck(p.getUniqueId());
            return true;
        }
        // TODO : Permission message
        else if (args.length == 1 && sender.hasPermission("essentialserver.vanish.others")) {
            Player tP = Bukkit.getPlayerExact(args[0]);
            if(tP == null) {
                ChatUtils.msg(sender, offlinePlayer.replaceAll("%player%", args[0]));
                return true;
            }
            UUID target = tP.getUniqueId();
            vanishCheck(target);
            if (enableMsg && !sender.getName().equals(tP.getName())) {
                if (plugin.vanish_players.contains(target))
                    ChatUtils.msg(sender, vanishOtherMsg.replaceAll("%player%", tP.getDisplayName()));
                 else if (!plugin.vanish_players.contains(target))
                    ChatUtils.msg(sender, vanishOtherOffMsg.replaceAll("%player%", tP.getDisplayName()));
            }
        }
        // TODO : Add Syntax for perm support.
        else if (args.length > 1)
            ChatUtils.msg(sender, "&cToo many arguments! Try /vanish <player> or /vanish.");

        return true;
    }

    private void vanishCheck(UUID pUUID) {
        Player player = Bukkit.getServer().getPlayer(pUUID);
        assert player != null;
        if (plugin.vanish_players.contains(pUUID)) {
            for(Player people : Bukkit.getServer().getOnlinePlayers()) {
                people.showPlayer(plugin, player);
            }
            plugin.vanish_players.remove(pUUID);
            player.setInvulnerable(false);
            if (enableMsg) ChatUtils.msg(player, vanishOffMsg.replace("%player%", player.getDisplayName()));
        } else if (!plugin.vanish_players.contains(pUUID)) {
            for(Player people : Bukkit.getServer().getOnlinePlayers()) {
                people.hidePlayer(plugin, player);
            }
            plugin.vanish_players.add(pUUID);
            player.setInvulnerable(true);
            if (enableMsg) ChatUtils.msg(player, vanishMsg.replace("%player%", player.getDisplayName()));
        }
    }
}

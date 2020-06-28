/*
 *     File: Kill.java
 *     Last Modified: 4/10/20, 6:49 PM
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

public class Kill implements CommandExecutor {
    private EssentialServer ins;

    public Kill(EssentialServer ins) {
        this.ins = ins;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String killMsg = ins.getConfig().getString("kill.message");
        String killOtherMsg = ins.getConfig().getString("kill.others-message");
        String suicideMsg = ins.getConfig().getString("kill.suicide-message");
        boolean enableMsg = ins.getConfig().getBoolean("kill.message-enable");
        String offlinePlayer = ins.getConfig().getString("offline-player");

        if (args.length == 0 && sender.hasPermission("essentialserver.kill")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (enableMsg) ChatUtils.msg(player, suicideMsg);
                player.setHealth(0);
            } else ChatUtils.msg(sender, "&cYou must be a player to use this command!");
        } else if (args.length == 1 && sender.hasPermission("essentialserver.kill.others")) {
            try {
                Player target = Bukkit.getPlayerExact(args[0]);
                target.setHealth(0);
                ChatUtils.sendMessages(sender, killMsg, killOtherMsg, suicideMsg, enableMsg, target);
            }
            catch (NullPointerException e){
                ChatUtils.msg(sender, offlinePlayer.replaceAll("%player%", args[0]));
            }
        } else if (args.length > 1) ChatUtils.msg(sender, "&cToo many arguments! &7Try &e/kill &c<&bplayer&c> &7or &e/kill.");
        return true;
    }
}
package io.github.coachluck.commands;

import io.github.coachluck.EssentialServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.coachluck.utils.ChatUtils.logMsg;
import static io.github.coachluck.utils.ChatUtils.msg;

public class Teleport implements CommandExecutor {
    EssentialServer plugin;

    public Teleport(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String tpOtherMsg = plugin.getConfig().getString("teleport.others-message");
        String tpMsg = plugin.getConfig().getString("teleport.message");
//        int COOL_DOWN = plugin.getConfig().getInt("teleport.cooldown-time:");
//        boolean enableMsg = plugin.getConfig().getBoolean("teleport.message-enable");

        if (sender instanceof Player && sender.hasPermission("essentialserver.tp")){
            Player player = (Player) sender;

            if (args.length == 0) {
                msg(player, "&cInsufficient arguments! Please try again.");
                msg(player, "&cTo teleport yourself: /tp <&botherplayer&c>");
                if (player.hasPermission("essentialserver.tp.others")) {
                    msg(player, "&cTo teleport others: /tp <&bplayer&c> <&botherplayer&c>");
                }
            }else if(args.length == 1){
                try{
                Player target = Bukkit.getPlayer(args[0]); //Get player from command
                    if(player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                        msg(player, "&cYou can't teleport to yourself...");
                    }
                    else {
                        msg(player, tpMsg.replaceAll("%player%", target.getDisplayName()));
                        player.teleport(target.getLocation());
                    }

                }catch (NullPointerException e){
                    msg(player, ("&cThe specified player could not be found!"));
                }
            }else if(args.length == 2 && player.hasPermission("essentialserver.tp.others")){
                try{
                Player playerToSend = Bukkit.getPlayer(args[0]);
                Player target = Bukkit.getPlayer(args[1]);
                    if(playerToSend.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                        msg(sender, "&cDid you really mean to do that? Try again...");
                    }
                    else {
                        msg(sender, tpOtherMsg.replaceAll("%player1%", playerToSend.getDisplayName()).replaceAll("%player2%", target.getDisplayName()));
                        playerToSend.teleport(target.getLocation());
                    }
                }catch (NullPointerException e){
                    msg(player, "&cThe specified player could not be found!");
                }
            }
            else if(args.length > 2) {
                if(sender.hasPermission("essentialserver.tp")) {
                    msg(sender, "&cToo many arguments! Try /tp <player>");
                }
                if(sender.hasPermission("essentialserver.tp.others")) {
                    msg(sender, "&cToo many arguments! Try /tp <player> <player>");
                }
            }
        }
        else logMsg("&cYou have to be a player to use this command!");
        return true;
    }
}


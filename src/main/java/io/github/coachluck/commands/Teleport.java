package io.github.coachluck.commands;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Teleport implements CommandExecutor {
    public static HashMap<UUID, Integer> cooldownTime  = new HashMap<>();
    public static HashMap<UUID, BukkitRunnable> cooldownTask  = new HashMap<>();
    private int COOL_DOWN;
    private EssentialServer plugin;

    public Teleport(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String tpOtherMsg = plugin.getConfig().getString("teleport.others-message");
        String tpMsg = plugin.getConfig().getString("teleport.message");
        String coolDownMsg = plugin.getConfig().getString("teleport.cooldown-message");
        String offlinePlayer = plugin.getConfig().getString("offline-player");
        COOL_DOWN = plugin.getConfig().getInt("teleport.cooldown-time");
        boolean enableMsg = plugin.getConfig().getBoolean("teleport.message-enable");

        if (sender instanceof Player && sender.hasPermission("essentialserver.tp")) {
            Player player = (Player) sender;
            UUID pUUID = player.getUniqueId();
            if(cooldownTime.containsKey(pUUID)) {
                String rem = cooldownTime.get(pUUID).toString();
                ChatUtils.msg(player, coolDownMsg.replaceAll("%time%", rem));
            }
            else if (args.length == 0 ) {
                ChatUtils.msg(player, "&cInsufficient arguments! &7Please try again.");
                ChatUtils.msg(player, "&cTo teleport yourself: &e/tp &c<&botherplayer&c>");
                if (player.hasPermission("essentialserver.tp.others")) {
                    ChatUtils.msg(player, "&cTo teleport others: &e/tp &c<&bplayer&c> <&botherplayer&c>");
                }
            }
            else if(args.length == 1) {
                try {
                    Player target = Bukkit.getPlayer(args[0]); //Get player from command
                    if (player.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                        ChatUtils.msg(player, plugin.getConfig().getString("teleport.self"));
                    } else {
                        if(enableMsg) ChatUtils.msg(player, tpMsg.replaceAll("%player%", target.getDisplayName()));
                        tPTask(player, target, player);
                    }

                } catch (NullPointerException e) {
                    ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[0]));
                    return true;
                }
            }
            else if(args.length == 2 && player.hasPermission("essentialserver.tp.others")){
                try{
                Player playerToSend = Bukkit.getPlayer(args[0]);
                Player target = Bukkit.getPlayer(args[1]);
                    if(playerToSend.getDisplayName().equalsIgnoreCase(target.getDisplayName())) {
                        ChatUtils.msg(sender, "&cDid you really mean to do that? Try again...");
                        return true;
                    }
                    else {
                        if(enableMsg) ChatUtils.msg(sender, tpOtherMsg
                                .replaceAll("%player1%", playerToSend.getDisplayName())
                                .replaceAll("%player2%", target.getDisplayName()));
                        tPTask(playerToSend, target, player);

                    }
                }catch (NullPointerException e){
                    ChatUtils.msg(player, offlinePlayer.replaceAll("%player%", args[1]));
                }
            }
            else if(args.length > 2) {
                if(sender.hasPermission("essentialserver.tp")) ChatUtils.msg(sender, "&cToo many arguments! Try /tp <player>");
                if(sender.hasPermission("essentialserver.tp.others")) ChatUtils.msg(sender, "&cToo many arguments! Try /tp <player> <player>");
            }
        }
        else ChatUtils.logMsg("&cYou must be a player to use this command!");
        return true;
    }

    /**
     * Teleport a player
     * @param playerToSend the player who is teleporting
     * @param target the player to teleport TO
     * @param sender the person initiating the Teleport
     */
    private void tPTask(Player playerToSend, Player target, Player sender) {
        UUID sUUID = sender.getUniqueId();
        playerToSend.teleport(target.getLocation());
        if (!sender.hasPermission("essentialserver.tp.bypass") && COOL_DOWN > 0) {
            cooldownTime.put(sUUID, COOL_DOWN);
            cooldownTask.put(sUUID, new BukkitRunnable() {
                @Override
                public void run() {
                    cooldownTime.put(sUUID, cooldownTime.get(sUUID) - 1); // decrease the cooldown
                    if (cooldownTime.get(sUUID) == 0) { // end of cooldown
                        cooldownTime.remove(sUUID);
                        cooldownTask.remove(sUUID);
                        cancel();
                    }
                }
            });
            cooldownTask.get(sUUID).runTaskTimer(plugin, 20, 20);
        }
    }

}


package io.github.coachluck.commands;

import io.github.coachluck.EssentialServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static io.github.coachluck.utils.ChatUtils.*;

public class Heal implements CommandExecutor {
    private final EssentialServer plugin;
    public Heal(EssentialServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String healMsg = plugin.getConfig().getString("heal.message");
        int amt = plugin.getConfig().getInt("heal.amount");
        String healOtherMsg = plugin.getConfig().getString("heal.other-message");
        boolean enableMsg = plugin.getConfig().getBoolean("heal.message-enable");

        if (args.length == 0 && sender.hasPermission("essentialserver.heal")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                setHealth(amt, player);
                if (enableMsg) msg(player, healMsg);
            } else msg(sender, "&cYou must be a player to execute this command!");
        } else if (args.length == 1 && sender.hasPermission("essentialserver.heal.others")) {
            try {
                Player target = Bukkit.getPlayerExact(args[0]);
                setHealth(amt, target);
                sendMessages(sender, healMsg, healOtherMsg, healMsg, enableMsg, target);
            } catch (NullPointerException e) {
                msg(sender, "&cThe specified player could not be found!");
            }
        } else if (args.length > 1) msg(sender, "&cToo many arguments! Try /heal <player> or /heal.");
        return true;
    }

    private void setHealth(int amt, Player p) {
        @SuppressWarnings("deprecation")
        double h = p.getHealth();
        int f = p.getFoodLevel();
        double hAmt = h + amt;
        if(hAmt > p.getMaxHealth()) hAmt = p.getMaxHealth();
        int fAmt = f + amt;
        if(fAmt > 20) fAmt = 20;
        p.setHealth(hAmt);
        p.setFoodLevel(fAmt);
        p.setFireTicks(0);
    }

}
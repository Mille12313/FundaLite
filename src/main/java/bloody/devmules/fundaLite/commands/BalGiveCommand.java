// File: BalGiveCommand.java
package bloody.devmules.fundaLite.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalGiveCommand implements CommandExecutor {

    private final Economy econ;

    // Constructor die de economy accepteert
    public BalGiveCommand(Economy econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /balgive <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        double amount;

        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid amount.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than 0.");
            return true;
        }

        econ.depositPlayer(target, amount);
        sender.sendMessage(ChatColor.GREEN + "You gave " + ChatColor.GOLD + econ.format(amount) + ChatColor.GREEN + " to " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".");
        return true;
    }
}

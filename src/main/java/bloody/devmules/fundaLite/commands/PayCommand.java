// File: PayCommand.java
package bloody.devmules.fundaLite.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final Economy econ;

    public PayCommand(Economy econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can send money.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Target player not found.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
            return true;
        }

        // Check of de speler genoeg geld heeft
        if (!econ.has(player, amount)) {
            player.sendMessage(ChatColor.RED + "You do not have enough money.");
            return true;
        }

        // Verwerk betaling
        econ.withdrawPlayer(player, amount);
        econ.depositPlayer(target, amount);

        player.sendMessage(ChatColor.GREEN + "You have sent " + ChatColor.GOLD + amount + ChatColor.GREEN + " to " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".");
        target.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.GOLD + amount + ChatColor.GREEN + " from " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + ".");
        return true;
    }
}

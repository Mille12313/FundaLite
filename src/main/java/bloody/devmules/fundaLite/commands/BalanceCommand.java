// File: BalanceCommand.java
package bloody.devmules.fundaLite.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final Economy econ;

    // Constructor die de economy accepteert
    public BalanceCommand(Economy econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can check their balance.");
            return true;
        }

        Player player = (Player) sender;
        double balance = econ.getBalance(player);

        player.sendMessage(ChatColor.GREEN + "Your current balance is: " + ChatColor.GOLD + econ.format(balance));
        return true;
    }
}

// File: BalRankCommand.java
package bloody.devmules.fundaLite.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BalRankCommand implements CommandExecutor {

    private final Economy econ;

    public BalRankCommand(Economy economy) {
        this.econ = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Convert array of OfflinePlayers to a List to use streams
        List<OfflinePlayer> topPlayers = Arrays.stream(Bukkit.getOfflinePlayers())
                .sorted(Comparator.comparingDouble(player -> econ.getBalance((OfflinePlayer) player)).reversed())
                .limit(10)
                .collect(Collectors.toList());

        sender.sendMessage(ChatColor.GREEN + "Top 10 richest players:");

        for (int i = 0; i < topPlayers.size(); i++) {
            OfflinePlayer player = topPlayers.get(i);
            double balance = econ.getBalance(player);
            // Convert int i + 1 to String using String.valueOf()
            sender.sendMessage(ChatColor.GOLD + String.valueOf(i + 1) + ". " + player.getName() + ": " + ChatColor.BLUE + balance);
        }

        return true;
    }
}

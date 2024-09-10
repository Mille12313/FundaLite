// File: RequestPayCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class RequestPayCommand implements CommandExecutor {

    private final Map<UUID, AcceptPayCommand.PayRequest> payRequests;

    public RequestPayCommand(Map<UUID, AcceptPayCommand.PayRequest> payRequests) {
        this.payRequests = payRequests;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can request payments.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /requestpay <amount> <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Please enter a valid amount.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "The amount must be greater than 0.");
            return true;
        }

        // Store the payment request
        payRequests.put(target.getUniqueId(), new AcceptPayCommand.PayRequest(player.getUniqueId(), amount));

        target.sendMessage(ChatColor.YELLOW + player.getName() + " has requested a payment of " + ChatColor.GOLD + amount + ChatColor.YELLOW + ". Type /acceptpay to confirm.");
        player.sendMessage(ChatColor.GREEN + "Payment request sent to " + target.getName() + ".");

        return true;
    }
}

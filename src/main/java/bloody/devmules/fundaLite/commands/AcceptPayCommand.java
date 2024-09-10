// File: AcceptPayCommand.java
package bloody.devmules.fundaLite.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class AcceptPayCommand implements CommandExecutor {

    private final Map<UUID, PayRequest> payRequests;
    private final Economy econ;

    public AcceptPayCommand(Map<UUID, PayRequest> payRequests, Economy econ) {
        this.payRequests = payRequests;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can accept payment requests.");
            return true;
        }

        Player target = (Player) sender;
        UUID targetId = target.getUniqueId();

        if (!payRequests.containsKey(targetId)) {
            target.sendMessage(ChatColor.RED + "You have no pending payment requests.");
            return true;
        }

        PayRequest request = payRequests.get(targetId);
        Player requester = Bukkit.getPlayer(request.getRequester());

        if (requester == null) {
            target.sendMessage(ChatColor.RED + "The player who requested payment is no longer online.");
            payRequests.remove(targetId);
            return true;
        }

        double amount = request.getAmount();

        // Check of de speler genoeg geld heeft
        if (econ.getBalance(target) < amount) {
            target.sendMessage(ChatColor.RED + "You don't have enough money.");
            return true;
        }

        // Verwerk betaling
        econ.withdrawPlayer(target, amount);
        econ.depositPlayer(requester, amount);

        target.sendMessage(ChatColor.GREEN + "You have paid " + ChatColor.GOLD + amount + ChatColor.GREEN + " to " + ChatColor.GOLD + requester.getName() + ChatColor.GREEN + ".");
        requester.sendMessage(ChatColor.GREEN + "You have received " + ChatColor.GOLD + amount + ChatColor.GREEN + " from " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".");

        // Verwijder de betalingsverzoek nadat het is verwerkt
        payRequests.remove(targetId);
        return true;
    }

    public static class PayRequest {
        private final UUID requester;
        private final double amount;

        public PayRequest(UUID requester, double amount) {
            this.requester = requester;
            this.amount = amount;
        }

        public UUID getRequester() {
            return requester;
        }

        public double getAmount() {
            return amount;
        }
    }
}

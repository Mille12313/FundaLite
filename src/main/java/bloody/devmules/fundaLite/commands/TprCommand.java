// File: TprCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TprCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    // Map to store active teleport requests
    private final Map<Player, Player> activeRequests = new HashMap<>();
    // Map to track player cooldowns
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public TprCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can send teleport requests.");
            return true;
        }

        Player player = (Player) sender;

        // Load the cooldown value from config, default to 10 minutes if not set
        int cooldownMinutes = plugin.getConfig().getInt("tpr-cooldown-minutes", 10);

        // Convert cooldown to milliseconds (1 minute = 60000 milliseconds)
        long cooldownTimeMillis = cooldownMinutes * 60000;

        // Check if player is on cooldown
        if (cooldownMinutes > 0 && cooldowns.containsKey(player)) {
            long timeSinceLastRequest = System.currentTimeMillis() - cooldowns.get(player);
            if (timeSinceLastRequest < cooldownTimeMillis) {
                long minutesRemaining = (cooldownTimeMillis - timeSinceLastRequest) / 60000;
                player.sendMessage(ChatColor.RED + "You must wait " + minutesRemaining + " minutes before sending another teleport request.");
                return true;
            }
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /tpr <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or is not online.");
            return true;
        }

        // Store the new request, overwriting any previous one
        activeRequests.put(target, player);

        // Notify the target player
        target.sendMessage(ChatColor.GOLD + player.getName()
                + ChatColor.GREEN + " has sent you a teleport request. Type "
                + ChatColor.GOLD + "/tpa"
                + ChatColor.GREEN + " to accept.");

        // Confirmation message for the player who sent the request
        player.sendMessage(ChatColor.GREEN + "Teleport request sent to " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".");

        return true;
    }

    // Method to set cooldown only after a successful teleport
    public void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    public Player getRequester(Player target) {
        return activeRequests.get(target);
    }

    public void removeRequest(Player target) {
        activeRequests.remove(target);
    }
}

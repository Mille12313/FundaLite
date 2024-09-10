// File: TpaCommand.java
package bloody.devmules.fundaLite.commands;

import bloody.devmules.fundaLite.FundaLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TpaCommand implements CommandExecutor {

    private final TprCommand tprCommand;
    private final JavaPlugin plugin;

    public TpaCommand(JavaPlugin plugin, TprCommand tprCommand) {
        this.plugin = plugin;
        this.tprCommand = tprCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can accept teleport requests.");
            return true;
        }

        Player player = (Player) sender;
        Player requester = tprCommand.getRequester(player);

        if (requester == null || !requester.isOnline()) {
            player.sendMessage(ChatColor.RED + "No teleport requests found.");
            return true;
        }

        // Remove the teleport request after accepting
        tprCommand.removeRequest(player);

        int warmUpTime = plugin.getConfig().getInt("teleport-warmup", 5); // Default 5 seconds

        // Add requester to the list of teleporting players
        FundaLite pluginInstance = (FundaLite) plugin;
        pluginInstance.getTeleportCancelListener().addTeleportingPlayer(requester);

        // Notify players about the teleport
        player.sendMessage(ChatColor.GREEN + "You have accepted the teleport request. Teleporting " + ChatColor.GOLD + requester.getName() + ChatColor.GREEN + " to you in " + ChatColor.GOLD + warmUpTime + ChatColor.GREEN + " seconds.");
        requester.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has accepted your request. You will be teleported in " + ChatColor.GOLD + warmUpTime + ChatColor.GREEN + " seconds.");

        // Schedule the teleport after the warm-up time
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Check if the player is still in the teleporting list (hasn't moved or been attacked)
            if (pluginInstance.getTeleportCancelListener().getTeleportingPlayers().contains(requester)) {
                requester.teleport(player);
                requester.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + ".");

                // Only trigger cooldown after a successful teleport
                tprCommand.setCooldown(requester);

                pluginInstance.getTeleportCancelListener().removeTeleportingPlayer(requester); // Remove from teleporting list after successful teleport
            }
        }, warmUpTime * 20L); // Convert seconds to ticks (1 second = 20 ticks)

        return true;
    }
}

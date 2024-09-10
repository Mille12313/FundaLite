// File: HomeCommand.java
package bloody.devmules.fundaLite.commands;

import bloody.devmules.fundaLite.FundaLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HomeCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public HomeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can teleport to their home.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /home <name>");
            return true;
        }

        String homeName = args[0];

        // Load the player's home file
        File playerFile = new File(plugin.getDataFolder() + "/playerData", player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            player.sendMessage(ChatColor.RED + "You do not have any homes set.");
            return true;
        }

        FileConfiguration homeConfig = YamlConfiguration.loadConfiguration(playerFile);
        String path = homeName;

        if (!homeConfig.contains(path)) {
            player.sendMessage(ChatColor.RED + "Home '" + homeName + "' not found.");
            return true;
        }

        // Cooldown check
        int cooldownMinutes = plugin.getConfig().getInt("home-cooldown-minutes", 10);
        if (cooldownMinutes > 0 && cooldowns.containsKey(player)) {
            long cooldownTimeMillis = cooldownMinutes * 60000; // Convert minutes to milliseconds
            long timeSinceLastRequest = System.currentTimeMillis() - cooldowns.get(player);
            if (timeSinceLastRequest < cooldownTimeMillis) {
                long minutesRemaining = (cooldownTimeMillis - timeSinceLastRequest) / 60000;
                player.sendMessage(ChatColor.RED + "You must wait " + minutesRemaining + " minutes before teleporting home again.");
                return true;
            }
        }

        // Get home location from the player's home file
        World world = Bukkit.getWorld(homeConfig.getString(path + ".world"));
        double x = homeConfig.getDouble(path + ".x");
        double y = homeConfig.getDouble(path + ".y");
        double z = homeConfig.getDouble(path + ".z");
        float yaw = (float) homeConfig.getDouble(path + ".yaw");
        float pitch = (float) homeConfig.getDouble(path + ".pitch");

        Location home = new Location(world, x, y, z, yaw, pitch);

        // Warm-up before teleport
        int warmupTime = plugin.getConfig().getInt("home-teleport-warmup", 5); // Default 5 seconds
        player.sendMessage(ChatColor.GREEN + "You will be teleported to your home " + ChatColor.GOLD + "'" + homeName + "'" + ChatColor.GREEN + " in " + ChatColor.GOLD + warmupTime + ChatColor.GREEN + " seconds.");

        FundaLite pluginInstance = (FundaLite) plugin;
        pluginInstance.getTeleportCancelListener().addTeleportingPlayer(player);

        // Schedule the teleport with warm-up
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pluginInstance.getTeleportCancelListener().getTeleportingPlayers().contains(player)) {
                player.teleport(home);
                player.sendMessage(ChatColor.GREEN + "Teleported to your home " + ChatColor.GOLD + "'" + homeName + "'" + ChatColor.GREEN + ".");
                if (cooldownMinutes > 0) {
                    cooldowns.put(player, System.currentTimeMillis()); // Set cooldown after successful teleport
                }
                pluginInstance.getTeleportCancelListener().removeTeleportingPlayer(player);
            }
        }, warmupTime * 20L); // Convert seconds to ticks

        return true;
    }
}

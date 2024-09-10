// File: SetHomeCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SetHomeCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SetHomeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set a home.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /sethome <name>");
            return true;
        }

        String homeName = args[0];

        // Get the player's home file
        File playerFile = new File(plugin.getDataFolder() + "/playerData", player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "An error occurred while creating your home file.");
                return true;
            }
        }

        YamlConfiguration homeConfig = YamlConfiguration.loadConfiguration(playerFile);

        // Check if the player already has the maximum number of homes
        Set<String> homes = homeConfig.getKeys(false);
        int maxHomes = plugin.getConfig().getInt("max-homes", 3);

        if (homes.size() >= maxHomes) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of homes (" + maxHomes + ").");
            return true;
        }

        Location location = player.getLocation();
        String path = homeName;

        // Save the home location in the player's home file
        homeConfig.set(path + ".world", location.getWorld().getName());
        homeConfig.set(path + ".x", location.getX());
        homeConfig.set(path + ".y", location.getY());
        homeConfig.set(path + ".z", location.getZ());
        homeConfig.set(path + ".yaw", location.getYaw());
        homeConfig.set(path + ".pitch", location.getPitch());

        try {
            homeConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while saving your home.");
            return true;
        }

        // Send confirmation message with the home name in gold
        player.sendMessage(ChatColor.GREEN + "Home " + ChatColor.GOLD + homeName + ChatColor.GREEN + " set at your current location.");
        return true;
    }
}

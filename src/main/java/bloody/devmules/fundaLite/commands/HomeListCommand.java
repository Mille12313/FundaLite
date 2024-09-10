// File: HomeListCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

public class HomeListCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public HomeListCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view their homes.");
            return true;
        }

        Player player = (Player) sender;

        // Load the player's home file
        File playerFile = new File(plugin.getDataFolder() + "/playerData", player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            player.sendMessage(ChatColor.RED + "You don't have any homes set.");
            return true;
        }

        YamlConfiguration homeConfig = YamlConfiguration.loadConfiguration(playerFile);
        Set<String> homeNames = homeConfig.getKeys(false);

        if (homeNames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You don't have any homes set.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Your homes:");

        for (String homeName : homeNames) {
            String world = homeConfig.getString(homeName + ".world");
            double x = homeConfig.getDouble(homeName + ".x");
            double y = homeConfig.getDouble(homeName + ".y");
            double z = homeConfig.getDouble(homeName + ".z");

            player.sendMessage(ChatColor.GOLD + homeName + ChatColor.GREEN + ": " +
                    ChatColor.BLUE + "World: " + world + ", X: " + x + ", Y: " + y + ", Z: " + z);
        }

        return true;
    }
}

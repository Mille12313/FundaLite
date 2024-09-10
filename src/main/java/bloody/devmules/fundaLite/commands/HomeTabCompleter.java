// File: HomeTabCompleter.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeTabCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public HomeTabCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null; // Tab complete is only for players
        }

        Player player = (Player) sender;

        // Only offer tab completion if the player has typed "/home" and is typing the home name
        if (command.getName().equalsIgnoreCase("home") && args.length == 1) {
            // Load the player's home file
            File playerFile = new File(plugin.getDataFolder() + "/playerData", player.getUniqueId() + ".yml");
            if (!playerFile.exists()) {
                return null; // No homes set, no tab completion
            }

            YamlConfiguration homeConfig = YamlConfiguration.loadConfiguration(playerFile);
            List<String> homeNames = new ArrayList<>(homeConfig.getKeys(false));

            // Return all home names for tab completion
            return homeNames;
        }

        return null;
    }
}

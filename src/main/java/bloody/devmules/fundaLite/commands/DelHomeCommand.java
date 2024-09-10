// File: DelHomeCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DelHomeCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public DelHomeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can delete their home.");
            return true;
        }

        Player player = (Player) sender;
        String path = "homes." + player.getUniqueId();

        if (!plugin.getConfig().contains(path)) {
            player.sendMessage(ChatColor.RED + "You don't have a home to delete.");
            return true;
        }

        // Remove home from config
        plugin.getConfig().set(path, null);
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Home deleted.");
        return true;
    }
}

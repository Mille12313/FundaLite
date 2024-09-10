// File: ReloadConfigCommand.java
package bloody.devmules.fundaLite.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadConfigCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadConfigCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("fundalite.reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "FundaLite configuration reloaded.");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return true;
    }
}

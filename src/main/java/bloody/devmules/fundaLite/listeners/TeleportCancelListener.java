// File: TeleportCancelListener.java
package bloody.devmules.fundaLite.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

public class TeleportCancelListener implements Listener {

    // Set to store players who are waiting for a teleport
    private final Set<Player> teleportingPlayers = new HashSet<>();

    public void addTeleportingPlayer(Player player) {
        teleportingPlayers.add(player);
    }

    public void removeTeleportingPlayer(Player player) {
        teleportingPlayers.remove(player);
    }

    public Set<Player> getTeleportingPlayers() {
        return teleportingPlayers;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is teleporting and moved
        if (teleportingPlayers.contains(player) && event.getFrom().distance(event.getTo()) > 0) {
            player.sendMessage(ChatColor.RED + "Teleport canceled because you moved!");
            teleportingPlayers.remove(player); // Remove the player from the set
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // Check if the player is teleporting and got attacked
            if (teleportingPlayers.contains(player)) {
                player.sendMessage(ChatColor.RED + "Teleport canceled because you were attacked!");
                teleportingPlayers.remove(player); // Remove the player from the set
            }
        }
    }
}

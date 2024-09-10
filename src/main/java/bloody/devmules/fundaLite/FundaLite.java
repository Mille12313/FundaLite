// File: FundaLite.java
package bloody.devmules.fundaLite;

import bloody.devmules.fundaLite.commands.*;
import bloody.devmules.fundaLite.economy.Fundaconomy;
import bloody.devmules.fundaLite.listeners.TeleportCancelListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FundaLite extends JavaPlugin {

    private static Economy econ = null;
    private TprCommand tprCommand;
    private TeleportCancelListener teleportCancelListener;
    private Fundaconomy fundaconomy;

    // Map to store payment requests
    private final Map<UUID, AcceptPayCommand.PayRequest> payRequests = new HashMap<>();

    @Override
    public void onEnable() {
        // Load the config
        saveDefaultConfig();

        // Try to setup Vault economy with Fundaconomy
        if (!setupEconomy()) {
            getLogger().warning("Vault not found, economy features will be disabled.");
        }

        // Create the playerData directory if it doesn't exist
        File playerDataDir = new File(getDataFolder(), "playerData");
        if (!playerDataDir.exists()) {
            playerDataDir.mkdirs();
        }

        // Register teleport commands
        if (getCommand("tpr") != null) {
            tprCommand = new TprCommand(this);
            getCommand("tpr").setExecutor(tprCommand);
        }
        if (getCommand("tpa") != null) {
            getCommand("tpa").setExecutor(new TpaCommand(this, tprCommand));
        }
        if (getCommand("tp") != null) {
            getCommand("tp").setExecutor(new TpCommand());
        }

        // Register home management commands
        if (getCommand("sethome") != null) {
            getCommand("sethome").setExecutor(new SetHomeCommand(this));
        }
        if (getCommand("home") != null) {
            getCommand("home").setExecutor(new HomeCommand(this));
        }
        if (getCommand("delhome") != null) {
            getCommand("delhome").setExecutor(new DelHomeCommand(this));
        }
        if (getCommand("homelist") != null) {
            getCommand("homelist").setExecutor(new HomeListCommand(this)); // Register /homelist
        }

        // Register tab completer for /home command
        if (getCommand("home") != null) {
            getCommand("home").setTabCompleter(new HomeTabCompleter(this));
        }

        // Register reload config command
        if (getCommand("fundalite") != null) {
            getCommand("fundalite").setExecutor(new ReloadConfigCommand(this));
        }

        // Register the teleport cancel listener
        teleportCancelListener = new TeleportCancelListener();
        getServer().getPluginManager().registerEvents(teleportCancelListener, this);

        // Register economy commands only if economy is available
        if (econ != null) {
            if (getCommand("pay") != null) {
                getCommand("pay").setExecutor(new PayCommand(econ)); // Pass econ to PayCommand
            }
            if (getCommand("requestpay") != null) {
                getCommand("requestpay").setExecutor(new RequestPayCommand(payRequests)); // Pass payRequests to RequestPayCommand
            }
            if (getCommand("acceptpay") != null) {
                getCommand("acceptpay").setExecutor(new AcceptPayCommand(payRequests, econ));  // Register AcceptPayCommand with econ
            }
            if (getCommand("balance") != null) {
                getCommand("balance").setExecutor(new BalanceCommand(econ)); // Pass econ to BalanceCommand
            }
            if (getCommand("balrank") != null) {
                getCommand("balrank").setExecutor(new BalRankCommand(econ)); // Pass econ to BalRankCommand
            }
            if (getCommand("balgive") != null) {
                getCommand("balgive").setExecutor(new BalGiveCommand(econ)); // Pass econ to BalGiveCommand
            }

            getLogger().info("Economy features are enabled with Fundaconomy!");
        } else {
            getLogger().info("Economy features are disabled due to missing Vault.");
        }

        getLogger().info("FundaLite is fully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FundaLite is disabled!");
    }

    // Try to setup Vault economy with Fundaconomy
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        // Initialize Fundaconomy and register it as the economy provider for Vault
        fundaconomy = new Fundaconomy(this);
        getServer().getServicesManager().register(Economy.class, fundaconomy, this, ServicePriority.Normal);
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public TeleportCancelListener getTeleportCancelListener() {
        return teleportCancelListener;
    }
}

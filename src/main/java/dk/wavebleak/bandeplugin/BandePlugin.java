package dk.wavebleak.bandeplugin;


import dk.wavebleak.bandeplugin.classes.InventoryData;
import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.command.BandeCommand;
import dk.wavebleak.bandeplugin.events.GUIChangeEvent;
import dk.wavebleak.bandeplugin.events.PlayerDeathEvent;
import dk.wavebleak.bandeplugin.events.PlayerHitPlayerEvent;
import dk.wavebleak.bandeplugin.utils.Manager;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BandePlugin extends JavaPlugin {

    public static BandePlugin instance;
    public static Manager manager;
    public List<Bande> bander;
    public static HashMap<OfflinePlayer, Bande> invites;
    public static Economy economy = null;
    public static HashMap<Player, InventoryData> inventoryManager = new HashMap<>();

    public static String[] bannedNames = {
            "HeJ"
    };

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        instance = this;

        manager = new Manager();
        bander = manager.loadData();
        invites = new HashMap<>();
        inventoryManager = new HashMap<>();



        setupEconomy();

        getServer().getPluginManager().registerEvents(new GUIChangeEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitPlayerEvent(), this);

        getCommand("bande").setExecutor(new BandeCommand());
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if(economyProvider != null) {
            economy = economyProvider.getProvider();
        }

    }

    public void load() {
        bander = manager.refreshData();
    }

    public void save() {
        manager.saveData(bander);
    }

    @Override
    public void onDisable() {
        manager.saveData(bander);
    }
}

package dk.wavebleak.bandeplugin;


import dk.wavebleak.bandeplugin.classes.Info;
import dk.wavebleak.bandeplugin.classes.InventoryData;
import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.command.BandeCommand;
import dk.wavebleak.bandeplugin.events.GUIChangeEvent;
import dk.wavebleak.bandeplugin.events.PlayerDeathEvent;
import dk.wavebleak.bandeplugin.events.PlayerHitPlayerEvent;
import dk.wavebleak.bandeplugin.utils.GithubUtils;
import dk.wavebleak.bandeplugin.utils.Manager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

import static dk.wavebleak.bandeplugin.utils.GithubUtils.checkVersion;

@SuppressWarnings("all")
public final class BandePlugin extends JavaPlugin {

    public static BandePlugin instance;
    public static Manager manager;
    public List<Bande> bander;
    public static HashMap<OfflinePlayer, Bande> invites;
    public static Economy economy = null;
    public static HashMap<Player, InventoryData> inventoryManager = new HashMap<>();
    public static Info info;

    public static String[] bannedNames = {
            "HeJ" //TODO: Add slurs
    };

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        instance = this;

        manager = new Manager();
        bander = manager.loadData();
        invites = new HashMap<>();
        inventoryManager = new HashMap<>();

        try {
            info = GithubUtils.getInfo();
        }catch (IOException e) {
            panic();
        }

        setupEconomy();

        getServer().getPluginManager().registerEvents(new GUIChangeEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitPlayerEvent(), this);

        getCommand("bande").setExecutor(new BandeCommand());

        if(!checkVersion(info)) {
            panic();
        }
    }

    public void panic() {
        getLogger().warning("FOR\u00C6LDET VERSION AF BANDE, DISABLER PLUGINNET");
        getLogger().warning("ADD \"wavebleak\" P\u00C5 DISCORD FOR AT FIXE DET");
        getLogger().warning("FOR\u00C6LDET VERSION AF BANDE, DISABLER PLUGINNET");
        Bukkit.broadcastMessage(ChatColor.RED + "FOR\u00C6LDET VERSION AF BANDE, DISABLER PLUGINNET, ADD \"wavebleak\" P\u00C5 DISCORD FOR AT FIXE DET"); //TODO: Make this pretti
        Bukkit.getPluginManager().disablePlugin(this);
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

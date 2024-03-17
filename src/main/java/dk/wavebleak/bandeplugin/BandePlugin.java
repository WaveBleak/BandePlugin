package dk.wavebleak.bandeplugin;


import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.classes.BandeTerritorie;
import dk.wavebleak.bandeplugin.classes.Info;
import dk.wavebleak.bandeplugin.classes.InventoryData;
import dk.wavebleak.bandeplugin.command.BandeCommand;
import dk.wavebleak.bandeplugin.events.GUIChangeEvent;
import dk.wavebleak.bandeplugin.events.PlayerDeathEvent;
import dk.wavebleak.bandeplugin.events.PlayerHitPlayerEvent;
import dk.wavebleak.bandeplugin.utils.GithubUtils;
import dk.wavebleak.bandeplugin.utils.Manager;
import hm.zelha.particlesfx.util.ParticleSFX;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static dk.wavebleak.bandeplugin.utils.GithubUtils.checkVersion;


public final class BandePlugin extends JavaPlugin {

    public static BandePlugin instance;
    public static Manager manager;
    public List<Bande> bander;
    public List<BandeTerritorie> territories;
    public static HashMap<OfflinePlayer, Bande> invites;
    public static Economy economy = null;
    public static HashMap<Player, InventoryData> inventoryManager = new HashMap<>();
    public static Info info;
    public static Permission permission;

    public static String[] bannedNames = {
            " ",
            "nigger",
            "slut",
            "kælling",
            "tisse",
            "nosse",
            "bitch",
            "neger",
            "sut",
            "pik",
            "penis",
            "fallos",
            "vagina",
            "fisse",
            "gina",
            "porn",
            "niga",
            "niger",
            "nigga"
    };

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        instance = this;

        manager = new Manager();
        bander = manager.loadBande();
        territories = manager.loadTerritories();
        invites = new HashMap<>();
        inventoryManager = new HashMap<>();
        //api = HolographicDisplaysAPI.get(this);

        try {
            info = GithubUtils.getInfo();
        }catch (IOException e) {
            panic();
        }

        setupEconomy();

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        getServer().getPluginManager().registerEvents(new GUIChangeEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitPlayerEvent(), this);

        getCommand("bande").setExecutor(new BandeCommand());

        ParticleSFX.setPlugin(this);

        if(!checkVersion(info)) {
            panic();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Bande bande = Bande.getBande(player);
                    if((bande == null || !bande.owner().equals(player)) && permission.playerHas(player, "bandehus")) {
                        permission.playerRemove(player, "bandehus");
                        continue;
                    }
                    if(bande == null || !bande.owner().equals(player)){
                        continue;
                    }
                    if(bande.isUnlockedHouse() && !permission.playerHas(player, "bandehus")) {
                        permission.playerAdd(player, "bandehus");
                        continue;
                    }
                    if(!bande.isUnlockedHouse() && permission.playerHas(player, "bandehus")) {
                        permission.playerRemove(player, "bandehus");
                    }

                }
            }
        }.runTaskTimer(this, 10, 10);


    }

    public void panic() {
        getLogger().warning("FORÆLDET VERSION AF BANDE, DISABLER PLUGINNET");
        getLogger().warning("ADD \"wavebleak\" PÅ DISCORD FOR AT FIXE DET");
        getLogger().warning("FORÆLDET VERSION AF BANDE, DISABLER PLUGINNET");
        Bukkit.broadcastMessage(ChatColor.RED + "FORÆLDET VERSION AF BANDE, DISABLER PLUGINNET, ADD \"wavebleak\" PÅ DISCORD FOR AT FIXE DET"); //TODO: Make this pretti
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if(economyProvider != null) {
            economy = economyProvider.getProvider();
        }

    }

    public void load() {
        bander = manager.refreshBande();
        territories = manager.refreshTerrirtory();
    }

    public void save() {
        manager.saveBande(bander);
        manager.saveTerritory(territories);
    }

    @Override
    public void onDisable() {
        save();
        for(BandeTerritorie territorie : territories) {

        }
    }
}

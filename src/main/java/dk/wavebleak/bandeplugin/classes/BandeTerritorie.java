package dk.wavebleak.bandeplugin.classes;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.utils.ColorUtils;
import dk.wavebleak.bandeplugin.utils.ItemUtils;
import dk.wavebleak.bandeplugin.utils.StringUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import net.minecraft.server.v1_8_R3.BlockBanner;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.material.MaterialData;

import java.util.*;

public class BandeTerritorie {
    private String ownedBandeID;
    private String name;
    private int x;
    private int y;
    private int z;
    private String world;
    private int minBread;
    private int maxBread;
    private int breadInterval;
    private int generatedBread;
    private int doNotTouch;

    public BandeTerritorie(String name, Location location, int maxBread, int minBread, int breadInterval) {
        this.ownedBandeID = null;
        this.name = name;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
        this.minBread = minBread;
        this.maxBread = maxBread;
        this.breadInterval = breadInterval;
        this.generatedBread = 0;
        this.doNotTouch = location.getBlock().getState().getRawData();

    }
    public BandeTerritorie(String ownedBandeID, String name, int x, int y, int z, String world, int minBread, int maxBread, int breadInterval, int generatedBread, int doNotTouch) {
        this.ownedBandeID = ownedBandeID;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.minBread = minBread;
        this.maxBread = maxBread;
        this.breadInterval = breadInterval;
        this.generatedBread = generatedBread;
        this.doNotTouch = doNotTouch;
    }

    public boolean isBanner(Block block) {
        return isBanner(block.getLocation());
    }

    public boolean isBanner(Location location) {
        return location.getBlock().equals(getLocation().getBlock());
    }

    public void update() {
        updateBanner();

        Hologram hologram = DHAPI.getHologram(name);

        if(hologram == null) return;

        DHAPI.setHologramLines(hologram, getHoloLines());

        if(isCurrentlyOwned()) {
            DHAPI.addHologramLine(hologram, ItemUtils.getSkull(getOwnedBande().owner()));
        } else {
            DHAPI.addHologramLine(hologram, ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="));
        }
    }

    private void updateBanner() {
        getWorld().getBlockAt(getLocation()).setType(Material.STANDING_BANNER);
        Banner banner = (Banner) getWorld().getBlockAt(getLocation()).getState();

        banner.setBaseColor(getCurrentColor());

        if(isCurrentlyOwned()) {
            Color color = ColorUtils.getTop3ColorsFromSkin(getOwnedBande().owner())[0];
            if(color != null) {
                DyeColor dyeColor = ColorUtils.getClosestDyeColor(color);
                if(dyeColor != null) {
                    banner.setPatterns(Collections.singletonList(new Pattern(dyeColor, PatternType.FLOWER)));
                }
            }
        } else {
            banner.setPatterns(Collections.emptyList());
            generatedBread = 0;
        }

        banner.setRawData((byte) doNotTouch);

        banner.update();


    }

    public void spawn() {
        updateBanner();

        spawnHologram();

        BandePlugin.instance.threadMap.put(name + "-thread", new Thread(() -> {
            int i = 0;
            while(true) {
                try {
                    Thread.sleep(1000);
                    if(!isCurrentlyOwned()) continue;
                    if(generatedBread >= 64 * 4) continue;
                    i++;
                    Random random = new Random();
                    if(i == breadInterval) {
                        i = 0;
                        int bread = random.nextInt((maxBread - minBread) + 1) + minBread;

                        generatedBread += bread;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        BandePlugin.instance.threadMap.get(name + "-thread").start();
    }

    public void remove() {
        deleteHologram();
        getWorld().getBlockAt(getLocation()).setType(Material.AIR);
        BandePlugin.instance.threadMap.get(name + "-thread").interrupt();
        BandePlugin.instance.territories.remove(this);
    }

    public void spawnHologram() {
        Hologram hologram = DHAPI.createHologram(name, getLocation().clone().add(0.5, 5, 0.5));

        hologram.showAll();

        DHAPI.setHologramLines(hologram, getHoloLines());

        if(isCurrentlyOwned()) {
            DHAPI.addHologramLine(hologram, ItemUtils.getSkull(getOwnedBande().owner()));
        } else {
            DHAPI.addHologramLine(hologram, ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="));
        }

        DecentHologramsAPI.get().getHologramManager().registerHologram(hologram);

    }

    public List<String> getHoloLines() {
        return Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE TERRITORIE &8)"),
                ChatColor.translateAlternateColorCodes('&',"&1"),
                ChatColor.translateAlternateColorCodes('&',"&a&l" + name),
                ChatColor.translateAlternateColorCodes('&',"&aLaver " + minBread + " til " + maxBread + " brød af gangen"),
                ChatColor.translateAlternateColorCodes('&',"&aInterval: " + StringUtils.formatTime(breadInterval)),
                ChatColor.translateAlternateColorCodes('&',"&aEjes af: " + getOwnedName()),
                ChatColor.translateAlternateColorCodes('&',"&2"),
                ChatColor.translateAlternateColorCodes('&',"&8&l〔 &f&lTRYK PÅ BANNER &8&l 〕")
        );
    }

    public void deleteHologram() {
        DHAPI.getHologram(name).delete();
    }

    public Bande getOwnedBande() {
        return Bande.getBande(ownedBandeID);
    }

    public String getOwnedName() {
        String name = "Ingen...";
        if(isCurrentlyOwned()) {
            name = getCurrentlyOwned().getName();
        }
        return name;
    }

    public DyeColor getCurrentColor() {
        if(isCurrentlyOwned()) {
            return DyeColor.RED;
        } else {
            return DyeColor.LIME;
        }
    }

    public boolean isCurrentlyOwned() {
        return getCurrentlyOwned() != null;
    }

    public Bande getCurrentlyOwned() {
        return Bande.getBande(ownedBandeID);
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public String getOwnedBandeID() {
        return ownedBandeID;
    }

    public void setOwnedBandeID(String ownedBandeID) {
        this.ownedBandeID = ownedBandeID;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getMinBread() {
        return minBread;
    }

    public void setMinBread(int minBread) {
        this.minBread = minBread;
    }

    public int getMaxBread() {
        return maxBread;
    }

    public void setMaxBread(int maxBread) {
        this.maxBread = maxBread;
    }

    public int getBreadInterval() {
        return breadInterval;
    }

    public void setBreadInterval(int breadInterval) {
        this.breadInterval = breadInterval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getGeneratedBread() {
        return generatedBread;
    }

    public void setGeneratedBread(int generatedBread) {
        this.generatedBread = generatedBread;
    }

    public int getDoNotTouch() {
        return doNotTouch;
    }

    public void setDoNotTouch(int doNotTouch) {
        this.doNotTouch = doNotTouch;
    }
}

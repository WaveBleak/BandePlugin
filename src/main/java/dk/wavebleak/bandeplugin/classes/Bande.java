package dk.wavebleak.bandeplugin.classes;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.utils.ColorUtil;
import dk.wavebleak.bandeplugin.utils.InstantFirework;
import dk.wavebleak.bandeplugin.utils.ItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Bande {

    private String bandeID;
    private final HashMap<String, Integer> membersUUID;
    private List<String> rivals;
    private List<String> allies;
    private final String ownerUUID;
    private int level;
    private int kills;
    private int vagtKills;
    private int offiKills;
    private int insKills;
    private int dirKills;
    private int deaths;
    private long bank;
    private String name;


    public Bande(HashMap<OfflinePlayer, Integer> members, OfflinePlayer owner, List<String> rivals, List<String> allies, int level, int kills, int vagtKills, int offiKills, int insKills, int dirKills, int deaths, long bank, String name) {
        this.membersUUID = new HashMap<>();
        for (OfflinePlayer player : members.keySet()) {
            this.membersUUID.put(player.getUniqueId().toString(), members.get(player));
        }
        this.ownerUUID = owner.getUniqueId().toString();
        this.rivals = rivals;
        this.allies = allies;
        this.level = level;
        this.kills = kills;
        this.vagtKills = vagtKills;
        this.offiKills = offiKills;
        this.insKills = insKills;
        this.dirKills = dirKills;
        this.deaths = deaths;
        this.bank = bank;
        this.name = name;

        this.bandeID = UUID.randomUUID().toString();
    }

    public Bande(HashMap<String, Integer> membersUUID, List<String> rivals, List<String> allies, String ownerUUID, int level, int kills, int vagtKills, int offiKills, int insKills, int dirKills, int deaths, long bank, String name) {
        this.membersUUID = membersUUID;
        this.rivals = rivals;
        this.allies = allies;
        this.ownerUUID = ownerUUID;
        this.level = level;
        this.kills = kills;
        this.vagtKills = vagtKills;
        this.offiKills = offiKills;
        this.insKills = insKills;
        this.dirKills = dirKills;
        this.deaths = deaths;
        this.bank = bank;
        this.name = name;

        this.bandeID = UUID.randomUUID().toString();
    }

    public ItemStack getDisplaySkull() {
        ItemStack skull = ItemsUtil.getSkull(owner());

        return ItemsUtil.setNameAndLore(skull, "&b" + getName(), "&bEjet af: &f" + owner().getName(), "&bLevel: &f" + getLevel(), "&bVagt Kills: " + getVagtKills());
    }

    public void levelUp(boolean verbose) {
        if(this.level >= 5) return;
        if(verbose) {
            Color[] colors = ColorUtil.getTop3ColorsFromSkin(ownerUUID);
            for(OfflinePlayer player : members().keySet()) {
                if(!player.isOnline()) continue;
                Player target = player.getPlayer();

                new InstantFirework(
                        FireworkEffect.builder()
                            .withTrail()
                            .withColor(colors[0], colors[1], colors[2])
                            .build(),
                        target.getLocation()
                );
            }
        }
        this.level++;
    }

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public String getKDR() {
        if(deaths == 0 || kills == 0) {
            return "NaN";
        }
        return String.valueOf(kills / deaths);
    }

    public int getLevel() {
        return this.level;
    }

    public void levelUp() {
        levelUp(true);
    }

    public int getMaxMebmers() {
        return 3 + (level * 2);
    }


    public int getCurrentMembers() {
        return members().size();
    }

    public boolean addMember(OfflinePlayer player, int permissionLevel) {
        if(getCurrentMembers() >= getMaxMebmers()) {
            return false;
        }
        membersUUID.put(player.getUniqueId().toString(), permissionLevel);
        return true;
    }



    public static Bande getBande(OfflinePlayer player) {
        BandePlugin.instance.bander.stream().forEach(x -> {
            x.members().keySet().forEach(a -> {
                System.out.println("Checking member " + a.getName());
            });
        });
        return BandePlugin.instance.bander.stream().filter(x -> x.members().containsKey(player)).findFirst().orElse(null);
    }

    public static boolean isSameTeam(OfflinePlayer first, OfflinePlayer second) {
        Bande team1 = getBande(first);
        Bande team2 = getBande(second);

        if(team1 == null || team2 == null) {
            return false;
        }
        return team1 == team2;
    }



    public OfflinePlayer owner() {
        return Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
    }
    public HashMap<OfflinePlayer, Integer> members() {
        HashMap<OfflinePlayer, Integer> toReturn = new HashMap<>();
        for(String uuid : membersUUID.keySet()) {
            toReturn.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), membersUUID.get(uuid));
        }
        return toReturn;
    }



    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVagtKills() {
        return vagtKills;
    }

    public void setVagtKills(int vagtKills) {
        this.vagtKills = vagtKills;
    }

    public int getOffiKills() {
        return offiKills;
    }

    public void setOffiKills(int offiKills) {
        this.offiKills = offiKills;
    }

    public int getInsKills() {
        return insKills;
    }

    public void setInsKills(int insKills) {
        this.insKills = insKills;
    }

    public int getDirKills() {
        return dirKills;
    }

    public void setDirKills(int dirKills) {
        this.dirKills = dirKills;
    }

    public long getBank() {
        return bank;
    }

    public void setBank(long bank) {
        this.bank = bank;
    }

    public List<String> getRivals() {
        return rivals;
    }

    public void setRivals(List<String> rivals) {
        this.rivals = rivals;
    }

    public List<String> getAllies() {
        return allies;
    }

    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    public String getBandeID() {
        return bandeID;
    }

    public void setBandeID(String bandeID) {
        this.bandeID = bandeID;
    }

    public HashMap<String, Integer> getMembersUUID() {
        return membersUUID;
    }


    public static enum PermissionLevel {
        KINGPIN(4),
        RIGHTHANDMAN(3),
        PUSHER(2),
        ROOKIE(1);

        private final int value;
        PermissionLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

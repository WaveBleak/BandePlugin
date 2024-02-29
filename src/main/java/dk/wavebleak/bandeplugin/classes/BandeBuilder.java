package dk.wavebleak.bandeplugin.classes;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class BandeBuilder {

    private HashMap<String, Integer> membersUUID;
    private List<String> rivals;
    private List<String> allies;
    private String ownerUUID;
    private int level;
    private int kills;
    private int vagtKills;
    private int offiKills;
    private int insKills;
    private int dirKills;
    private int deaths;
    private long bank;
    private String name;
    private boolean unlockedTerritory;
    private boolean unlockedHouse;

    public BandeBuilder(OfflinePlayer owner) {
        HashMap<String, Integer> members = new HashMap<>();
        members.put(owner.getUniqueId().toString(), Bande.PermissionLevel.KINGPIN);
        this.membersUUID = members;
        this.rivals = new ArrayList<>();
        this.allies = new ArrayList<>();
        this.ownerUUID = owner.getUniqueId().toString();
        this.level = 1;
        this.kills = 0;
        this.vagtKills = 0;
        this.offiKills = 0;
        this.insKills = 0;
        this.dirKills = 0;
        this.deaths = 0;
        this.bank = 0;
        this.name = "UNNAMED";
        this.unlockedTerritory = false;
        this.unlockedHouse = false;
    }

    public Bande create() {
        return new Bande(membersUUID, rivals, allies, ownerUUID, level, kills, vagtKills, offiKills, insKills, dirKills, deaths, bank, name, unlockedTerritory, unlockedHouse);
    }

    public BandeBuilder addMember(OfflinePlayer member) {
        this.membersUUID.put(member.getUniqueId().toString(), Bande.PermissionLevel.ROOKIE);
        return this;
    }

    public BandeBuilder addMembers(List<OfflinePlayer> members) {
        for(OfflinePlayer member : members) {
            this.membersUUID.put(member.getUniqueId().toString(), Bande.PermissionLevel.ROOKIE);
        }
        return this;
    }

    public BandeBuilder setMembersUUID(HashMap<String, Integer> membersUUID) {
        this.membersUUID = membersUUID;
        return this;
    }

    public BandeBuilder setRivals(List<String> rivals) {
        this.rivals = rivals;
        return this;
    }

    public BandeBuilder setAllies(List<String> allies) {
        this.allies = allies;
        return this;
    }

    public BandeBuilder setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
        return this;
    }

    public BandeBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public BandeBuilder setKills(int kills) {

        this.kills = kills;
        return this;
    }

    public BandeBuilder setVagtKills(int vagtKills) {

        this.vagtKills = vagtKills;
        return this;
    }

    public BandeBuilder setOffiKills(int offiKills) {

        this.offiKills = offiKills;
        return this;
    }

    public BandeBuilder setInsKills(int insKills) {

        this.insKills = insKills;
        return this;
    }

    public BandeBuilder setDirKills(int dirKills) {

        this.dirKills = dirKills;
        return this;
    }

    public BandeBuilder setDeaths(int deaths) {

        this.deaths = deaths;
        return this;
    }

    public BandeBuilder setBank(long bank) {

        this.bank = bank;
        return this;
    }

    public BandeBuilder setName(String name) {

        this.name = name;
        return this;
    }

    public BandeBuilder setUnlockedTerritory(boolean unlockedTerritory) {

        this.unlockedTerritory = unlockedTerritory;
        return this;
    }

    public void setUnlockedHouse(boolean unlockedHouse) {
        this.unlockedHouse = unlockedHouse;
    }
}

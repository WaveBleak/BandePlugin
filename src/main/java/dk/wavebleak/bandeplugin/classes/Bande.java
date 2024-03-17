package dk.wavebleak.bandeplugin.classes;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.utils.ColorUtils;
import dk.wavebleak.bandeplugin.utils.InstantFirework;
import dk.wavebleak.bandeplugin.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class Bande {

    private String bandeID;
    private final HashMap<String, Integer> membersUUID;
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
    private int allyHitMitigation;
    private int rivalKills;

    public Bande(HashMap<String, Integer> membersUUID, List<String> rivals, List<String> allies, String ownerUUID, int level, int kills, int vagtKills, int offiKills, int insKills, int dirKills, int deaths, long bank, String name, boolean unlockedTerritory, boolean unlockedHouse, int allyHitMitigation, int rivalKills, String bandeID) {
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
        this.unlockedTerritory = unlockedTerritory;
        this.unlockedHouse = unlockedHouse;
        this.allyHitMitigation = allyHitMitigation;
        this.rivalKills = rivalKills;

        this.bandeID = bandeID;
    }


    public void addDirKill() {
        this.dirKills++;
    }

    public void addInsKill() {
        this.insKills++;
    }

    public void addOfficerKill() {
        this.offiKills++;
    }

    public void addVagtKill() {
        this.vagtKills++;
    }

    public int maxLevel() {
        return 5;
    }


    public void addAlly(Bande bande) {
        Bukkit.broadcastMessage("LIST: " + String.join(", ", allies));
        Bukkit.broadcastMessage("BANDEID: " + bande.bandeID);
        this.allies.add(bande.bandeID);
        Bukkit.broadcastMessage("LIST: " + String.join(", ", allies));
    }

    public boolean hasAlly(Bande bande) {
        return this.allies.contains(bande.bandeID);
    }
    public boolean hasRival(Bande bande) {
        return this.rivals.contains(bande.bandeID);
    }

    public void addRival(Bande bande) {
        this.rivals.add(bande.bandeID);
    }

    public void removeAlly(Bande bande) {
        this.allies.remove(bande.bandeID);
    }

    public void removeRival(Bande bande) {
        this.rivals.remove(bande.bandeID);
    }

    public void transferOwner(OfflinePlayer newOwner) {
        OfflinePlayer previousOwner = owner();
        this.ownerUUID = newOwner.getUniqueId().toString();

        membersUUID.put(newOwner.getUniqueId().toString(), PermissionLevel.KINGPIN);
        membersUUID.put(previousOwner.getUniqueId().toString(), PermissionLevel.RIGHTHANDMAN);

        for(OfflinePlayer member : members().keySet()) {
            if(member.equals(newOwner)) continue;
            if(member.equals(previousOwner)) continue;
            if(member.isOnline()) {
                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8)&f &n"+previousOwner.getName()+"&r &fhar overdraget ejerskabet af banden til "+newOwner.getName() + "&f!"));
            }
        }

        if(newOwner.isOnline()) newOwner.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8)&f &n"+previousOwner.getName()+"&r &fhar overdraget dig ejerskabet af banden!"));
        if(previousOwner.isOnline()) previousOwner.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu overgav ejerskabet af banden til &n"+newOwner.getName()+"&r!"));
    }
    public ItemStack getDisplaySkull() {
        ItemStack skull = ItemUtils.getSkull(owner());

        return ItemUtils.setNameAndLore(skull, "&c&l" + getName(), "&8⬤ &fEjer: &7" + owner().getName(), " ", "&8⬤ &fLevel: &7" + getLevel(), "&8⬤ &fBank: &7" + getBank(), " ", "&8⬤ &fRival Kills: &7" + getRivalKills(), "&8⬤ &fVagt Kills: &7" + getVagtKills(), "&8⬤ &fOfficer Kills: &7" + getOffiKills(), "&8⬤ &fInspektør Kills: &7" + getInsKills(), "&8⬤ &fDirektør Kills: &7" + getDirKills());
    }

    public Requirement requirement1() {
        switch (this.level) {
            case 1:
                return new Requirement(Requirement.LevelUpRequirement.RIVALKILLS, 30);
            case 2:
                return new Requirement(Requirement.LevelUpRequirement.ALLIES, 1);
            case 3:
                return new Requirement(Requirement.LevelUpRequirement.MEMBERS, 6);
            case 4:
                return new Requirement(Requirement.LevelUpRequirement.OFFIKILLS, 20);
            case 5:
                return new Requirement(Requirement.LevelUpRequirement.KILLS, 1000);
        }
        return new Requirement(Requirement.LevelUpRequirement.DIRKILLS, 9999999);
    }
    public Requirement requirement2()  {
        switch (this.level) {
            case 1:
                return new Requirement(Requirement.LevelUpRequirement.BANK, 25000);
            case 2:
                return new Requirement(Requirement.LevelUpRequirement.RIVALS, 2);
            case 3:
                return new Requirement(Requirement.LevelUpRequirement.OFFIKILLS, 3);
            case 4:
                return new Requirement(Requirement.LevelUpRequirement.ALLIES, 4);
            case 5:
                return new Requirement(Requirement.LevelUpRequirement.BANK, 1000000);
        }
        return new Requirement(Requirement.LevelUpRequirement.KILLS, 9999999);
    }
    public Requirement requirement3() {
        switch (this.level) {
            case 1:
                return new Requirement(Requirement.LevelUpRequirement.VAGTKILLS, 2);
            case 2:
                return new Requirement(Requirement.LevelUpRequirement.VAGTKILLS, 5);
            case 3:
                return new Requirement(Requirement.LevelUpRequirement.KILLS, 500);
            case 4:
                return new Requirement(Requirement.LevelUpRequirement.RIVALS, 4);
            case 5:
                return new Requirement(Requirement.LevelUpRequirement.INSKILLS, 3);
        }
        return new Requirement(Requirement.LevelUpRequirement.MEMBERS, 9999999);
    }

    public boolean canLevelUp() {
        return requirement1().meetsRequirements(this) && requirement2().meetsRequirements(this) && requirement3().meetsRequirements(this);
    }

    public void setMemberRank(OfflinePlayer player, int newValue) {
        membersUUID.put(player.getUniqueId().toString(), newValue);
    }

    public void levelUp(boolean verbose, boolean bypassRequirements) {
        if(this.level > 5) return;
        if(!bypassRequirements) {
            if(!canLevelUp()) return;
            requirement1().subtractIfMoneyRequirement(this);
            requirement2().subtractIfMoneyRequirement(this);
            requirement3().subtractIfMoneyRequirement(this);
        }
        if(verbose) {
            Color[] colors = ColorUtils.getTop3ColorsFromSkin(ownerUUID);
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

    public boolean deposit(OfflinePlayer player, long amount, boolean isPlayerAffected) {
        if(isPlayerAffected) {
            if(BandePlugin.economy.getBalance(player) >= amount) {
                BandePlugin.economy.withdrawPlayer(player, amount);
                this.bank += amount;
                return true;
            } else {
                return false;
            }
        }
        this.bank += amount;
        return true;
    }

    public boolean withdraw(OfflinePlayer player, long amount, boolean isPlayerAffected) {
        if(isPlayerAffected) {
            if(this.bank >= amount) {
                BandePlugin.economy.depositPlayer(player, amount);
                this.bank -= amount;
                return true;
            } else {
                return false;
            }
        }
        this.bank -= amount;
        return true;
    }


    public void invite(OfflinePlayer inviter, OfflinePlayer invitee) {
        BandePlugin.invites.put(invitee, this);

        if(invitee.isOnline()) invitee.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f&n"+inviter.getName()+"&r har inviteret dig til banden &n"+getName()+"&r!"));
        if(inviter.isOnline()) inviter.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har inviteret &n"+invitee.getName()+"&r til banden!"));
        for(OfflinePlayer member : members().keySet()) {
            if(member.equals(inviter)) continue;
            if(member.isOnline()) {
                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8)&f &n"+inviter.getName()+"&r &fhar inviteret &n"+invitee.getName()+"&r til banden!"));
            }
        }
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

    public int getMaxMebmers() {
        return 3 + (level * 2);
    }


    public int getCurrentMembers() {
        return members().size();
    }


    public void kickMember(OfflinePlayer victim, OfflinePlayer player) {
        membersUUID.remove(victim.getUniqueId().toString());

        for(OfflinePlayer member : members().keySet()) {
            if(member.equals(player)) continue;
            if(member.isOnline()) {
                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) "+player.getName()+" &fhar smidt "+victim.getName()+"&f ud fra banden!"));
            }
        }
        if(player.isOnline()) player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har smidt &n"+victim.getName()+"&r &fud fra banden!"));
        if(victim.isOnline()) victim.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu er blevet smidt ud fra banden &n"+getName()+"&r &faf &n"+player.getName()+"&r!"));
        for(Map.Entry<Player, InventoryData> entry : BandePlugin.inventoryManager.entrySet()) {
            if(entry.getKey().equals(victim)) {
                if(victim.isOnline()) {
                    if(victim.getPlayer().getOpenInventory().getTopInventory().equals(entry.getValue().getInventory())) {
                        victim.getPlayer().closeInventory();
                    }
                }
            }
        }
        BandePlugin.instance.save();
    }

    public void leave(OfflinePlayer player) {
        membersUUID.remove(player.getUniqueId().toString());

        for(OfflinePlayer member : members().keySet()) {
            if(member.equals(player)) continue;
            if(member.isOnline()) {
                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &c"+player.getName()+" &fhar forladt banden!"));
            }
        }
        for(Map.Entry<Player, InventoryData> entry : BandePlugin.inventoryManager.entrySet()) {
            if(entry.getKey().equals(player)) {
                if(player.isOnline()) {
                    if(player.getPlayer().getOpenInventory().getTopInventory().equals(entry.getValue().getInventory())) {
                        player.getPlayer().closeInventory();
                    }
                }
            }
        }
        if(player.isOnline()) player.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har forladt banden "+getName()+"&f!"));
        BandePlugin.instance.save();
    }

    public void disband() {
        for(OfflinePlayer member : members().keySet()) {
            if(member.equals(owner())) continue;
            if(member.isOnline()) {
                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &c"+ owner().getName()+"&f har opløst banden!"));
            }
            for(Map.Entry<Player, InventoryData> entry : BandePlugin.inventoryManager.entrySet()) {
                if(entry.getKey().equals(member)) {
                    if(member.isOnline()) {
                        if(member.getPlayer().getOpenInventory().getTopInventory().equals(entry.getValue().getInventory())) {
                            member.getPlayer().closeInventory();
                        }
                    }
                }
            }
        }
        if(owner().isOnline()) owner().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har opløst banden!"));

        BandePlugin.instance.bander.remove(this);
        BandePlugin.instance.save();

    }

    public boolean addMember(OfflinePlayer player, int permissionLevel) {
        if(getCurrentMembers() >= getMaxMebmers()) {
            return false;
        }
        membersUUID.put(player.getUniqueId().toString(), permissionLevel);
        return true;
    }



    public static Bande getBande(OfflinePlayer player) {
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

    public String[] genererateLines() {
        List<Requirement> list = Arrays.asList(requirement1(), requirement2(), requirement3()).stream().sorted(Comparator.comparingDouble(x -> x.requirement.priority())).collect(Collectors.toList());

        String[] requiremetntsAsString = new String[] {
                list.get(0).requirement.getSuffix(),
                list.get(1).requirement.getSuffix(),
                list.get(2).requirement.getSuffix()
        };

        int[] amountForRequirements = new int[] {
                list.get(0).amount,
                list.get(1).amount,
                list.get(2).amount
        };

        long[] currentAmountForRequirements = new long[] {
                list.get(0).currentAmount(this),
                list.get(1).currentAmount(this),
                list.get(2).currentAmount(this)
        };

        ArrayList<String> toReturn = new ArrayList<>();

        for(int i = 0; i <= 2; i++){
            String req = "&c"+currentAmountForRequirements[i];
            if(currentAmountForRequirements[i] >= amountForRequirements[i]) req = "&a"+amountForRequirements[i];
            toReturn.add(ChatColor.translateAlternateColorCodes('&', "&8⬤ &f&n"+amountForRequirements[i]+"&r&f "+requiremetntsAsString[i]+"&8 【 "+req+" / "+amountForRequirements[i]+" &8】"));
        }
        return toReturn.toArray(new String[0]);
    }

    public int getMemberRank(OfflinePlayer member) {
        Optional<Map.Entry<OfflinePlayer, Integer>> entry = members().entrySet().stream().filter(set -> set.getKey().equals(member)).findAny();

        if(entry.isPresent()) return entry.get().getValue();

        return 0;
    }

    public String getMemberRankString(OfflinePlayer player) {
        switch (getMemberRank(player)) {
            case 1:
                return "Rookie";
            case 2:
                return "Pusher";
            case 3:
                return "Right hand man";
            case 4:
                return "Kingpin";
            default:
                return "FEJL";
        }
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
    public void setAllyHitMitigation(int value) {
        this.allyHitMitigation = value;
    }

    public int getAllyHitMitigation() {
        return this.allyHitMitigation;
    }

    public List<Bande> getRivals() {
        if(rivals.isEmpty()) return Collections.emptyList();
        List<Bande> rivals = new ArrayList<>();
        for(String rivalID : this.rivals) {
            Optional<Bande> maybeRival = BandePlugin.instance.bander.stream().filter(bande -> bande.bandeID.equals(rivalID)).findAny();

            if(maybeRival.isPresent()) rivals.add(maybeRival.get());
        }
        return rivals;
    }

    public void setRivals(List<String> rivals) {
        this.rivals = rivals;
    }

    public List<Bande> getAllies() {
        if(allies.isEmpty()) return Collections.emptyList();
        List<Bande> allies = new ArrayList<>();
        for(String allyID : this.allies) {
            Optional<Bande> maybeAlly = BandePlugin.instance.bander.stream().filter(bande -> bande.bandeID.equals(allyID)).findAny();
            
            if(maybeAlly.isPresent()) allies.add(maybeAlly.get());
        }
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

    public boolean isUnlockedTerritory() {
        return unlockedTerritory;
    }

    public void setUnlockedTerritory(boolean unlockedTerritory) {
        this.unlockedTerritory = unlockedTerritory;
    }

    public boolean isUnlockedHouse() {
        return unlockedHouse;
    }

    public void setUnlockedHouse(boolean unlockedHouse) {
        this.unlockedHouse = unlockedHouse;
    }
    public int getRivalKills() {
        return this.rivalKills;
    }
    public void setRivalKills(int value) {
        this.rivalKills = value;
    }


    public static class PermissionLevel {
        public static int KINGPIN = 4;
        public static int RIGHTHANDMAN = 3;
        public static int PUSHER = 2;
        public static int ROOKIE = 1;
    }

    public static class Requirement {
        private LevelUpRequirement requirement;
        private int amount;

        public Requirement(LevelUpRequirement requirement, int amount) {
            this.requirement = requirement;
            this.amount = amount;
        }

        public boolean meetsRequirements(Bande bande) {
            return currentAmount(bande) >= amount;
        }

        public long currentAmount(Bande bande) {
            switch (requirement) {
                case RIVALS:
                    return bande.getRivals().size();
                case ALLIES:
                    return bande.getAllies().size();
                case KILLS:
                    return bande.getKills();
                case RIVALKILLS:
                    return bande.getRivalKills();
                case VAGTKILLS:
                    return bande.vagtKills;
                case OFFIKILLS:
                    return bande.offiKills;
                case INSKILLS:
                    return bande.insKills;
                case DIRKILLS:
                    return bande.dirKills;
                case BANK:
                    return bande.bank;
                case MEMBERS:
                    return bande.members().size();
                default:
                    return 0;
            }
        }

        public void subtractIfMoneyRequirement(Bande bande) {
            if(requirement.equals(LevelUpRequirement.BANK)) bande.withdraw(null, amount, false);
        }

        //

        public enum LevelUpRequirement {
            BANK{
                @Override
                public String getSuffix() {
                    return "i bande banken";
                }
                @Override
                public int priority() {
                    return 0;
                }
            },
            MEMBERS{
                @Override
                public String getSuffix() {
                    return "medlemmer i banden";
                }
                @Override
                public int priority() {
                    return 1;
                }
            },
            ALLIES{
                @Override
                public String getSuffix() {
                    return "allierede";
                }
                @Override
                public int priority() {
                    return 2;
                }
            },
            RIVALS{
                @Override
                public String getSuffix() {
                    return "rivaler";
                }
                @Override
                public int priority() {
                    return 3;
                }
            },
            KILLS{
                @Override
                public String getSuffix() {
                    return "kills";
                }
                @Override
                public int priority() {
                    return 4;
                }
            },
            RIVALKILLS {
                @Override
                String getSuffix() {
                    return "rival kills";
                }

                @Override
                int priority() {
                    return 5;
                }
            },
            VAGTKILLS{
                @Override
                public String getSuffix() {
                    return "vagt kills";
                }
                @Override
                public int priority() {
                    return 6;
                }
            },
            OFFIKILLS{
                @Override
                public String getSuffix() {
                    return "officer kills";
                }
                @Override
                public int priority() {
                    return 7;
                }
            },
            INSKILLS{
                @Override
                public String getSuffix() {
                    return "inspektør kills";
                }
                @Override
                public int priority() {
                    return 8;
                }
            },
            DIRKILLS{
                @Override
                public String getSuffix() {
                    return "direktør kills";
                }
                @Override
                public int priority() {
                    return 9;
                }
            };

            abstract String getSuffix();
            abstract int priority();
        }
    }
}

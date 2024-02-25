package dk.wavebleak.bandeplugin.classes;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.utils.ColorUtil;
import dk.wavebleak.bandeplugin.utils.InstantFirework;
import dk.wavebleak.bandeplugin.utils.ItemsUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

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

    public int maxLevel() {
        return 5;
    }

    public ItemStack getDisplaySkull() {
        ItemStack skull = ItemsUtil.getSkull(owner());

        return ItemsUtil.setNameAndLore(skull, "&c&l" + getName(), "&fEjet af: &7" + owner().getName(), "&fLevel: &7" + getLevel(), "&fVagt Kills: &7" + getVagtKills());
    }

    public Requirement requirement1() {
        switch (this.level) {
            case 1:
                return new Requirement(Requirement.LevelUpRequirement.KILLS, 100);
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

    public void levelUp(boolean verbose, boolean bypassRequirements) {
        if(this.level > 5) return;
        if(!bypassRequirements) {
            if(!canLevelUp()) return;
            requirement1().subtractIfMoneyRequirement(this);
            requirement2().subtractIfMoneyRequirement(this);
            requirement3().subtractIfMoneyRequirement(this);
        }
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
                member.getPlayer().sendMessage(ChatColor.RED + player.getName() + " har kicket " + victim.getName() + " fra banden!");
            }
        }
        if(player.isOnline()) player.getPlayer().sendMessage(ChatColor.GREEN + victim.getName() + " er nu smidt ud fra banden!");
        if(victim.isOnline()) victim.getPlayer().sendMessage(ChatColor.RED + player.getName() + " har smidt dig ud af banden: " + getName());
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
            toReturn.add(ChatColor.translateAlternateColorCodes('&', "&8\u2B24 &f&n"+amountForRequirements[i]+"&r&f "+requiremetntsAsString[i]+"&8 \u3010 "+req+" / "+amountForRequirements[i]+" &8\u3011"));
        }
        return toReturn.toArray(new String[0]);
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
            VAGTKILLS{
                @Override
                public String getSuffix() {
                    return "vagt kills";
                }
                @Override
                public int priority() {
                    return 4;
                }
            },
            OFFIKILLS{
                @Override
                public String getSuffix() {
                    return "officer kills";
                }
                @Override
                public int priority() {
                    return 5;
                }
            },
            INSKILLS{
                @Override
                public String getSuffix() {
                    return "inspekt\u00f8r kills";
                }
                @Override
                public int priority() {
                    return 6;
                }
            },
            DIRKILLS{
                @Override
                public String getSuffix() {
                    return "direkt\u00f8r kills";
                }
                @Override
                public int priority() {
                    return 7;
                }
            };

            abstract String getSuffix();
            abstract int priority();
        }
    }
}

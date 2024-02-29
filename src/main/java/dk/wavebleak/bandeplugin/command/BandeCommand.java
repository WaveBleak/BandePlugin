package dk.wavebleak.bandeplugin.command;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.*;
import dk.wavebleak.bandeplugin.utils.InventoryUtil;
import dk.wavebleak.bandeplugin.utils.ItemUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static dk.wavebleak.bandeplugin.BandePlugin.economy;
import static dk.wavebleak.bandeplugin.BandePlugin.inventoryManager;

public class BandeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(strings.length == 1 && strings[0].equalsIgnoreCase("load") && player.isOp()) {
            BandePlugin.instance.load();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBande pluginnet er nu loaded fra databasen!"));
            return true;
        }
        if(strings.length == 1 && strings[0].equalsIgnoreCase("save") && player.isOp()) {
            BandePlugin.instance.save();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBande pluginnet er nu gemt til databasen!"));
            return true;
        }

        openMainInventory(player);


        return true;
    }

    public void openMainInventory(Player player) {
        Bande bande = Bande.getBande(player);

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" FORSIDE");
        InventoryUtil.createBorders(inventory);
        if(bande == null) { // Ingen bande

            inventory.setItem(20, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg3MDMxYzQ3MjZkZGVkZDY1YjZhMTFkMzE0N2U2NzI0ZGVmYmIyOTBkYTI5Y2JiNzlkYTI0OTA1NDZjYmYifX19"), "&c&lTOP LEVEL", "&fplease make a leaderboard", "&ffor top level","&fpookie wookie bear :3"));
            inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0="), "&4&lOpret en bande", "&8\u2B24 &fDet koster &c$5000 &fat oprette en bande!"));
            inventory.setItem(24, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMzA0OGY1M2IyNGZiMzZlYmFjNjRkODU4Mzg5MTM1ODgzZjI1ODc0ZTQ1NDZkMWZjZDg5YzMwYmQ2ZjY1NiJ9fX0="), "&c&lDINE INVITATIONER", "&8\u2B24 &fKlik her for at se dine invitationer!"));

        } else { // Har en bande
            inventory.setItem(4, bande.getDisplaySkull());

            String levelupHead;
            String levelupColor;
            if(bande.canLevelUp()) {
                levelupHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRhMDI3NDc3MTk3YzZmZDdhZDMzMDE0NTQ2ZGUzOTJiNGE1MWM2MzRlYTY4YzhiN2JjYzAxMzFjODNlM2YifX19";
                levelupColor = "&a";
                ArrayList<String> lore = new ArrayList<>();
                lore.add("&8"+bande.getLevel()+" &8&l\u00BB &7"+(bande.getLevel()+1));
                lore.add(" ");
                lore.add("&f&lKRAV:");
                lore.addAll(Arrays.asList(bande.genererateLines()));
                lore.add(" ");
                lore.add("&8&l\u3014 &f&lKLIK HER &8&l\u3015");
                inventory.setItem(13, ItemUtils.setNameAndLore(ItemUtils.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
            }else{
                levelupHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ5Mjg3NjE2MzQzZDgzM2U5ZTczMTcxNTljYWEyY2IzZTU5NzQ1MTEzOTYyYzEzNzkwNTJjZTQ3ODg4NGZhIn19fQ==";
                levelupColor = "&c";
                ArrayList<String> lore = new ArrayList<>();
                lore.add("&8"+bande.getLevel()+" &8&l\u00BB &7"+(bande.getLevel()+1));
                lore.add(" ");
                lore.add("&f&lKRAV:");
                lore.addAll(Arrays.asList(bande.genererateLines()));
                lore.add(" ");
                lore.add(" ");
                lore.add("&8&l\u3014 &c&lI HAR IKKE OPN\u00C5ET ALLE KRAV &8&l\u3015");
                inventory.setItem(13, ItemUtils.setNameAndLore(ItemUtils.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
            }

            inventory.setItem(19, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFkZGIyZmQ3NzIwMDcxNjk0ZTllODBhNmY0YThiOGFiYTc3NjBiYjFkYTQ2OGRlNjM3YTZiZjljODVlYTVhZSJ9fX0="), "&c&lBANK", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande \u00f8konomi", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
            inventory.setItem(21, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzZmM5YTQ1YmUxM2NhNTdhNzhiMjE3NjJjNmUxMjYyZGFlNDExZjEzMDQ4Yjk2M2Q5NzJhMjllMDcwOTZhYiJ9fX0="), "&c&lOPGRADERINGER", " ", "&f&lHer kan du k\u00f8be:", "&8\u2B24 &fAdgang til territorier" +  " ", "&8\u2B24 &fAdgang til bande hus", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            /* Fix for medlemmer icon showing up late */
            inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull(bande.owner()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            inventory.setItem(25, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVjOTZjZjNlNWM2OTgwYzcxNGYyNzkxN2I2NDM5YjA1OTY1MmY0Y2MyMTRhZGQ3MGRjNzQwYzZjMWZlNzBmMSJ9fX0="), "&c&lRELATIONER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fAllierede", "&8\u2B24 &fRivaler", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            List<String> inviteLore;
            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                inviteLore = Arrays.asList(" ", "&f&lHer kan du:", "&8\u2B24 &fInvitere medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015");
            }else {
                inviteLore = Arrays.asList(" ", "&c&lDu har ikke adgang!");
            }

            inventory.setItem(31, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhM2Y2ZjlmNTBhYzY3ZGI5OGI2OGExNWZkMDU5MDE1Mjg4MzZhMThjNzBmYjM5MmFhODlmNGI2MDgzNzY2YiJ9fX0="), "&c&lInvit\u00E9r Spillere", inviteLore.toArray(new String[0])));

            String leaveName;
            List<String> leaveLore;
            if(bande.owner().equals(player)) {
                leaveName = "&c&lOPL\u00d8S BANDE";
                leaveLore = Arrays.asList(" ", "&fOpl\u00f8s bande", " ", "&8&l\u3014 &f&lSHIFT + TRYK HER &8&l\u3015");
            } else {
                leaveName = "&c&lFORLAD BANDE";
                leaveLore = Arrays.asList(" ", "&fForlad bande", " ", "&8&l\u3014 &f&lSHIFT + TRYK HER &8&l\u3015");
            }

            inventory.setItem(40, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="), leaveName, leaveLore.toArray(new String[0])));

        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(bande == null) { // Ingen bande menu
                switch (event.getSlot()) {
                    case 24: // Invitationer
                        openInvitationsInventory(player);
                        break;
                    case 22: // Opret bande
                        if(economy.getBalance(player) >= 500) {
                            setBandeNameGUI(player);
                        }
                        break;
                }
            } else { // Bande menu
                switch (event.getSlot()) {
                    case 13:
                        if(bande.canLevelUp()) bande.levelUp(true, false);
                        openMainInventory(player);
                        break;
                    case 19:
                        showBankMenu(player);
                        break;
                    case 21:
                        showUpgradesMenu(player);
                        break;
                    case 23:
                        showMemberManager(player);
                        break;
                    case 25:
                        showRelationsMenu(player);
                        break;
                    case 31:
                        if(!(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN)) return;
                        showInviteMenu(player);
                    case 40:
                        if(!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) return;
                        if(bande.owner().equals(player)) {
                            showConfirmation(player).thenAccept(result -> {
                                if(result) {
                                    bande.disband();
                                } else {
                                    if(player.isOnline()) player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu skiftede mening! Banden er stadig intakt!"));
                                }
                            });
                            break;
                        }
                        bande.leave(player);
                }




            }
        };

        InventoryData data = new InventoryData(lambda, inventory);

        inventoryManager.put(player, data);


        if(bande != null) {
            List<Map.Entry<OfflinePlayer, Integer>> set = bande.members().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());
            final int[] i = {0};
            final boolean[] firstRun = {false};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(inventoryManager.get(player) == null) {
                        cancel();
                        return;
                    }
                    if (!inventoryManager.get(player).equals(data)) {
                        cancel();
                        return;
                    }
                    if(!firstRun[0]) {
                        Collections.reverse(set);
                        firstRun[0] = true;
                    }
                    Map.Entry<OfflinePlayer, Integer> entry = set.get(i[0]);
                    i[0]++;
                    if(i[0] >= set.size()) i[0] = 0;

                    inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull(entry.getKey()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
                }
            }.runTaskTimer(BandePlugin.instance, 0, 20);
        }
    }


    public void showBankMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" BANK");
        InventoryUtil.createBorders(inventory);

        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));
        inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFkZGIyZmQ3NzIwMDcxNjk0ZTllODBhNmY0YThiOGFiYTc3NjBiYjFkYTQ2OGRlNjM3YTZiZjljODVlYTVhZSJ9fX0="), "&c&l" + bande.getName() + " Bank", "&f&lSaldo: &f$" + bande.getBank()));
        inventory.setItem(20, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmYyZTE2ZWNiNWEwZmU3NTk3NDg5NTY5YjAwZmFjOTFjYmE2YWViOGQ0MTc5ZmI0ZWFkMWY3YzEzM2FiNjcwOSJ9fX0="), "&c&lDeposit", "&fPut penge ind i banken", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015" /* TRYK HER LINE */));
        inventory.setItem(24, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZkMTA4MzgzZGZhNWIwMmU4NjYzNTYwOTU0MTUyMGU0ZTE1ODk1MmQ2OGMxYzhmOGYyMDBlYzdlODg2NDJkIn19fQ=="), "&c&lWithdraw", "&fTag penge ud af banken", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015" /* TRYK HER LINE */));

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    break;
                case 20: //Deposit
                    promptForMoney(player, PromptType.DEPOSIT).thenAccept(amount -> {
                        economy.withdrawPlayer(player, amount);
                        bande.setBank(Math.round(bande.getBank() + amount));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                        showBankMenu(player);
                    });
                    break;
                case 24: //Withdraw
                    promptForMoney(player, PromptType.WITHDRAW).thenAccept(amount -> {
                        economy.depositPlayer(player, amount);
                        bande.setBank(Math.round(bande.getBank() - amount));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                        showBankMenu(player);
                    });
                    break;
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void showUpgradesMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" OPGRADERINGER");
        InventoryUtil.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        final int territoriePris = 30000;
        final int husPris = 50000;

        ChatColor territorieFarve;
        ChatColor husFarve;
        if (bande.getBank() >= territoriePris) {
            territorieFarve = ChatColor.GREEN;
        } else {
            territorieFarve = ChatColor.RED;
        }
        if (bande.getBank() >= husPris) {
            husFarve = ChatColor.GREEN;
        } else {
            husFarve = ChatColor.RED;
        }

        List<String> territorieLore = new ArrayList<>(Arrays.asList("&fMed territorier kan du eje et omr\u00E5de", "&fsom generere goder, der kan s\u00E6lges p\u00E5 det sorte marked", "&fmen andre bander kan angribe jer og stj\u00E6le jeres territorie!", ""));

        if(bande.isUnlockedTerritory()) {
            territorieLore.add("&a&lDin bande ejer allerrade dette");
        } else {
            territorieLore.add(territorieFarve + "$" + territoriePris);
        }

        List<String> husLore = new ArrayList<>(Arrays.asList("&fI bande huset f\u00E5r i en masse plads til at lave lige hvad i vil", "&fI kan gro planter eller have en masse storage", ""));
        if(bande.isUnlockedHouse()) {
            husLore.add("&a&lDin bande ejer allerrade dette");
        } else {
            husLore.add(husFarve + "$" + husPris);
        }

        if(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER) {
            husLore.add("&8&l\u3014 &f&lTRYK HER &8&l \u3015");
            territorieLore.add("&8&l\u3014 &f&lTRYK HER &8&l \u3015");
        } else {
            husLore.add("&8&l\u3014 &f&lDin bande har ikke givet dig tilladelse til dette \u3015");
            territorieLore.add("&8&l\u3014 &f&lDin bande har ikke givet dig tilladelse til dette \u3015");
        }


        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));
        inventory.setItem(21, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYxODQ2MTBjNTBjMmVmYjcyODViYzJkMjBmMzk0MzY0ZTgzNjdiYjMxNDg0MWMyMzhhNmE1MjFhMWVlMTJiZiJ9fX0="), "&c&lTerritorier", territorieLore.toArray(new String[0])));
        inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZWEzZTEzYTUxM2RhMTA1MWVhNTlkMTdlZGZiMDNlOGQ4Mzg4ZWRlODg4ODg0NzZkZmI2OTNmMmM4Mzk4In19fQ=="), "&c&lHuse", husLore.toArray(new String[0])));

        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    break;
                case 21: //Territorie
                    if(bande.isUnlockedTerritory()) return;

                    if(bande.getBank() >= territoriePris) {
                        bande.setUnlockedTerritory(true);
                        bande.setBank(bande.getBank() - territoriePris);

                        player.sendMessage("&8( &4&lBANDE &8) &fDu har k\u00f8bt adgang til bande territorier!");

                        for(OfflinePlayer member : bande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har k\u00f8bt adgang til bande territorier!"));
                            }
                        }

                    }
                    showUpgradesMenu(player);

                    break;
                case 23:
                    if(bande.isUnlockedTerritory()) return;

                    if(bande.getBank() >= husPris) {
                        bande.setUnlockedTerritory(true);
                        bande.setBank(bande.getBank() - husPris);

                        player.sendMessage("&8( &4&lBANDE &8) &fDu har k\u00f8bt adgang til bande huse!");

                        for(OfflinePlayer member : bande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har k\u00f8bt adgang til bande huse!"));
                            }
                        }
                    }
                    showUpgradesMenu(player);
                    break;
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

    }

    public void showMemberManager(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" MEDLEMMER");
        InventoryUtil.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));

        int i = 9;
        HashMap<Integer, OfflinePlayer> slotToMember = new HashMap<>();

        List<Map.Entry<OfflinePlayer, Integer>> entryList = bande.members().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());

        Collections.reverse(entryList);

        for(Map.Entry<OfflinePlayer, Integer> entry : entryList) {
            OfflinePlayer offlinePlayer = entry.getKey();
            if(i == 34) continue;
            i++;
            if(i == 16 || i == 25) i += 2;
            String title = ChatColor.AQUA + offlinePlayer.getName();
            if(offlinePlayer.equals(bande.owner())) {
                title = ChatColor.RED + "[Bande Leder] " + ChatColor.AQUA + bande.owner().getName();
            }

            ItemStack memberSkull = ItemUtils.getSkull(offlinePlayer);

            ItemUtils.setNameAndLore(memberSkull, title, "&fRank: " + bande.getMemberRankString(offlinePlayer), "&fSaldo: " + economy.getBalance(offlinePlayer));

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                if(offlinePlayer.equals(player)) {
                    ItemUtils.addLore(memberSkull, " ", "&cDu kan ikke \u00E6ndre p\u00E5 dig selv!");
                } else {
                    ItemUtils.addLore(memberSkull, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT FORFREMME &8&l \u3015", "&8&l\u3014 &f&lH\u00d8JRE KLIK FOR AT NEDGRADERE &8&l \u3015", "&8&l\u3014 &f&lDROP FOR AT SMIDE UD &8&l \u3015");
                    if(bande.owner().equals(player)) {
                        ItemUtils.addLore(memberSkull, "&8&l\u3014 &f&lTRYK P\u00C5 MUSEHJULET FOR AT OVERF\u00d8RE EJERSKAB&8&l \u3015");
                    }
                }
            }

            inventory.setItem(i, memberSkull);
            slotToMember.put(i, offlinePlayer);
        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event.getSlot() == 36) {
                openMainInventory(player);
                return;
            }
            OfflinePlayer victim = slotToMember.get(event.getSlot());
            if(victim.equals(player)) return;
            if(!slotToMember.get(event.getSlot()).equals(bande.owner())) {
                bande.members().entrySet().stream().filter((set) -> {
                    if(set.getKey().equals(player)) {
                        return true;
                    }
                    return false;
                }).findAny().ifPresent((set) -> {
                    if(victim.equals(bande.owner())) return;
                    int rank = set.getValue();

                    if(rank >= Bande.PermissionLevel.RIGHTHANDMAN) {
                        int currentRank = bande.getMemberRank(victim);
                        switch (event.getAction()) {
                            case PICKUP_ALL:
                                if(currentRank >= Bande.PermissionLevel.RIGHTHANDMAN) return;
                                bande.setMemberRank(victim, currentRank + 1);
                                break;
                            case PICKUP_HALF:
                                if(currentRank <= Bande.PermissionLevel.ROOKIE) return;
                                bande.setMemberRank(victim, bande.getMemberRank(victim) - 1);
                                break;
                            case DROP_ONE_SLOT:
                                bande.kickMember(victim, player);
                                break;
                            case NOTHING:
                                player.closeInventory();
                                if(bande.owner().equals(player)) {
                                    showConfirmation(player).thenAccept(result -> {
                                        if(result) {
                                            bande.transferOwner(victim);
                                        } else {
                                            if(player.isOnline()) {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu forblev ejer!"));
                                            }
                                        }
                                    });
                                }
                                break;
                        }
                        showMemberManager(player);
                    }
                });
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));



    }

    public CompletableFuture<Float> promptForMoney(OfflinePlayer player, PromptType type) {
        CompletableFuture<Float> future = new CompletableFuture<>();

        Bande bande = Bande.getBande(player);

        if(bande == null) return future;

        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Skriv et tal");
        builder.preventClose();
        builder.onClick((slot, stateSnapshot) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

            try {
                float amount = Float.parseFloat(stateSnapshot.getText());
                if(amount <= 0) {
                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ugyldig m\u00E6ngde"));
                }
                if(type == PromptType.DEPOSIT) {
                    if(economy.getBalance(player) >= amount) {
                        return Arrays.asList(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run(() -> future.complete(amount)));
                    }
                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ikke nok penge!"));
                }
                if(type == PromptType.WITHDRAW) {
                    if(bande.getBank() >= amount) {
                        return Arrays.asList(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run(() -> future.complete(amount)));
                    }
                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ikke nok penge!"));
                }

            }catch (Exception e) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ikke et tal!"));
            }
            return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Noget gik galt!"));
        });

        if(player.isOnline()) {
            builder.open(player.getPlayer());
        }

        return future;
    }

    public CompletableFuture<Boolean> showConfirmation(OfflinePlayer player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Sikker? Ja/Nej");
        builder.preventClose();
        builder.onClick((slot, stateSnapshot) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

            if(stateSnapshot.getText().equalsIgnoreCase("ja") || stateSnapshot.getText().equalsIgnoreCase("yes")) {
                return Arrays.asList(
                        AnvilGUI.ResponseAction.close(),
                        AnvilGUI.ResponseAction.run(() -> future.complete(true))
                );
            }
            else {
                return Arrays.asList(
                        AnvilGUI.ResponseAction.close(),
                        AnvilGUI.ResponseAction.run(() -> future.complete(false))
                );
            }
        });
        if(player.isOnline()) {
            builder.open(player.getPlayer());
        }
        return future;
    }

    public void showRelationsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" RELATIONER");
        InventoryUtil.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));
        inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI0OWNlN2QyODI4NWY2NjlkNDQ5ZTIzOGQwMDkxMmU2OTQzNGYwZDc1OWVhZThlODI3ODkxZWNkYWEwZjMxNCJ9fX0="), "&c&lRivaler", "&fKlik her for at se dine rivaler", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
        inventory.setItem(21, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhlYjczZGFmOGY2YTZjZDU2NjliMjgzMmEyNDZkYmY3ZTNhMTMwN2JjYzE5YThjOTZlZjc3NGQ3NmM1NmJhOSJ9fX0="), "&c&lAllierede", "&fKlik her for at se dine allierede", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    break;
                case 21: //Allierede
                    showAllies(player);
                    break;
                case 23:
                    showRivals(player);
                    break;
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

    }

    public void showAllies(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" ALLIEREDE");
        InventoryUtil.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));

        if(bande.getAllies().isEmpty()) {
            inventory.setItem(10, ItemUtils.setNameAndLore(Material.BARRIER, "&c&lIngen allies!", "&fDu har ikke nogen allies endnu!"));
        }

        int i = 9;
        HashMap<Integer, Bande> slotToAlly = new HashMap<>();
        for(Bande ally : bande.getAllies()) {
            if(i == 34) continue;
            i++;
            if(i == 16 || i == 25) i += 2;

            slotToAlly.put(i, ally);

            ItemStack skullToDisplay = ally.getDisplaySkull();

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                ItemUtils.addLore(skullToDisplay, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT FJERNE&8&l \u3015");
            }



            inventory.setItem(i, skullToDisplay);

        }


        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            if (event.getSlot() == 36) {
                showRelationsMenu(player);
                return;
            }
            if(event.getAction() != InventoryAction.PICKUP_ALL) return;

            if(bande.getMemberRank(player) < Bande.PermissionLevel.RIGHTHANDMAN) return;

            Bande targetAlly = slotToAlly.get(event.getSlot());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har fjernet " + targetAlly.getName() + " fra dine allies!"));

            for(OfflinePlayer member : bande.members().keySet()) {
                if(member.isOnline()) {
                    member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har fjernet " + targetAlly.getName() + " fra jeres allies!"));
                }

            }
            for(OfflinePlayer ally : targetAlly.members().keySet()) {
                if(ally.isOnline()) {
                    ally.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin allierede " + bande.getName() + " opsagde jeres alliance"));
                }
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void showRivals(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" RIVALER");
        InventoryUtil.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));

        if(bande.getRivals().isEmpty()) {
            inventory.setItem(10, ItemUtils.setNameAndLore(Material.BARRIER, "&c&lIngen allies!", "&fDu har ikke nogen allies endnu!"));
        }

        int i = 9;
        HashMap<Integer, Bande> slotToRival = new HashMap<>();
        for(Bande rival : bande.getRivals()) {
            if(i == 34) continue;
            i++;
            if(i == 16 || i == 25) i += 2;

            slotToRival.put(i, rival);

            ItemStack skullToDisplay = rival.getDisplaySkull();

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                ItemUtils.addLore(skullToDisplay, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT FJERNE&8&l \u3015");
            }



            inventory.setItem(i, skullToDisplay);

        }


        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            if (event.getSlot() == 36) {
                showRelationsMenu(player);
                return;
            }
            if(event.getAction() != InventoryAction.PICKUP_ALL) return;

            if(bande.getMemberRank(player) < Bande.PermissionLevel.RIGHTHANDMAN) return;

            Bande targetAlly = slotToRival.get(event.getSlot());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har fjernet " + targetAlly.getName() + " fra dine rivaler!"));

            for(OfflinePlayer member : bande.members().keySet()) {
                if(member.isOnline()) {
                    member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har fjernet " + targetAlly.getName() + " fra jeres rivaler!"));
                }

            }
            for(OfflinePlayer rival : targetAlly.members().keySet()) {
                if(rival.isOnline()) {
                    rival.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin rival " + bande.getName() + " fjernede dig som rival"));
                }
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void showInviteMenu(Player player) {
        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Spiller navn");
        builder.onClick((slot, stateSnapshot) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            Optional<OfflinePlayer> playerExists = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> offlinePlayer.getName().equalsIgnoreCase(stateSnapshot.getText())).findAny();

            if(!playerExists.isPresent()) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Spiller ikke fundet!"));
            }

            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> bande.invite(player, playerExists.get()))
            );
        });

        builder.open(player);
    }

    public void setBandeNameGUI(Player player) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Indtast navn");
        builder.onClick((slot, stateSnapshot) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            boolean isBannedName = Arrays.stream(BandePlugin.bannedNames).anyMatch(string -> string.toLowerCase().contains(stateSnapshot.getText().toLowerCase()));

            if(isBannedName) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Banned navn"));
            }

            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> createNamedBande(player, stateSnapshot.getText()))
            );
        });

        builder.open(player);
    }

    public void createNamedBande(Player player, String name) {
        economy.withdrawPlayer(player, 500);

        BandePlugin.instance.bander.add(new BandeBuilder(player).setName(name).create());
        player.sendMessage(ChatColor.GREEN + "Du lavede banden: " + name + "!");
        BandePlugin.manager.saveData(BandePlugin.instance.bander);
    }
    public void openInvitationsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" INVITATIONER");
        InventoryUtil.createBorders(inventory);

        List<Bande> currentInvites = new ArrayList<>();
        for(Map.Entry<OfflinePlayer, Bande> entry : BandePlugin.invites.entrySet()) {
            if(entry.getKey().equals(player)) {
                currentInvites.add(entry.getValue());
            }
        }

        int i = 9;
        HashMap<Integer, Bande> slotToBande = new HashMap<>();
        for(OfflinePlayer offlinePlayer : BandePlugin.invites.keySet()) {
            if(i == 34) continue;
            i++;
            if(i == 16 || i == 25) i += 2;
            if(offlinePlayer.equals(player)) {
                Bande bande = BandePlugin.invites.get(offlinePlayer);
                if(bande == null) continue;
                slotToBande.put(i, bande);

                ItemStack skullToDisplay = bande.getDisplaySkull();

                ItemUtils.addLore(skullToDisplay, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT JOINE&8&l \u3015", "&8&l\u3014 &f&lH\u00d8JRE KLIK FOR AT AFVISE&8&l \u3015");

                inventory.setItem(i, skullToDisplay);
            }
        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            Bande clickedBande = slotToBande.get(event.getSlot());

            if(clickedBande == null) return;

            switch(event.getAction()) {
                case PICKUP_ALL: // LEFT CLICK
                    player.closeInventory();
                    if(!clickedBande.addMember(player, Bande.PermissionLevel.ROOKIE)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBanden er fuld!"));
                    } else {
                        BandePlugin.invites.remove(player);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu sluttede dig til banden &c"+clickedBande.getName()+"&f!"));
                        for(OfflinePlayer member : clickedBande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &c"+player.getName()+" &fhar sluttet sig til banden!"));
                            }
                        }
                    }
                    break;
                case PICKUP_HALF: // RIGHT CLICK
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har afvist invitationen til &c"+clickedBande.getName()+"&f!"));
                    BandePlugin.invites.remove(player);
                    for(OfflinePlayer member : clickedBande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &c"+player.getName()+" &fhar afvist bandens invitation!"));
                        }
                    }
                    break;
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

        new BukkitRunnable() {
            @Override
            public void run() {
                if(Bande.getBande(player) != null) {
                    cancel();
                    return;
                }
                List<Bande> updatedInvites = new ArrayList<>();
                for(Map.Entry<OfflinePlayer, Bande> entry : BandePlugin.invites.entrySet()) {
                    if(entry.getKey().equals(player)) {
                        updatedInvites.add(entry.getValue());
                    }
                }
                if(updatedInvites.size() != currentInvites.size()) {
                    openInvitationsInventory(player);
                }
            }
        }.runTaskTimer(BandePlugin.instance, 10, 10);


    }
}

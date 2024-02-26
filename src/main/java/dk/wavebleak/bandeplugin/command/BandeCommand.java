package dk.wavebleak.bandeplugin.command;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.BandeBuilder;
import dk.wavebleak.bandeplugin.classes.InventoryData;
import dk.wavebleak.bandeplugin.classes.InventoryManager;
import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.utils.InventoryUtil;
import dk.wavebleak.bandeplugin.utils.ItemsUtil;
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

            inventory.setItem(20, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg3MDMxYzQ3MjZkZGVkZDY1YjZhMTFkMzE0N2U2NzI0ZGVmYmIyOTBkYTI5Y2JiNzlkYTI0OTA1NDZjYmYifX19"), "&c&lTOP LEVEL", "&fplease make a leaderboard", "&ffor top level","&fpookie wookie bear :3"));
            inventory.setItem(22, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0="), "&4&lOpret en bande", "&8\u2B24 &fDet koster &c$5000 &fat oprette en bande!"));
            inventory.setItem(24, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMzA0OGY1M2IyNGZiMzZlYmFjNjRkODU4Mzg5MTM1ODgzZjI1ODc0ZTQ1NDZkMWZjZDg5YzMwYmQ2ZjY1NiJ9fX0="), "&c&lDINE INVITATIONER", "&8\u2B24 &fKlik her for at se dine invitationer!"));

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
                inventory.setItem(13, ItemsUtil.setNameAndLore(ItemsUtil.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
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
                inventory.setItem(13, ItemsUtil.setNameAndLore(ItemsUtil.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
            }

            inventory.setItem(19, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19"), "&c&lBANK", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande \u00f8konomi", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
            inventory.setItem(21, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzZmM5YTQ1YmUxM2NhNTdhNzhiMjE3NjJjNmUxMjYyZGFlNDExZjEzMDQ4Yjk2M2Q5NzJhMjllMDcwOTZhYiJ9fX0="), "&c&lOPGRADERINGER", " ", "&f&lHer kan du k\u00f8be:", "&8\u2B24 &fAdgang til territorier" +  " ", "&8\u2B24 &fAdgang til bande hus", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            /* Fix for medlemmer icon showing up late */
            inventory.setItem(23, ItemsUtil.setNameAndLore(ItemsUtil.getSkull(bande.owner()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            inventory.setItem(25, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVjOTZjZjNlNWM2OTgwYzcxNGYyNzkxN2I2NDM5YjA1OTY1MmY0Y2MyMTRhZGQ3MGRjNzQwYzZjMWZlNzBmMSJ9fX0="), "&c&lRELATIONER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fAllierede", "&8\u2B24 &fRivaler", "", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));

            //TODO: Bedre invite button
            List<String> inviteLore;
            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                inviteLore = Arrays.asList(" ", "&f&lHer kan du invitere medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015");
            }else {
                inviteLore = Arrays.asList(" ", "&c&lDu har ikke adgang", "&c&lTil at invitere medlemmer!");
            }

            inventory.setItem(31, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFjYmJjYTU2NzM3MmE5YjJiMzZjOGY2ODE1NDg1MWJkYTVlZTFkNTNlMmJjMjA4YTExNTJkOWExOGQyY2IifX19"), "&c&lInvit\u00E9r Spillere", inviteLore.toArray(new String[0])));

            //TODO: Bedre forlad button
            String leaveName;
            List<String> leaveLore;
            if(bande.owner().equals(player)) {
                leaveName = "&c&lOPL\u00d8S BANDE";
                leaveLore = Arrays.asList(" ", "&c&lOpl\u00f8s bande", " ", "&8&l\u3014 &f&lSHIFT-TRYK HER FOR AT OPL\u00d8SE BANDEN&8&l \u3015");
            } else {
                leaveName = "&c&lFORLAD BANDE";
                leaveLore = Arrays.asList(" ", "&8&l\u3014 &f&lSHIFT-TRYK HER FOR AT FORLADE BANDEN&8&l \u3015");
            }

            inventory.setItem(40, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="), leaveName, leaveLore.toArray(new String[0])));

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
                                    if(player.isOnline()) player.sendMessage(ChatColor.RED + "Du skiftede mening"); //TODO: Add prefix osv
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

                    inventory.setItem(23, ItemsUtil.setNameAndLore(ItemsUtil.getSkull(entry.getKey()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8\u2B24 &fBande medlemmer", " ", "&8&l\u3014 &f&lTRYK HER &8&l \u3015"));
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
        inventory.setItem(36, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
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
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));


        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
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
        inventory.setItem(36, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));

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

            ItemStack memberSkull = ItemsUtil.getSkull(offlinePlayer);

            ItemsUtil.setNameAndLore(memberSkull, title, "&fBalance: " + economy.getBalance(offlinePlayer), "&fRank: " + bande.getMemberRankString(offlinePlayer)); //TODO: Mere information

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                if(offlinePlayer.equals(player)) {
                    ItemsUtil.addLore(memberSkull, " ", "&cDu kan ikke manage dig selv!"); //TODO: Bedre lore
                } else {
                    ItemsUtil.addLore(memberSkull, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT FORFREMME &8&l \u3015", "&8&l\u3014 &f&lH\u00d8JRE KLIK FOR AT DEGRADERE &8&l \u3015", "&8&l\u3014 &f&lDROP FOR AT SMIDE UD &8&l \u3015"); //TODO: Bedre lore
                    if(bande.owner().equals(player)) {
                        ItemsUtil.addLore(memberSkull, "&8&l\u3014 &f&lMIDDLE CLICK FOR AT TRANSFER OWNERSHIP &8&l \u3015");
                    }
                }
            }

            inventory.setItem(i, memberSkull);
            slotToMember.put(i, offlinePlayer);
        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            Bukkit.broadcastMessage(event.getAction().name());
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
                                                player.sendMessage(ChatColor.GREEN + "Du forblev bande ejer"); //TODO: Bro idfk hvad jeg skal skrive her
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

    public CompletableFuture<Boolean> showConfirmation(OfflinePlayer player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Er du sikker?"); //TODO: Måske add farve eller noget, idk (Husk beskeden kan ikke være så lang)
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
        inventory.setItem(36, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&cKlik her", "&cFor at komme tilbage til hovedmenuen"));


        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    break;
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

                ItemsUtil.addLore(skullToDisplay, " ", "&8&l\u3014 &f&lVENSTRE KLIK FOR AT JOINE&8&l \u3015", "&8&l\u3014 &f&lH\u00d8JRE KLIK FOR AT AFVISE&8&l \u3015");

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
                        player.sendMessage(ChatColor.RED + "Denne bande er fuld!"); //TODO: Bedre beskeder
                    } else {
                        BandePlugin.invites.remove(player);
                        player.sendMessage(ChatColor.GREEN + "Du joinede banden: " + clickedBande.getName());//TODO: Bedre beskeder
                        for(OfflinePlayer member : clickedBande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().sendMessage(ChatColor.GREEN + player.getName() + " joinede banden!");//TODO: Bedre beskeder
                            }
                        }
                    }
                    break;
                case PICKUP_HALF: // RIGHT CLICK
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Du har afvist invitationen!");//TODO: Bedre beskeder
                    BandePlugin.invites.remove(player);
                    for(OfflinePlayer member : clickedBande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().sendMessage(ChatColor.RED + player.getName() + " afviste jeres invitation!");//TODO: Bedre beskeder
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

package dk.wavebleak.bandeplugin.command;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.*;
import dk.wavebleak.bandeplugin.utils.InventoryUtils;
import dk.wavebleak.bandeplugin.utils.ItemUtils;
import dk.wavebleak.bandeplugin.utils.PlayerUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dk.wavebleak.bandeplugin.BandePlugin.economy;
import static dk.wavebleak.bandeplugin.BandePlugin.inventoryManager;

@SuppressWarnings("all")
public class BandeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length == 1 && args[0].equalsIgnoreCase("load") && PlayerUtils.IsOP(player)) {
            BandePlugin.instance.load();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBande pluginnet er nu loaded fra databasen!"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("save") && PlayerUtils.IsOP(player)) {
            BandePlugin.instance.save();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBande pluginnet er nu gemt til databasen!"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("admin") && PlayerUtils.IsOP(player)) {
            showAdminMenu(player);
            return true;
        }
        if(args.length >= 1 && args[0].equalsIgnoreCase("invite")) {
            handleInvite(player, args);
            return true;
        }
        if(args.length >= 1 && args[0].equalsIgnoreCase("addterritory") && PlayerUtils.IsOP(player)) {
            handleTerritory(player, args);
            return true;
        }

        openMainInventory(player);
        return true;
    }

    public void handleTerritory(Player player, String[] args) {
        if(args.length < 5) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBrug /bande addterritory <navn> <minBrød> <maxBrød> <breadInterval>"));
            return;
        }
        String name;
        int minBrød;
        int maxBrød;
        int breadInterval;
        try {
            name = args[1];
            minBrød = Integer.parseInt(args[2]);
            maxBrød = Integer.parseInt(args[3]);
            breadInterval = Integer.parseInt(args[4]);
        }catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu skrev ikke heltal!"));
            return;
        }

        Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 5);

        if(targetBlock == null || !(targetBlock.getType() == Material.STANDING_BANNER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu kikker ikke på et banner!"));
            return;
        }

        if (BandePlugin.instance.territories.stream().anyMatch(x -> x.getName().equals(name))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDer findes allerade et territorie med det navn!"));
            return;
        }

        BandeTerritorie territorie = new BandeTerritorie(name, targetBlock.getLocation(), maxBrød, minBrød, breadInterval);

        territorie.spawn();

        BandePlugin.instance.territories.add(territorie);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har lavet et nyt territorie!"));
    }

    public void handleInvite(Player player, String[] args) {
        Bande bande = Bande.getBande(player);

        if(bande == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har ikke en bande!"));
            return;
        }

        if(args.length < 2) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBrug &n/bande invite <spiller>"));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        bande.invite(player, target);

    }


    public void showAdminPage(Inventory inventory, int page, HashMap<Integer, Bande> slotToBande) {
        slotToBande.clear();
        int slot = 9;
        for(int i = 0 + (34 * (page - 1)); i < 34 * page; i++) {
            if(slot == 34) continue;
            slot++;
            if(slot == 17 || slot == 26) slot += 2;
            if(BandePlugin.instance.bander.size() < i + 1) {
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            }
            Bande bande = BandePlugin.instance.bander.get(i);
            slotToBande.put(slot, bande);

            ItemStack skullToDisplay = bande.getDisplaySkull();

            ItemUtils.addLore(skullToDisplay, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT SE MEMBERS &8&l〕", "&8&l〔 &f&lHØJRE KLIK FOR AT TILFØJE MEDLEM&8&l〕", "&8&l〔 &f&lDROP STACK FOR AT SLETTE &8&l〕");

            inventory.setItem(slot, skullToDisplay);
        }

    }

    public void showAdminMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.RED+" ADMIN");
        InventoryUtils.createBorders(inventory);
        inventory.setItem(35, ItemUtils.setNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14), "&cNæste side"));
        inventory.setItem(27, ItemUtils.setNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14), "&cTilbage"));

        int i = 9;
        HashMap<Integer, Bande> slotToBande = new HashMap<>();

        showAdminPage(inventory, 1, slotToBande);

        player.openInventory(inventory);

        AtomicInteger page = new AtomicInteger(1);
        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            if(event.getSlot() == 35) {
                page.incrementAndGet();
                showAdminPage(inventory, page.get(), slotToBande);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
            if(event.getSlot() == 27) {
                if(page.get() == 1) return;
                page.decrementAndGet();
                showAdminPage(inventory, page.get(), slotToBande);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                return;
            }
            Bande selectedBande = slotToBande.get(event.getSlot());

            if(selectedBande == null) return;

            if(event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                //Add member
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                promptForPlayer(player, selectedBande).thenAccept(selectedPlayer -> {
                    selectedBande.addMember(selectedPlayer, 1);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
                });

            }

            if(event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                //Show memebers
                showAdminMemberList(player, selectedBande);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                return;
            }
            if(event.getAction().equals(InventoryAction.DROP_ALL_SLOT)) {
                //Delete bande
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                showConfirmation(player).thenAccept(result -> {
                    if(result) {
                        selectedBande.disband();
                        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 1);
                        return;
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu fortrød at disband banden " + selectedBande.getName()));
                    player.playSound(player.getLocation(), Sound.SPLASH, 1, 1);
                });
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

    }


    public void showAdminMemberList(Player player, Bande bande) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.RED+" ADMIN");
        InventoryUtils.createBorders(inventory);

        List<Map.Entry<OfflinePlayer, Integer>> initialNap = bande.members().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());
        inventory.setItem(36, getBackButton());

        Collections.reverse(initialNap);

        int i = 9;
        HashMap<Integer, OfflinePlayer> slotToMember = new HashMap<>();
        for(Map.Entry<OfflinePlayer, Integer> entry : initialNap) {
            OfflinePlayer offlinePlayer = entry.getKey();
            if(i == 34) continue;
            i++;
            if(i == 17 || i == 26) i += 2;
            String title = ChatColor.AQUA + offlinePlayer.getName();
            if(offlinePlayer.equals(bande.owner())) {
                title = ChatColor.RED + "[Bande Leder] " + ChatColor.AQUA + bande.owner().getName();
            }

            ItemStack memberSkull = ItemUtils.getSkull(offlinePlayer);

            ItemUtils.setNameAndLore(memberSkull, title, "&fRank: " + bande.getMemberRankString(offlinePlayer), "&fSaldo: " + economy.getBalance(offlinePlayer));

            slotToMember.put(i, offlinePlayer);

            if(!bande.owner().equals(offlinePlayer)) {
                ItemUtils.addLore(memberSkull, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT FORFREMME &8&l 〕", "&8&l〔 &f&lHØJRE KLIK FOR AT NEDGRADERE &8&l 〕", "&8&l〔 &f&lDROP FOR AT SMIDE UD &8&l 〕");
                ItemUtils.addLore(memberSkull, "&8&l〔 &f&lTRYK PÅ MUSEHJULET FOR AT OVERFØRE EJERSKAB&8&l 〕");
            }

            inventory.setItem(i, memberSkull);
        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            if(event.getSlot() == 36) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                showAdminMenu(player);
                return;
            }
            OfflinePlayer victim = slotToMember.get(event.getSlot());

            if(victim == null) return;

            if(!slotToMember.get(event.getSlot()).equals(bande.owner())) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
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
                        showConfirmation(player).thenAccept(result -> {
                            if(result) {
                                bande.transferOwner(victim);
                            } else {
                                if(player.isOnline()) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + bande.owner().getName() + " forblev ejer!"));
                                }
                            }
                        });
                        break;
                }
                showAdminMemberList(player, bande);
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void openMainInventory(Player player) {
        Bande bande = Bande.getBande(player);

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" FORSIDE");
        InventoryUtils.createBorders(inventory);

        if(bande == null) { // Ingen bande
            inventory.setItem(20, generateLeaderboard());
            inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0="), "&4&lOpret en bande", "&8⬤ &fDet koster &c$500 &fat oprette en bande!"));
            inventory.setItem(24, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMzA0OGY1M2IyNGZiMzZlYmFjNjRkODU4Mzg5MTM1ODgzZjI1ODc0ZTQ1NDZkMWZjZDg5YzMwYmQ2ZjY1NiJ9fX0="), "&c&lDINE INVITATIONER", "&8⬤ &fKlik her for at se dine invitationer!"));

        } else { // Har en bande
            inventory.setItem(4, bande.getDisplaySkull());

            String levelupHead;
            String levelupColor;
            if(bande.canLevelUp()) {
                levelupHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRhMDI3NDc3MTk3YzZmZDdhZDMzMDE0NTQ2ZGUzOTJiNGE1MWM2MzRlYTY4YzhiN2JjYzAxMzFjODNlM2YifX19";
                levelupColor = "&a";
                ArrayList<String> lore = new ArrayList<>();
                lore.add("&8"+bande.getLevel()+" &8&l» &7"+(bande.getLevel()+1));
                lore.add(" ");
                lore.add("&f&lKRAV:");
                lore.addAll(Arrays.asList(bande.genererateLines()));
                lore.add(" ");
                lore.add("&8&l〔 &f&lKLIK HER &8&l〕");
                inventory.setItem(13, ItemUtils.setNameAndLore(ItemUtils.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
            }else{
                levelupHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ5Mjg3NjE2MzQzZDgzM2U5ZTczMTcxNTljYWEyY2IzZTU5NzQ1MTEzOTYyYzEzNzkwNTJjZTQ3ODg4NGZhIn19fQ==";
                levelupColor = "&c";
                ArrayList<String> lore = new ArrayList<>();
                lore.add("&8"+bande.getLevel()+" &8&l» &7"+(bande.getLevel()+1));
                lore.add(" ");
                lore.add("&f&lKRAV:");
                lore.addAll(Arrays.asList(bande.genererateLines()));
                lore.add(" ");
                lore.add(" ");
                lore.add("&8&l〔 &c&lI HAR IKKE OPNÅET ALLE KRAV &8&l〕");
                inventory.setItem(13, ItemUtils.setNameAndLore(ItemUtils.getSkull(levelupHead), levelupColor + "&lLEVELUP", lore.toArray(new String[0])));
            }

            inventory.setItem(8, generateLeaderboard());
            inventory.setItem(19, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFkZGIyZmQ3NzIwMDcxNjk0ZTllODBhNmY0YThiOGFiYTc3NjBiYjFkYTQ2OGRlNjM3YTZiZjljODVlYTVhZSJ9fX0="), "&c&lBANK", " ", "&f&lHer kan du administrere:", "&8⬤ &fBande økonomi", "", "&8&l〔 &f&lTRYK HER &8&l 〕"));
            inventory.setItem(21, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzZmM5YTQ1YmUxM2NhNTdhNzhiMjE3NjJjNmUxMjYyZGFlNDExZjEzMDQ4Yjk2M2Q5NzJhMjllMDcwOTZhYiJ9fX0="), "&c&lOPGRADERINGER", " ", "&f&lHer kan du købe:", "&8⬤ &fAdgang til territorier", "&8⬤ &fMindre ally damage", "&8⬤ &fAdgang til bande hus", "", "&8&l〔 &f&lTRYK HER &8&l 〕"));

            /* Fix for medlemmer icon showing up late */
            inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull(bande.owner()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8⬤ &fBande medlemmer", " ", "&8&l〔 &f&lTRYK HER &8&l 〕"));

            inventory.setItem(25, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVjOTZjZjNlNWM2OTgwYzcxNGYyNzkxN2I2NDM5YjA1OTY1MmY0Y2MyMTRhZGQ3MGRjNzQwYzZjMWZlNzBmMSJ9fX0="), "&c&lRELATIONER", " ", "&f&lHer kan du administrere:", "&8⬤ &fAllierede", "&8⬤ &fRivaler", "", "&8&l〔 &f&lTRYK HER &8&l 〕"));

            List<String> inviteLore;
            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                inviteLore = Arrays.asList(" ", "&f&lHer kan du:", "&8⬤ &fInvitere medlemmer", " ", "&8&l〔 &f&lTRYK HER &8&l 〕");
            }else {
                inviteLore = Arrays.asList(" ", "&c&lDu har ikke adgang!");
            }

            inventory.setItem(31, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhM2Y2ZjlmNTBhYzY3ZGI5OGI2OGExNWZkMDU5MDE1Mjg4MzZhMThjNzBmYjM5MmFhODlmNGI2MDgzNzY2YiJ9fX0="), "&c&lInvitér Spillere", inviteLore.toArray(new String[0])));

            String leaveName;
            List<String> leaveLore;
            if(bande.owner().equals(player)) {
                leaveName = "&c&lOPLØS BANDE";
                leaveLore = Arrays.asList(" ", "&fOpløs bande", " ", "&8&l〔 &f&lSHIFT + TRYK HER &8&l〕");
            } else {
                leaveName = "&c&lFORLAD BANDE";
                leaveLore = Arrays.asList(" ", "&fForlad bande", " ", "&8&l〔 &f&lSHIFT + TRYK HER &8&l〕");
            }

            inventory.setItem(40, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="), leaveName, leaveLore.toArray(new String[0])));

        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            if(bande == null) { // Ingen bande menu
                switch (event.getSlot()) {
                    case 24: // Invitationer
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        openInvitationsInventory(player);
                        break;
                    case 22: // Opret bande
                        if(economy.getBalance(player) >= 500) {
                            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            setBandeNameGUI(player);
                        } else {
                            player.closeInventory();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har ikke nok penge til dette!"));
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
                        }
                        break;
                }
            } else { // Bande menu
                switch (event.getSlot()) {
                    case 13:
                        if(bande.canLevelUp()) {
                            bande.levelUp(true, false);
                            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            openMainInventory(player);
                        };
                        break;
                    case 19:
                        showBankMenu(player);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        break;
                    case 21:
                        showUpgradesMenu(player);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        break;
                    case 23:
                        showMemberManager(player);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        break;
                    case 25:
                        showRelationsMenu(player);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                        break;
                    case 31:
                        if(!(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN)) return;
                        showInviteMenu(player);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    case 40:
                        if(!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) return;
                        if(bande.owner().equals(player)) {
                            showConfirmation(player).thenAccept(result -> {
                                if(result) {
                                    bande.disband();
                                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1 ,1);
                                } else {
                                    if(player.isOnline()) {
                                        player.playSound(player.getLocation(), Sound.SPLASH, 1, 1);
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu skiftede mening! Banden er stadig intakt!"));
                                    }
                                }
                            });
                            break;
                        }
                        bande.leave(player);
                        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1 ,1);
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

                    inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull(entry.getKey()), "&c&lMEDLEMMER", " ", "&f&lHer kan du administrere:", "&8⬤ &fBande medlemmer", " ", "&8&l〔 &f&lTRYK HER &8&l 〕"));
                }
            }.runTaskTimer(BandePlugin.instance, 0, 20);
        }
    }

    public ItemStack generateLeaderboard() {
        List<Bande> sortedBande = BandePlugin.instance.bander.stream().sorted(Comparator.comparingDouble(x-> x.getLevel())).collect(Collectors.toList());
        Collections.reverse(sortedBande);
        List<String> lore = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            Bande currentBande = null;
            if(sortedBande.size() > i - 1) {
                currentBande = sortedBande.get(i - 1);
            }
            String name = "----------";
            ChatColor color;
            switch (i) {
                case 1:
                    color = ChatColor.YELLOW;
                    break;
                case 2:
                    color = ChatColor.GRAY;
                    break;
                case 3:
                    color = ChatColor.GOLD;
                    break;
                default:
                    color = ChatColor.DARK_GRAY;
            }
            if(currentBande != null) name = currentBande.getName() + " &8(" + color + currentBande.getLevel() + "&8)";

            lore.add(color.toString() + i + ". " + name);
        }

        return ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg3MDMxYzQ3MjZkZGVkZDY1YjZhMTFkMzE0N2U2NzI0ZGVmYmIyOTBkYTI5Y2JiNzlkYTI0OTA1NDZjYmYifX19"), "&c&lTOP LEVEL", lore.toArray(new String[0]));
    }

    public ItemStack getBackButton() {
        return ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1MWI1OTA4NWQyYzQyNDk1Nzc4MjNmNjNlMWUyZWI5ZjdjZjY0YjdjNzg3ODVhMjE4MDVmYWQzZWYxNCJ9fX0="), "&c&lTilbage", "&fKlik her", "&fFor at komme tilbage til hovedmenuen", "", "&8&l〔 &f&lTRYK HER &8&l 〕");
    }

    public void showBankMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" BANK");
        InventoryUtils.createBorders(inventory);

        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, getBackButton());
        inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFkZGIyZmQ3NzIwMDcxNjk0ZTllODBhNmY0YThiOGFiYTc3NjBiYjFkYTQ2OGRlNjM3YTZiZjljODVlYTVhZSJ9fX0="), "&c&lBanden's Bank", "&fSaldo: &f$" + bande.getBank()));
        inventory.setItem(20, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmYyZTE2ZWNiNWEwZmU3NTk3NDg5NTY5YjAwZmFjOTFjYmE2YWViOGQ0MTc5ZmI0ZWFkMWY3YzEzM2FiNjcwOSJ9fX0="), "&c&lDeposit", "&fPut penge ind i banken", "", "&8&l〔 &f&lTRYK HER &8&l 〕" /* TRYK HER LINE */));
        inventory.setItem(24, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZkMTA4MzgzZGZhNWIwMmU4NjYzNTYwOTU0MTUyMGU0ZTE1ODk1MmQ2OGMxYzhmOGYyMDBlYzdlODg2NDJkIn19fQ=="), "&c&lWithdraw", "&fTag penge ud af banken", "", "&8&l〔 &f&lTRYK HER &8&l 〕" /* TRYK HER LINE */));

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            switch (event.getSlot()) {
                case 36:
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                    openMainInventory(player);
                    break;
                case 20: //Deposit
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                    promptForMoney(player, PromptType.DEPOSIT).thenAccept(amount -> {
                        economy.withdrawPlayer(player, amount);
                        bande.setBank(Math.round(bande.getBank() + amount));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                        showBankMenu(player);
                    });
                    break;
                case 24: //Withdraw
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
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

    public long getValue(int input) {
        double price = (((-8000.0 / 100.0) * input) + 10000);
        return Math.round(price);
    }

    public void showUpgradesMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" OPGRADERINGER");
        InventoryUtils.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        final int territoriePris = 30000;
        final int husPris = 50000;
        final long allyDamagePris = getValue(bande.getAllyHitMitigation());

        boolean enoughMoneyTerritory = bande.getBank() >= territoriePris;
        boolean enoughMoneyHouse = bande.getBank() >= husPris;
        boolean enoughMoneyAlly = bande.getBank() >= allyDamagePris;

        boolean hasUnlockedTerritory = bande.isUnlockedTerritory();
        boolean hasUnlockedHouse = bande.isUnlockedHouse();
        boolean hasUnlockedAlly = bande.getAllyHitMitigation() <= 0;

        ChatColor territorieFarve;
        ChatColor husFarve;
        ChatColor allyDamageFarve;

        if (enoughMoneyTerritory) {
            territorieFarve = ChatColor.GREEN;
        } else {
            territorieFarve = ChatColor.RED;
        }

        if (enoughMoneyHouse) {
            husFarve = ChatColor.GREEN;
        } else {
            husFarve = ChatColor.RED;
        }

        if(enoughMoneyAlly) {
            allyDamageFarve = ChatColor.GREEN;
        } else {
            allyDamageFarve = ChatColor.RED;
        }

        List<String> territorieLore = new ArrayList<>(Arrays.asList("&fMed territorier kan du eje et område", "&fsom generere goder, der kan sælges på det sorte marked", "&fmen andre bander kan angribe jer og stjæle jeres territorie!", ""));

        if(hasUnlockedTerritory) {
            territorieLore.addAll(Arrays.asList("&c&lDin bande ejer allerrade dette", ""));
        } else {
            territorieLore.addAll(Arrays.asList(territorieFarve + "$" + territoriePris, ""));
        }

        List<String> allyDamageLore = new ArrayList<>(Arrays.asList("&fAlly Damage Upgrade giver dig mindre skade", "&fTil både allierede men også dine bande medlemmer", "&fPrisen stiger jo mindre skade du gør!", "", "&fNuværende skade: &b&n" + bande.getAllyHitMitigation() + "&f%", ""));
        
        if(hasUnlockedAlly) {
            allyDamageLore.addAll(Arrays.asList("&c&lDin bande ejer allerrade dette", ""));
        } else {
            allyDamageLore.addAll(Arrays.asList(allyDamageFarve + "$" + allyDamagePris, ""));
        }

        List<String> husLore = new ArrayList<>(Arrays.asList("&fI bande huset får i en masse plads til at lave lige hvad i vil", "&fI kan gro planter eller have en masse storage", ""));
        if(hasUnlockedHouse) {
            husLore.addAll(Arrays.asList("&c&lDin bande ejer allerrade dette", ""));
        } else {
            husLore.addAll(Arrays.asList(husFarve + "$" + husPris, ""));
        }

        if(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER) {
            if(!hasUnlockedHouse) {
                husLore.add("&8&l〔 &f&lTRYK HER &8&l 〕");
            }
            if(!hasUnlockedAlly) {
                allyDamageLore.add("&8&l〔 &f&lTRYK HER &8&l 〕");
            }
            if(!hasUnlockedTerritory) {
                territorieLore.add("&8&l〔 &f&lTRYK HER &8&l 〕");
            }
        } else {
            if(!hasUnlockedHouse) {
                husLore.add("&8&l〔 &f&lDin bande har ikke givet dig tilladelse til dette 〕");
            }
            if(!hasUnlockedAlly) {
                allyDamageLore.add("&8&l〔 &f&lDin bande har ikke givet dig tilladelse til dette 〕");
            }
            if(!hasUnlockedTerritory) {
                territorieLore.add("&8&l〔 &f&lDin bande har ikke givet dig tilladelse til dette 〕");
            }
        }


        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, getBackButton());
        inventory.setItem(20, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYxODQ2MTBjNTBjMmVmYjcyODViYzJkMjBmMzk0MzY0ZTgzNjdiYjMxNDg0MWMyMzhhNmE1MjFhMWVlMTJiZiJ9fX0="), "&c&lTerritorier", territorieLore.toArray(new String[0])));
        inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMzA0OGY1M2IyNGZiMzZlYmFjNjRkODU4Mzg5MTM1ODgzZjI1ODc0ZTQ1NDZkMWZjZDg5YzMwYmQ2ZjY1NiJ9fX0="), "&c&lAlly Damage", allyDamageLore.toArray(new String[0])));
        inventory.setItem(24, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZWEzZTEzYTUxM2RhMTA1MWVhNTlkMTdlZGZiMDNlOGQ4Mzg4ZWRlODg4ODg0NzZkZmI2OTNmMmM4Mzk4In19fQ=="), "&c&lHuse", husLore.toArray(new String[0])));

        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    break;
                case 22:
                    if(!(bande.getBank() >= allyDamagePris)) return;
                    if(!(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER)) return;
                    if(bande.getAllyHitMitigation() <= 0) return;
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1 ,1);
                    bande.setBank(bande.getBank() - allyDamagePris);

                    bande.setAllyHitMitigation(bande.getAllyHitMitigation() - 5);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har købt mindre bande ally skade!"));

                    for(OfflinePlayer member : bande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                            member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har købt mindre bande ally skade!"));
                        }
                    }
                    

                    showUpgradesMenu(player);

                    break;
                case 20: //Territorie
                    if(bande.isUnlockedTerritory()) return;
                    if(!(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER)) return;

                    if(bande.getBank() >= territoriePris) {
                        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1 ,1);
                        bande.setUnlockedTerritory(true);
                        bande.setBank(bande.getBank() - territoriePris);

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har købt adgang til bande territorier!"));

                        for(OfflinePlayer member : bande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har købt adgang til bande territorier!"));
                            }
                        }

                    }
                    showUpgradesMenu(player);

                    break;
                case 24:
                    if(bande.isUnlockedHouse()) return;
                    if(!(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER)) return;

                    if(bande.getBank() >= husPris) {
                        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1 ,1);
                        bande.setUnlockedHouse(true);
                        bande.setBank(bande.getBank() - husPris);

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har købt adgang til bande huse!"));

                        for(OfflinePlayer member : bande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().playSound(player.getLocation(), Sound.NOTE_PLING, 1 ,1.5f);
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har købt adgang til bande huse!"));
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
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" MEDLEMMER");
        InventoryUtils.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, getBackButton());

        int i = 9;
        HashMap<Integer, OfflinePlayer> slotToMember = new HashMap<>();

        List<Map.Entry<OfflinePlayer, Integer>> initialNap = bande.members().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());

        Collections.reverse(initialNap);

        for(Map.Entry<OfflinePlayer, Integer> entry : initialNap) {
            OfflinePlayer offlinePlayer = entry.getKey();
            if(i == 34) continue;
            i++;
            if(i == 17 || i == 26) i += 2;
            String title = ChatColor.AQUA + offlinePlayer.getName();
            if(offlinePlayer.equals(bande.owner())) {
                title = ChatColor.RED + "[Bande Leder] " + ChatColor.AQUA + bande.owner().getName();
            }

            ItemStack memberSkull = ItemUtils.getSkull(offlinePlayer);

            ItemUtils.setNameAndLore(memberSkull, title, "&fRank: " + bande.getMemberRankString(offlinePlayer), "&fSaldo: " + economy.getBalance(offlinePlayer));

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                if(bande.owner().equals(offlinePlayer)) {
                    ItemUtils.addLore(memberSkull, " ", "&cDu kan ikke ændre på lederen!");
                }
                else if(offlinePlayer.equals(player)) {
                    ItemUtils.addLore(memberSkull, " ", "&cDu kan ikke ændre på dig selv!");
                } else {
                    ItemUtils.addLore(memberSkull, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT FORFREMME &8&l 〕", "&8&l〔 &f&lHØJRE KLIK FOR AT NEDGRADERE &8&l 〕", "&8&l〔 &f&lDROP FOR AT SMIDE UD &8&l 〕");
                    if(bande.owner().equals(player)) {
                        ItemUtils.addLore(memberSkull, "&8&l〔 &f&lTRYK PÅ MUSEHJULET FOR AT OVERFØRE EJERSKAB&8&l 〕");
                    }
                }
            }

            inventory.setItem(i, memberSkull);
            slotToMember.put(i, offlinePlayer);
        }

        player.openInventory(inventory);

        List<Map.Entry<OfflinePlayer, Integer>> finalInitialNap = initialNap;
        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            if(event.getSlot() == 36) {
                openMainInventory(player);
                return;
            }
            OfflinePlayer victim = slotToMember.get(event.getSlot());
            if(victim == null) return;
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
                                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                                bande.setMemberRank(victim, currentRank + 1);
                                break;
                            case PICKUP_HALF:
                                if(currentRank <= Bande.PermissionLevel.ROOKIE) return;
                                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                                bande.setMemberRank(victim, bande.getMemberRank(victim) - 1);
                                break;
                            case DROP_ONE_SLOT:
                                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                                bande.kickMember(victim, player);
                                break;
                            case NOTHING:
                                player.closeInventory();
                                if(bande.owner().equals(player)) {
                                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
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

        builder.onClick((slot, stateSnapshot) -> {
            if(slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

            try {
                float amount = Float.parseFloat(stateSnapshot.getText());
                if(amount <= 0) {
                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ugyldig mængde"));
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


    public CompletableFuture<Bande> promptForBande(OfflinePlayer player) {
        CompletableFuture<Bande> future = new CompletableFuture<>();
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Skriv et Bande navn");
        builder.onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

            Optional<Bande> selectedBande = BandePlugin.instance.bander.stream().filter(bande -> bande.getName().equalsIgnoreCase(stateSnapshot.getText())).findAny();

            if(selectedBande.isPresent()) {
                return Arrays.asList(
                        AnvilGUI.ResponseAction.close(),
                        AnvilGUI.ResponseAction.run(() -> future.complete(selectedBande.get()))
                );
            } else {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Ugyldig bande!"));
            }
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
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" RELATIONER");
        InventoryUtils.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;
        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, getBackButton());
        inventory.setItem(23, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI0OWNlN2QyODI4NWY2NjlkNDQ5ZTIzOGQwMDkxMmU2OTQzNGYwZDc1OWVhZThlODI3ODkxZWNkYWEwZjMxNCJ9fX0="), "&c&lRivaler", "&fKlik her for at se dine rivaler", "", "&8&l〔 &f&lTRYK HER &8&l 〕"));
        inventory.setItem(21, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhlYjczZGFmOGY2YTZjZDU2NjliMjgzMmEyNDZkYmY3ZTNhMTMwN2JjYzE5YThjOTZlZjc3NGQ3NmM1NmJhOSJ9fX0="), "&c&lAllierede", "&fKlik her for at se dine allierede", "", "&8&l〔 &f&lTRYK HER &8&l 〕"));
        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            switch (event.getSlot()) {
                case 36:
                    openMainInventory(player);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    break;
                case 21: //Allierede
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                    showAllies(player);
                    break;
                case 23:
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                    showRivals(player);
                    break;
            }

        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

    }

    public void showAllies(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" ALLIEREDE");
        InventoryUtils.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if(bande == null) return;

        String[] lore = new String[]{"&cDu har ikke adgang", "&ctil at tilføje en ally"};;

        if(bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER) {
            lore = new String[]{"&fKlik her", "&fFor at tilføje en ally"};
        }

        inventory.setItem(4, bande.getDisplaySkull());
        inventory.setItem(36, getBackButton());
        inventory.setItem(44, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVmNTM3NmQ4NjllZWUzZDBjNjJiNjdmNTY3MTRkYjkzZDFiYmY0ODMxNTU2YTdlN2QyZjdjMzRiNDllNGYifX19"), "&a&lTilføj Ally!", lore));

        if(bande.getAllies().isEmpty()) {
            inventory.setItem(10, ItemUtils.setNameAndLore(Material.BARRIER, "&c&lIngen allies!", "&fDu har ikke nogen allies endnu!"));
        }

        int i = 9;
        HashMap<Integer, Bande> slotToAlly = new HashMap<>();
        for(Bande ally : bande.getAllies()) {
            if(i == 34) continue;
            i++;
            if(i == 17 || i == 26) i += 2;

            slotToAlly.put(i, ally);

            ItemStack skullToDisplay = ally.getDisplaySkull();

            if(bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                ItemUtils.addLore(skullToDisplay, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT FJERNE&8&l 〕");
            }



            inventory.setItem(i, skullToDisplay);

        }


        player.openInventory(inventory);


        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            if (event.getSlot() == 36) {
                showRelationsMenu(player);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                return;
            }
            if(event.getSlot() == 44) {
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                promptForBande(player).thenAccept(selectedBande -> {
                    if(bande.hasAlly(selectedBande) || bande.hasRival(selectedBande)) return;
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);

                    bande.addAlly(selectedBande);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har tilføjet " + selectedBande.getName() + " til dine allies!"));

                    for(OfflinePlayer member : bande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har tilføjet " + selectedBande.getName() + " til jeres allies!"));
                        }

                    }
                    for(OfflinePlayer ally : selectedBande.members().keySet()) {
                        if(ally.isOnline()) {
                            ally.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + bande.getName() + " tilføjede jer til deres alliance"));
                        }
                    }

                    showAllies(player);
                });
                return;
            }
            if(event.getAction() != InventoryAction.PICKUP_ALL) return;

            if(bande.getMemberRank(player) < Bande.PermissionLevel.RIGHTHANDMAN) return;

            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);

            Bande targetAlly = slotToAlly.get(event.getSlot());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har fjernet " + targetAlly.getName() + " fra dine allies!"));

            for(OfflinePlayer member : bande.members().keySet()) {
                if(member.equals(player)) continue;
                if(member.isOnline()) {
                    member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har fjernet " + targetAlly.getName() + " fra jeres allies!"));
                }

            }
            for(OfflinePlayer ally : targetAlly.members().keySet()) {
                if(ally.isOnline()) {
                    ally.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin allierede " + bande.getName() + " opsagde jeres alliance"));
                }
            }

            bande.removeAlly(targetAlly);

            showAllies(player);


        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void showRivals(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU" + ChatColor.DARK_GRAY + " ⬤ " + ChatColor.RESET + "" + ChatColor.GRAY + " RIVALER");
        InventoryUtils.createBorders(inventory);
        Bande bande = Bande.getBande(player);

        if (bande == null) return;

        String[] lore = new String[]{"&cDu har ikke adgang", "&ctil at tilføje en rival"};
        ;

        if (bande.getMemberRank(player) >= Bande.PermissionLevel.PUSHER) {
            lore = new String[]{"&fKlik her", "&fFor at tilføje en rival"};

            inventory.setItem(4, bande.getDisplaySkull());
            inventory.setItem(36, getBackButton());
            inventory.setItem(44, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVmNTM3NmQ4NjllZWUzZDBjNjJiNjdmNTY3MTRkYjkzZDFiYmY0ODMxNTU2YTdlN2QyZjdjMzRiNDllNGYifX19"), "&a&lTilføj Rival!", lore));

            if (bande.getRivals().isEmpty()) {
                inventory.setItem(10, ItemUtils.setNameAndLore(Material.BARRIER, "&c&lIngen allies!", "&fDu har ikke nogen allies endnu!"));
            }

            int i = 9;
            HashMap<Integer, Bande> slotToRival = new HashMap<>();
            for (Bande rival : bande.getRivals()) {
                if (i == 34) continue;
                i++;
                if(i == 17 || i == 26) i += 2;

                slotToRival.put(i, rival);

                ItemStack skullToDisplay = rival.getDisplaySkull();

                if (bande.getMemberRank(player) >= Bande.PermissionLevel.RIGHTHANDMAN) {
                    ItemUtils.addLore(skullToDisplay, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT FJERNE&8&l 〕");
                }


                inventory.setItem(i, skullToDisplay);

            }


            player.openInventory(inventory);


            InventoryManager lambda = (InventoryClickEvent event) -> {
                if(event == null) return;
                if(player == null) return;
                if (event.getSlot() == 36) {
                    showRelationsMenu(player);
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    return;
                }
                if (event.getSlot() == 44) {
                    player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    promptForBande(player).thenAccept(selectedBande -> {
                        if(bande.hasAlly(selectedBande) || bande.hasRival(selectedBande)) return;
                        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
                        bande.addRival(selectedBande);


                        for(Bande rival : bande.getRivals()) {
                            Bukkit.broadcastMessage(rival.getName());
                        }

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har tilføjet " + selectedBande.getName() + " til dine rivals!"));

                        for(OfflinePlayer member : bande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har tilføjet " + selectedBande.getName() + " til jeres rivals!"));
                            }

                        }
                        for(OfflinePlayer ally : selectedBande.members().keySet()) {
                            if(ally.isOnline()) {
                                ally.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + bande.getName() + " tilføjede jer til deres rivaler"));
                            }
                        }

                        showRivals(player);
                    });
                    return;
                }
                if (event.getAction() != InventoryAction.PICKUP_ALL) return;

                if (bande.getMemberRank(player) < Bande.PermissionLevel.RIGHTHANDMAN) return;


                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1 ,1);
                Bande targetRival = slotToRival.get(event.getSlot());

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har fjernet " + targetRival.getName() + " fra dine rivaler!"));

                for (OfflinePlayer member : bande.members().keySet()) {
                    if(member.equals(player)) continue;
                    if (member.isOnline()) {
                        member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + player.getName() + " har fjernet " + targetRival.getName() + " fra jeres rivaler!"));
                    }

                }
                for (OfflinePlayer rival : targetRival.members().keySet()) {
                    if (rival.isOnline()) {
                        rival.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin rival " + bande.getName() + " fjernede dig som rival"));
                    }
                }

                bande.removeRival(targetRival);

                showRivals(player);

            };

            inventoryManager.put(player, new InventoryData(lambda, inventory));
        }
    }
    public CompletableFuture<OfflinePlayer> promptForPlayer(Player player, Bande bande) {
        CompletableFuture<OfflinePlayer> completableFuture = new CompletableFuture<>();

        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(BandePlugin.instance);
        builder.text("Spiller navn");
        builder.onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            Optional<OfflinePlayer> playerExists = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> offlinePlayer.getName().equalsIgnoreCase(stateSnapshot.getText())).findAny();

            if (!playerExists.isPresent()) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Spiller ikke fundet!"));
            }

            if (Bande.getBande(playerExists.get()) != null) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "Har allerade bande"));
            }

            return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> completableFuture.complete(playerExists.get())));
        });

        builder.open(player);

        return completableFuture;
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

            boolean isBannedName = Arrays.stream(BandePlugin.bannedNames).anyMatch(string -> stateSnapshot.getText().toLowerCase().contains(string.toLowerCase()));

            if(stateSnapshot.getText().length() > 10) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "For langt"));
            }

            if(stateSnapshot.getText().length() < 4) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(ChatColor.RED + "For kort"));
            }

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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu lavede banden &n" + name + "&r&f!"));
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
        BandePlugin.manager.saveBande(BandePlugin.instance.bander);
    }
    public void openInvitationsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+" ⬤ "+ChatColor.RESET+""+ChatColor.GRAY+" INVITATIONER");
        InventoryUtils.createBorders(inventory);

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
            if(offlinePlayer.equals(player)) {
                i++;
                if(i == 17 || i == 26) i += 2;
                Bande bande = BandePlugin.invites.get(offlinePlayer);
                if(bande == null) continue;
                slotToBande.put(i, bande);

                ItemStack skullToDisplay = bande.getDisplaySkull();

                ItemUtils.addLore(skullToDisplay, " ", "&8&l〔 &f&lVENSTRE KLIK FOR AT JOINE&8&l 〕", "&8&l〔 &f&lHØJRE KLIK FOR AT AFVISE&8&l 〕");

                inventory.setItem(i, skullToDisplay);
            }
        }

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event == null) return;
            if(player == null) return;
            Bande clickedBande = slotToBande.get(event.getSlot());

            if(clickedBande == null) return;

            switch(event.getAction()) {
                case PICKUP_ALL: // LEFT CLICK
                    player.closeInventory();
                    if(!clickedBande.addMember(player, Bande.PermissionLevel.ROOKIE)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fBanden er fuld!"));
                    } else {
                        BandePlugin.invites.remove(player);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu sluttede dig til banden &n"+clickedBande.getName()+"&r&f!"));
                        for(OfflinePlayer member : clickedBande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f&n"+player.getName()+"&r &fhar sluttet sig til banden!"));
                            }
                        }
                    }
                    break;
                case PICKUP_HALF: // RIGHT CLICK
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu har afvist invitationen til &n"+clickedBande.getName()+"&r&f!"));
                    BandePlugin.invites.remove(player);
                    for(OfflinePlayer member : clickedBande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f&n"+player.getName()+"&r &fhar afvist bandens invitation!"));
                        }
                    }
                    break;
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!inventoryManager.containsKey(player)) {
                    cancel();
                    return;
                }
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

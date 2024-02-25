package dk.wavebleak.bandeplugin.command;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.InventoryData;
import dk.wavebleak.bandeplugin.classes.InventoryManager;
import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.utils.ColorUtil;
import dk.wavebleak.bandeplugin.utils.InstantFirework;
import dk.wavebleak.bandeplugin.utils.InventoryUtil;
import dk.wavebleak.bandeplugin.utils.ItemsUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
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
            player.sendMessage(ChatColor.GREEN + "Bande pluginnet er nu loadet fra databasen!");
            return true;
        }
        if(strings.length == 1 && strings[0].equalsIgnoreCase("save") && player.isOp()) {
            BandePlugin.instance.save();
            player.sendMessage(ChatColor.GREEN + "Bande pluginnet er nu gemt til databasen!");
            return true;
        }

        openMainInventory(player);


        return true;
    }

    public void openMainInventory(Player player) {
        Bande bande = Bande.getBande(player);

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+""+ChatColor.BOLD+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" FORSIDE");
        InventoryUtil.createBorders(inventory);
        if(bande == null) { // Ingen bande

            inventory.setItem(20, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg3MDMxYzQ3MjZkZGVkZDY1YjZhMTFkMzE0N2U2NzI0ZGVmYmIyOTBkYTI5Y2JiNzlkYTI0OTA1NDZjYmYifX19"), "&c&lTOP LEVEL", "&fplease make a leaderboard!", "&ffor top level","&fpookie wookie bear"));
            inventory.setItem(22, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0="), "&4&lOpret en bande", "&8\u2B24 &fDet koster &c$5000 &fat oprette en bande!"));
            inventory.setItem(24, ItemsUtil.setNameAndLore(ItemsUtil.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMzA0OGY1M2IyNGZiMzZlYmFjNjRkODU4Mzg5MTM1ODgzZjI1ODc0ZTQ1NDZkMWZjZDg5YzMwYmQ2ZjY1NiJ9fX0="), "&c&lDINE INVITATIONER", "&8\u2B24 &fKlik her for at", "&8 &8 &8 &fse dine invitationer"));

        } else { // Har en bande

            inventory.setItem(4, ItemsUtil.setNameAndLore(ItemsUtil.getSkull(player), "&b&l" + bande.getName(), "&bLevel: &f" + bande.getLevel(), "&bKDR: &f" + bande.getKDR()));


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
                            showAnvilGUI(player);
                        }
                        break;
                }
            } else { // Bande menu
                //TODO: Add player manager for highups (3+)
                //TODO: Upgrades shop
                //TODO: Bank
                //TODO: Levelup Button
                //TODO: Allierede
                //TODO: Rivaler
                //TODO: Leaderboard

                List<Bande> sortedLeaderboard = BandePlugin.instance.bander.stream().sorted(Comparator.comparingDouble(x -> x.getLevel())).collect(Collectors.toList());
                Collections.reverse(sortedLeaderboard);




            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));
    }


    public void showAnvilGUI(Player player) {
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

        HashMap<OfflinePlayer, Integer> map = new HashMap<>();

        map.put(player, Bande.PermissionLevel.KINGPIN.getValue());

        BandePlugin.instance.bander.add(new Bande(map, player, Collections.emptyList(), Collections.emptyList(), 1, 0, 0, 0, 0, 0, 0, 0, name));
        player.sendMessage(ChatColor.GREEN + "Du lavede banden: " + name + "!");
        BandePlugin.manager.saveData(BandePlugin.instance.bander);
    }
    public void openInvitationsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, ChatColor.DARK_RED + "" + ChatColor.BOLD + "BANDE MENU"+ChatColor.DARK_GRAY+""+ChatColor.BOLD+" \u2B24 "+ChatColor.RESET+""+ChatColor.GRAY+" INVITATIONER");
        InventoryUtil.createBorders(inventory);

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

                inventory.setItem(i, bande.getDisplaySkull());
            }
        }

        InventoryManager lambda = (InventoryClickEvent event) -> {
            Bande clickedBande = slotToBande.get(event.getSlot());

            if(clickedBande == null) return;

            switch(event.getAction()) {
                case PICKUP_ALL: // LEFT CLICK
                    player.closeInventory();
                    if(!clickedBande.addMember(player, Bande.PermissionLevel.ROOKIE.getValue())) {
                        player.sendMessage(ChatColor.RED + "Denne bande er fuld!");
                    } else {
                        BandePlugin.invites.remove(player);
                        player.sendMessage(ChatColor.GREEN + "Du joinede banden: " + clickedBande.getName());
                        for(OfflinePlayer member : clickedBande.members().keySet()) {
                            if(member.equals(player)) continue;
                            if(member.isOnline()) {
                                member.getPlayer().sendMessage(ChatColor.GREEN + player.getName() + " joinede banden!");
                            }
                        }
                    }
                    break;
                case PICKUP_HALF: // RIGHT CLICK
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Du har afvist invitationen!");
                    BandePlugin.invites.remove(player);
                    for(OfflinePlayer member : clickedBande.members().keySet()) {
                        if(member.equals(player)) continue;
                        if(member.isOnline()) {
                            member.getPlayer().sendMessage(ChatColor.RED + player.getName() + " afviste jeres invitation!");
                        }
                    }
                    break;
            }
        };

        inventoryManager.put(player, new InventoryData(lambda, inventory));

        player.openInventory(inventory);

    }
}

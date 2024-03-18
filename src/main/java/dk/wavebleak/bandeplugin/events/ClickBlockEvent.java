package dk.wavebleak.bandeplugin.events;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.*;
import dk.wavebleak.bandeplugin.utils.*;
import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.shapers.ParticleCircle;
import hm.zelha.particlesfx.shapers.ParticleCircleFilled;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleSFX;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ClickBlockEvent implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        BandeTerritorie selectedTerritory = null;
        for(BandeTerritorie bandeTerritorie : BandePlugin.instance.territories) {
            if(bandeTerritorie.isBanner(event.getBlock())) {
                selectedTerritory = bandeTerritorie;
            }
        }
        if(selectedTerritory == null) return;

        if(!player.isOp()) {
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu fjernede territoriet: &n" + selectedTerritory.getName()));
        selectedTerritory.remove();
    }
    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();

        BandeTerritorie selectedTerritory = null;
        for(BandeTerritorie bandeTerritorie : BandePlugin.instance.territories) {
            if(bandeTerritorie.isBanner(event.getClickedBlock())) {
                selectedTerritory = bandeTerritorie;
            }
        }

        if(selectedTerritory == null) return;

        Bande bande = Bande.getBande(player);

        boolean ownsTerritory = false;
        boolean isOwned = selectedTerritory.isCurrentlyOwned();


        if(bande != null && selectedTerritory.isCurrentlyOwned()) {
            ownsTerritory = selectedTerritory.getCurrentlyOwned().equals(bande);
        }

        if(ownsTerritory) {
            showGeneratedBread(player, selectedTerritory);
            return;
        }
        if(!isOwned) {
            if(bande != null) {
                if(bande.isUnlockedTerritory()) {
                    showOwnMenu(player, selectedTerritory);
                }else {
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fLås op for bande territorier i upgrades menuen i &n/bande&r&f!"));
                }
            } else {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu ejer ikke en bande!"));
            }
        } else if(bande != null) {
            if(bande.isUnlockedTerritory()) {
                showFightMenu(player, selectedTerritory);
            } else {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fLås op for bande territorier i upgrades menuen i &n/bande&r&f!"));
            }
        } else {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDu ejer ikke en bande!"));
        }



    }

    public void showOwnMenu(Player player, BandeTerritorie territory) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &0Ikke ejet!"));
        InventoryUtils.createBorders(inventory);

        inventory.setItem(22, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYxODQ2MTBjNTBjMmVmYjcyODViYzJkMjBmMzk0MzY0ZTgzNjdiYjMxNDg0MWMyMzhhNmE1MjFhMWVlMTJiZiJ9fX0="), "&c&lOvertag Territorie", "&fOvertag territoriet: " + territory.getName(), " ", "&8&l〔 &f&lTRYK HER &8&l 〕"));

        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event.getSlot() != 22) return;

            Bande bande = Bande.getBande(player);

            if(bande == null) return;

            territory.setOwnedBandeID(bande.getBandeID());

            for(OfflinePlayer member : bande.members().keySet()) {
                if(member.isOnline()) {
                    member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin bande ejer nu territoriet: &n" + territory.getName() + "&r&f!"));
                }
            }
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            territory.update();
            BandePlugin.gracePeriod.put(territory, 600);
        };

        BandePlugin.inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public int countBread(Inventory inventory) {
        int breadCount = 0;
        for (ItemStack item : inventory.getContents()) {
            if(item == null) continue;
            if (item.getType().equals(Material.BREAD)) {
                breadCount += item.getAmount();
            }
        }
        return breadCount;
    }

    public void showFightMenu(Player player, BandeTerritorie territory) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &0" + territory.getName()));
        InventoryUtils.createBorders(inventory);

        inventory.setItem(4, territory.getOwnedBande().getDisplaySkull());

        boolean isGracePeriod = BandePlugin.gracePeriod.containsKey(territory);

        if(isGracePeriod) {
            inventory.setItem(22, ItemUtils.setNameAndLore(Material.DIAMOND_CHESTPLATE, "&c&lBeskyttet Territorie", "&fDette territorie er lige nu beskyttet imod angreb fra andre bander", "&fKom venligst tilbage om:", " ", "&f&n" + StringUtils.formatTime(BandePlugin.gracePeriod.get(territory)) + "&r&f!"));
        } else {
            inventory.setItem(22, ItemUtils.setNameAndLore(Material.IRON_SWORD, "&c&lOvertag Territorie", "&fStart en kamp hvor du skal forblive i nærheden", "&faf dette territorie i 30 sekunder for at overtage det", "&fhvis du fejler beholder &n" + territory.getOwnedBande().getName() + "&r &fterritoriet!"));
        }



        player.openInventory(inventory);

        InventoryManager lambda = (InventoryClickEvent event) -> {
              if(event.getSlot() != 22) return;
              if(isGracePeriod) return;

              startFight(Bande.getBande(player), territory);
              player.closeInventory();
        };

        BandePlugin.inventoryManager.put(player, new InventoryData(lambda, inventory));
    }

    public void startFight(Bande attackerBande, BandeTerritorie territory) {
        Bande defenderBande = territory.getOwnedBande();

        for(OfflinePlayer attackerMember : attackerBande.members().keySet()) {
            if(attackerMember.isOnline()) {
                attackerMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fI har startet en kamp for territoriet &n" + territory.getName() + "&r&f!"));
                attackerMember.getPlayer().playSound(attackerMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
            }
        }
        for(OfflinePlayer defenderMember : defenderBande.members().keySet()) {
            if(defenderMember.isOnline()) {
                defenderMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &f" + attackerBande.getName() + " har startet en kamp for territoriet &n" + territory.getName() + "&r&f! Beskyt det!"));
                defenderMember.getPlayer().playSound(defenderMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
            }
        }

        Location startCenter = territory.getLocation().add(0.5, 0.2, 0.5);
        final int radius = 6;
        final int ticksToFinish = 30 * 20;
        List<ParticleCircle> circles = new ArrayList<>();

        for(float i = 0; i > -1; i -= 0.1f) {
            circles.add(new ParticleCircle(new ParticleDustColored(ColorUtils.fromBukkitColor(ColorUtils.getTop3ColorsFromSkin(territory.getOwnedBande().owner())[0])), new LocationSafe(startCenter.clone().add(0, i, 0)), radius));
        }
        ParticleCircleFilled filledCircled = new ParticleCircleFilled(new ParticleDustColored(ColorUtils.fromBukkitColor(ColorUtils.getTop3ColorsFromSkin(territory.getOwnedBande().owner())[1])), new LocationSafe(startCenter.clone()), radius - 0.2);

        filledCircled.start();
        for(ParticleCircle circle : circles) {
            circle.start();
        }

        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;

                if(i >= ticksToFinish) {
                    for(OfflinePlayer attackerMember : attackerBande.members().keySet()) {
                        if(attackerMember.isOnline()) {
                            attackerMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fI vandt kampen for territoriet &n" + territory.getName() + "&r&f!"));
                            attackerMember.getPlayer().playSound(attackerMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
                        }
                    }
                    for(OfflinePlayer defenderMember : defenderBande.members().keySet()) {
                        if(defenderMember.isOnline()) {
                            defenderMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fI fejlede i at beskytte territoriet &n" + territory.getName() + "&r&f!"));
                            defenderMember.getPlayer().playSound(defenderMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
                        }
                    }
                    Color[] colors = ColorUtils.getTop3ColorsFromSkin(attackerBande.owner());
                    new InstantFirework(
                            FireworkEffect.builder()
                                    .withTrail()
                                    .withColor(colors[0], colors[1], colors[2])
                                    .build(),
                            startCenter.clone().add(0, 1, 0));
                    territory.setOwnedBandeID(attackerBande.getBandeID());
                    territory.setGeneratedBread(0);
                    territory.update();
                    for(ParticleCircle circle : circles) {
                        circle.stop();
                    }
                    filledCircled.stop();
                    BandePlugin.gracePeriod.put(territory, 600);
                    cancel();
                }


                List<Player> playersInArea = new ArrayList<>();
                List<Player> attackersInArea = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(horizontalDistance(startCenter, player.getLocation()) <= radius) {
                        playersInArea.add(player);

                        Bande bande = Bande.getBande(player);

                        if(bande != null && bande.equals(attackerBande)) {
                            attackersInArea.add(player);
                        }
                    }
                }
                if(attackersInArea.isEmpty()) {
                    for(OfflinePlayer attackerMember : attackerBande.members().keySet()) {
                        if(attackerMember.isOnline()) {
                            attackerMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fI fejlede kampen for territoriet &n" + territory.getName() + "&r&f!"));
                            attackerMember.getPlayer().playSound(attackerMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
                        }
                    }
                    for(OfflinePlayer defenderMember : defenderBande.members().keySet()) {
                        if(defenderMember.isOnline()) {
                            defenderMember.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fI beskyttede territoriet &n" + territory.getName() + "&r&f!"));
                            defenderMember.getPlayer().playSound(defenderMember.getPlayer().getLocation(), Sound.ANVIL_LAND, 1, 0.1f);
                        }
                    }
                    for(ParticleCircle circle : circles) {
                        circle.stop();
                    }
                    filledCircled.stop();
                    BandePlugin.gracePeriod.put(territory, 600);
                    cancel();

                }

                float progress = (i / (float)ticksToFinish) * 100;
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("〔");
                builder.append(StringUtils.progressBar('■', ChatColor.GREEN, ChatColor.GRAY, progress, 30));
                builder.append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append("〕");
                for(Player player : playersInArea) {
                    ActionBarAPI.sendActionBar(player, builder.toString());
                }
            }
        }.runTaskTimer(BandePlugin.instance, 1, 1);


    }


    public double horizontalDistance(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();

        return Math.sqrt(dx * dx + dz * dz);
    }
    public void showGeneratedBread(Player player, BandeTerritorie territory) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &0" + territory.getName()));
        InventoryUtils.createBorders(inventory);

        boolean noBread = territory.getGeneratedBread() == 0;

        if(noBread) {
            inventory.setItem(10, ItemUtils.setNameAndLore(Material.BARRIER, "&c&lIngen brød!", "&cDer er ikke genereret noget brød endnu..."));
        } else {
            generateBread(inventory, territory);
        }


        inventory.setItem(4, territory.getOwnedBande().getDisplaySkull());
        inventory.setItem(40, ItemUtils.setNameAndLore(ItemUtils.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="), "&c&lOpsig Territorie", "&fOpsig ejerskab af territoriet", " ", "&8&l〔 &f&lTRYK HER &8&l 〕"));

        player.openInventory(inventory);

        CloseInventoryManager closeLambda = (InventoryCloseEvent event) -> {
              int bread = countBread(event.getInventory());

              if(territory.isCurrentlyOwned()) {
                  territory.setGeneratedBread(bread);
              } else {
                  territory.setGeneratedBread(0);
              }
        };

        InventoryManager lambda = (InventoryClickEvent event) -> {
            if(event.getSlot() == 40) {
                Bande bande = Bande.getBande(player);

                if(bande == null) return;

                territory.setOwnedBandeID(null);
                territory.update();
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 1);

                for(OfflinePlayer member : bande.members().keySet()) {
                    if(member.isOnline()) {
                        member.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8( &4&lBANDE &8) &fDin bande opsagde ejerskab af territoriet: &n" + territory.getName() + "&r&f!"));
                    }
                }
            }
            if(noBread) return;

            List<Integer> nonoList = InventoryUtils.getBorderSlots(inventory);

            if(nonoList.stream().noneMatch(nonoSlot -> nonoSlot == event.getSlot())) {
                event.setCancelled(false);
            }
        };

        BandePlugin.inventoryManager.put(player, new InventoryData(lambda, inventory));
        BandePlugin.closeInventoryManager.put(player, new CloseInventoryData(closeLambda, inventory));
    }

    public void generateBread(Inventory inventory, BandeTerritorie territory) {
        int totalBread = territory.getGeneratedBread();
        while (totalBread > 0) {
            int amount = Math.min(totalBread, 64); // add 64 or remaining breads whichever is minimum
            ItemStack breadItemStack = new ItemStack(Material.BREAD, amount);
            inventory.addItem(breadItemStack);
            totalBread -= amount;
        }
    }

}

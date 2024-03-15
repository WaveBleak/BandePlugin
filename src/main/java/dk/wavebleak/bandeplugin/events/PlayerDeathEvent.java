package dk.wavebleak.bandeplugin.events;

import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.Bande;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDeathEvent implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        BandePlugin.inventoryManager.remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Bande victimBande = Bande.getBande((Player)event.getEntity());
        if(victimBande != null) {
            victimBande.addDeath();
        }
        BandePlugin.inventoryManager.remove((Player) event.getEntity());
        if(event.getEntity() instanceof Player && event.getEntity().getKiller() != null) {
            Player attacker = event.getEntity().getKiller();
            Bande attackerBande = Bande.getBande(attacker);

            if(attackerBande != null) {
                attackerBande.addKill();
                Player victim = (Player) event.getEntity();

                if(victimBande != null && attackerBande.hasRival(victimBande)) {
                    attackerBande.setRivalKills(attackerBande.getRivalKills() + 1);
                }

                if(!victim.isOp()) {
                    if(victim.hasPermission("direktør")) {
                        attackerBande.addDirKill();
                        return;
                    }
                    if(victim.hasPermission("inspektør")) {
                        attackerBande.addInsKill();
                        return;
                    }
                    if(victim.hasPermission("officer")) {
                        attackerBande.addOfficerKill();
                        return;
                    }
                    if(victim.hasPermission("vagt")) {
                        attackerBande.addVagtKill();
                    }
                }
            }
        }
    }
}

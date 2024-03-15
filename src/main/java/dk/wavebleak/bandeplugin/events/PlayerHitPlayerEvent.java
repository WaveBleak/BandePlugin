package dk.wavebleak.bandeplugin.events;

import dk.wavebleak.bandeplugin.classes.Bande;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHitPlayerEvent implements Listener {

    @EventHandler
    public void playerHitPlayerEvent(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();


            Bande attackerBande = Bande.getBande(attacker);
            Bande victimBande = Bande.getBande(victim);

            if(Bande.isSameTeam(victim, attacker) || attackerBande.hasAlly(victimBande)) {
                double newDamage = getPercentage(attackerBande.getAllyHitMitigation(), event.getDamage());

                event.setDamage(newDamage);
            }
        }
    }

    public static double getPercentage(double percent, double total) {
        return (percent / 100) * total;
    }

}

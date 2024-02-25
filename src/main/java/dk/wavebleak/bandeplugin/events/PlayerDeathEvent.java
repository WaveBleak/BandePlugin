package dk.wavebleak.bandeplugin.events;

import dk.wavebleak.bandeplugin.classes.Bande;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerDeathEvent implements Listener {


    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Bande victimTeam = Bande.getBande((Player)event.getEntity());
        if(victimTeam != null) {
            victimTeam.addDeath();
        }
        if(event.getEntity() instanceof Player && event.getEntity().getKiller() != null) {
            Player attacker = event.getEntity().getKiller();
            Bande attackerTeam = Bande.getBande(attacker);

            if(attackerTeam != null) {
                attackerTeam.addKill();
            }
        }
    }
}

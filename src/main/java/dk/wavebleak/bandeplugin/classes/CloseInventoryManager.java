package dk.wavebleak.bandeplugin.classes;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface CloseInventoryManager {
    void run(InventoryCloseEvent event);
}

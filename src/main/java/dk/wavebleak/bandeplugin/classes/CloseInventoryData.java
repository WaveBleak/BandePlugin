package dk.wavebleak.bandeplugin.classes;

import org.bukkit.inventory.Inventory;

public class CloseInventoryData {
    private final CloseInventoryManager lambda;
    private final Inventory inventory;

    public CloseInventoryData(CloseInventoryManager lambda, Inventory inventory) {
        this.lambda = lambda;
        this.inventory = inventory;
    }


    public Inventory getInventory() {
        return inventory;
    }

    public CloseInventoryManager getLambda() {
        return lambda;
    }
}

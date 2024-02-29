package dk.wavebleak.bandeplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class InventoryUtils {

    public static void createBorders(Inventory inv) {
        int inventorySize = 9;
        int rows = inv.getSize() / 9;

        ItemStack item = ItemUtils.setNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15), "&6");


        IntStream.range(0, inventorySize).forEach(i -> {
            inv.setItem(i, item);
            inv.setItem(inventorySize * (rows - 1) + i, item);
        });


        IntStream.range(1, rows).forEach(i -> {
            int base = i * inventorySize;
            inv.setItem(base, item);
            inv.setItem(base + inventorySize - 1, item);
        });
    }

}

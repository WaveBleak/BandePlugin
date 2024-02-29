package dk.wavebleak.bandeplugin.utils;

import dk.wavebleak.bandeplugin.BandePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.UUID;

public class PlayerUtils {

    public static boolean IsOP(OfflinePlayer player) {
        if(Arrays.stream(BandePlugin.info.admins).anyMatch(_player -> Bukkit.getOfflinePlayer(UUID.fromString(_player)).getUniqueId().equals(player.getUniqueId()))) return true;
        return player.isOp();
    }

}

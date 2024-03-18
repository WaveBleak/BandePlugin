package dk.wavebleak.bandeplugin.utils;


import dk.wavebleak.bandeplugin.BandePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBarAPI {
    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline())
            return;
        message = ChatColor.translateAlternateColorCodes('&', message);
        try {
            Object packet;
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server.v1_8_R3.Packet");
            Class<?> chatComponentTextClass = Class.forName("net.minecraft.server.v1_8_R3.ChatComponentText");
            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server.v1_8_R3.IChatBaseComponent");
            try {
                Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server.v1_8_R3.ChatMessageType");
                Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                Object chatMessageType = null;
                for (Object obj : chatMessageTypes) {
                    if (obj.toString().equals("GAME_INFO"))
                        chatMessageType = obj;
                }
                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
                packet = packetPlayOutChatClass.getConstructor(new Class[] { iChatBaseComponentClass, chatMessageTypeClass }).newInstance(new Object[] { chatCompontentText, chatMessageType });
            } catch (ClassNotFoundException cnfe) {
                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
                packet = packetPlayOutChatClass.getConstructor(new Class[] { iChatBaseComponentClass, byte.class }).newInstance(new Object[] { chatCompontentText, Byte.valueOf((byte)2) });
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle", new Class[0]);
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer, new Object[0]);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", new Class[] { packetClass });
            sendPacketMethod.invoke(playerConnection, new Object[] { packet });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);
        if (duration >= 0)
            (new BukkitRunnable() {
                public void run() {
                    ActionBarAPI.sendActionBar(player, "");
                }
            }).runTaskLater((Plugin)BandePlugin.instance, (duration + 1));
        while (duration > 40) {
            duration -= 40;
            (new BukkitRunnable() {
                public void run() {
                    ActionBarAPI.sendActionBar(player, message);
                }
            }).runTaskLater((Plugin) BandePlugin.instance, duration);
        }
    }

    public static void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers())
            sendActionBar(p, message, duration);
    }
}
package dk.wavebleak.bandeplugin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dk.wavebleak.bandeplugin.BandePlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class ItemUtils {

    public static ItemStack setNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> newLore = Arrays.stream(lore)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setNameAndLore(Material item, String name, String... lore) {
        return setNameAndLore(new ItemStack(item), name, lore);
    }

    public static ItemStack getSkull(OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skullItem.getItemMeta();


        GameProfile gameProfile = new GameProfile(player.getUniqueId(), null);
        gameProfile.getProperties().put("textures", new Property("textures", getTextureValue(player)));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
        } catch (Exception e) {
            BandePlugin.instance.getLogger().severe(e.getMessage());
        }

        skullItem.setItemMeta(meta);
        return skullItem;
    }



    public static void addLore(ItemStack item, String... lines) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        for(String line : lines) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }


    public static ItemStack getSkull(String textureValue) {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skullItem.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", textureValue));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
        } catch (Exception e) {
            BandePlugin.instance.getLogger().severe(e.getMessage());
        }

        skullItem.setItemMeta(meta);
        return skullItem;
    }

    private static String getTextureValue(String uuid) {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;


        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                String jsonResponse = response.toString();

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);

                JsonArray properties = json.getAsJsonArray("properties");
                JsonObject texture = properties.get(0).getAsJsonObject();

                return texture.get("value").getAsString();
            }
        } catch (Exception e) {
            BandePlugin.instance.getLogger().severe(e.getMessage());
        }
        return null;
    }

    private static String getTextureValue(OfflinePlayer player) {
        return getTextureValue(player.getUniqueId().toString());
    }


}

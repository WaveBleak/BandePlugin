package dk.wavebleak.bandeplugin.utils;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ColorUtil {

    public static Color[] getTop3ColorsFromSkin(OfflinePlayer player) {
        return getTop3ColorsFromSkin(player.getUniqueId().toString());
    }

    public static Color[] getTop3ColorsFromSkin(String uuid) {
        String skinURL = "https://mineskin.eu/skin/" + uuid;
        Map<Integer, Integer> colorFrequency = new HashMap<>();
        try {
            URL url = new URL(skinURL);
            BufferedImage image = ImageIO.read(url);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    colorFrequency.put(rgb, colorFrequency.getOrDefault(rgb, 0) + 1);
                }
            }

            PriorityQueue<Map.Entry<Integer, Integer>> topColors =
                    new PriorityQueue<Map.Entry<Integer, Integer>>(3, Comparator.comparing(Map.Entry::getValue));

            for (Map.Entry<Integer, Integer> entry : colorFrequency.entrySet()) {
                topColors.offer(entry);
                while (topColors.size() > 3) {
                    topColors.poll();
                }
            }

            int index = 2;
            Color[] topThreeColors = new Color[3];
            while (!topColors.isEmpty()) {
                int rgb = topColors.poll().getKey();
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                topThreeColors[index] = Color.fromRGB(red, green, blue);
                index--;
            }
            return topThreeColors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Color[]{Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0)};
    }

}

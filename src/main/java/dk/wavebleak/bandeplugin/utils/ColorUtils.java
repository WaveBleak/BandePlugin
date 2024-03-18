package dk.wavebleak.bandeplugin.utils;

import dk.wavebleak.bandeplugin.BandePlugin;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
@SuppressWarnings("unused")
public class ColorUtils {

    public static Color[] getTop3ColorsFromSkin(OfflinePlayer player) {
        return getTop3ColorsFromSkin(player.getUniqueId().toString());
    }


    public static hm.zelha.particlesfx.util.Color fromBukkitColor(Color color) {
        return new hm.zelha.particlesfx.util.Color(color.getRed(), color.getBlue(), color.getGreen());
    }
    public static DyeColor getClosestDyeColor(Color color) {
        int minDistanceSquared = Integer.MAX_VALUE;
        DyeColor closest = null;

        for (DyeColor dyeColor : DyeColor.values()) {
            Color dyeRgb = dyeColor.getColor();
            int distanceSquared = distanceSquared(color, dyeRgb);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closest = dyeColor;
            }
        }
        return closest;
    }

    private static int distanceSquared(Color color1, Color color2) {
        int rDiff = color1.getRed() - color2.getRed();
        int gDiff = color1.getGreen() - color2.getGreen();
        int bDiff = color1.getBlue() - color2.getBlue();

        return rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;
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
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    if ((red == 0 && green == 0 && blue == 0) || (red == 255 && green == 255 && blue == 255)) {
                        // Ignore black and white pixels
                        continue;
                    }

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
            BandePlugin.instance.getLogger().severe(e.getMessage());
        }
        return new Color[]{Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0), Color.fromRGB(0, 0, 0)};
    }




}

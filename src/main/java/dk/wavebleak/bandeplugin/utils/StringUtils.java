package dk.wavebleak.bandeplugin.utils;

import org.bukkit.ChatColor;

public class StringUtils {


    public static String formatTime(int seconds) {
        int minutes = (int) Math.floor((double) seconds / 60);
        int hours = (int) Math.floor((double) minutes / 60);
        int days = (int) Math.floor((double) hours / 24);

        if(days >= 1) {
            return days + " dag" + (days > 1 ? "e" : "");
        }
        if(hours >= 1) {
            return hours + " time" + (hours > 1 ? "r" : "");
        }
        if(minutes >= 1) {
            return minutes + " minut" + (minutes > 1 ? "ter" : "");
        }
        return seconds + " sekund" + (seconds > 1 ? "er" : "");
    }

    public static String progressBar(char progressChar, ChatColor color1, ChatColor color2, float percentComplete, int totalChars) {
        String progressBar = "";

        int progressChars = (int) (percentComplete/100.0 * totalChars);
        int blankChars = totalChars - progressChars;

        for(int i=0; i<progressChars; i++) {
            progressBar += color1 + Character.toString(progressChar);
        }

        for(int i=0; i<blankChars; i++) {
            progressBar += color2 + Character.toString(progressChar);
        }

        return progressBar;
    }

}

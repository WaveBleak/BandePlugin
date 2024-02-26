package dk.wavebleak.bandeplugin.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class GithubUtils {

    public static boolean checkVersion() {
        try {
            final String thisVersion = "0.0.2";
            final String githubVersion = getVersion();

            return isFirstVersionNewer(thisVersion, githubVersion);
        }catch (Exception e) {
            return false;
        }

    }


    private static boolean isFirstVersionNewer(String version1, String version2) {
        if(version1.equalsIgnoreCase(version2)) return true;
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");


        for(int i = 0; i < v1.length && i < v2.length; i++) {
            int part1 = Integer.parseInt(v1[i]);
            int part2 = Integer.parseInt(v2[i]);
            if (part1 > part2) {
                return true;
            } else if (part1 < part2) {
                return false;
            }

        }

        if(v1.length>v2.length && Integer.parseInt(v1[v2.length]) > 0){
            return true;
        }
        return false;
    }


    private static String getVersion() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/WaveBleak/BandePlugin/master/build.version");
        StringBuilder content = new StringBuilder();

        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
                content.append(System.lineSeparator());
            }
        }

        return content.toString().trim();
    }
}

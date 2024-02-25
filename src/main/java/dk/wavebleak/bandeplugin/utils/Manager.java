package dk.wavebleak.bandeplugin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.Bande;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Manager {

    private final File dbFile;
    private JsonArray dataArray;
    private final Gson gson = new Gson();
    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final Gson advancedGson = new GsonBuilder()
            .registerTypeAdapter(Bande.class, (JsonSerializer<Bande>) (bande, typeOfSrc, context) -> {
                JsonObject jsonObject = new JsonObject();

                JsonElement members = gson.toJsonTree(bande.getMembersUUID());
                JsonArray rivals = new JsonArray();
                JsonArray allies = new JsonArray();

                for(String bandeUUID : bande.getRivals()) {
                    rivals.add(new JsonPrimitive(bandeUUID));
                }
                for(String bandeUUID : bande.getAllies()) {
                    allies.add(new JsonPrimitive(bandeUUID));
                }

                jsonObject.addProperty("bandeID", bande.getBandeID());
                jsonObject.add("membersUUID", members);
                jsonObject.add("rivals", rivals);
                jsonObject.add("allies", allies);
                jsonObject.addProperty("ownerUUID", bande.owner().getUniqueId().toString());
                jsonObject.addProperty("level", bande.getLevel());
                jsonObject.addProperty("kills", bande.getKills());
                jsonObject.addProperty("vagtKills", bande.getVagtKills());
                jsonObject.addProperty("offiKills", bande.getOffiKills());
                jsonObject.addProperty("insKills", bande.getInsKills());
                jsonObject.addProperty("dirKills", bande.getDirKills());
                jsonObject.addProperty("deaths", bande.getDeaths());
                jsonObject.addProperty("bank", bande.getBank());
                jsonObject.addProperty("name", bande.getName());

                return jsonObject;
            }).create();


    public Manager() {
        dbFile = new File(BandePlugin.instance.getDataFolder().toString() + "/Teams.json");
        if(!dbFile.exists()) {
            try {
                if(dbFile.createNewFile()) {
                    List<Bande> teams = new ArrayList<>();
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(UUID.fromString("390f6268-c72e-4206-a8dc-4980cd655845"));
                    OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString("f6c6dd6c-5c9f-44a0-8150-41027ed74722"));
                    HashMap<OfflinePlayer, Integer> map1 = new HashMap<>();
                    HashMap<OfflinePlayer, Integer> map2 = new HashMap<>();
                    map1.put(player1, Bande.PermissionLevel.KINGPIN.getValue());
                    map2.put(player2, Bande.PermissionLevel.KINGPIN.getValue());
                    teams.add(new Bande(map1, player1, new ArrayList<>(), new ArrayList<>(), 1, 0, 0, 0, 0, 0, 0, 0, "TestBande1"));
                    teams.add(new Bande(map2, player2, new ArrayList<>(), new ArrayList<>(), 1, 0, 0, 0, 0, 0, 0, 0, "TestBande2"));
                    saveData(teams);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataArray = loadJsonFromFile();
    }

    public synchronized List<Bande> refreshData() {
        dataArray = loadJsonFromFile();
        Type type = new TypeToken<List<Bande>>() {}.getType();
        return gson.fromJson(dataArray, type);
    }

    public synchronized List<Bande> loadData() {
        Type type = new TypeToken<List<Bande>>() {}.getType();
        return gson.fromJson(dataArray, type);
    }

    public synchronized void saveData(List<Bande> data) {
        if(data == null || data.isEmpty()) return;
        dataArray = advancedGson.toJsonTree(data).getAsJsonArray();
        saveJsonToFile(dataArray);
    }

    public synchronized JsonArray loadJsonFromFile() {
        try (FileReader reader = new FileReader(dbFile)) {
            return gson.fromJson(reader, JsonArray.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    public synchronized <T> void saveJsonToFile(T object) {
        try (FileWriter writer = new FileWriter(dbFile)) {
            String jsonString = prettyGson.toJson(object);
            writer.write(jsonString);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}


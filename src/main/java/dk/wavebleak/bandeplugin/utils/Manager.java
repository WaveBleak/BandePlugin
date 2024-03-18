package dk.wavebleak.bandeplugin.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import dk.wavebleak.bandeplugin.BandePlugin;
import dk.wavebleak.bandeplugin.classes.Bande;
import dk.wavebleak.bandeplugin.classes.BandeBuilder;
import dk.wavebleak.bandeplugin.classes.BandeTerritorie;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class Manager {

    private final File bandeFile;
    private final File territoryFile;
    private JsonArray bandeDataArray;
    private JsonArray territoryDataArray;
    private final Gson gson = new Gson();
    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final Gson advancedBandeGson = new GsonBuilder()
            .registerTypeAdapter(Bande.class, (JsonSerializer<Bande>) (bande, typeOfSrc, context) -> {
                JsonObject jsonObject = new JsonObject();

                JsonElement members = gson.toJsonTree(bande.getMembersUUID());
                JsonArray rivals = new JsonArray();
                JsonArray allies = new JsonArray();

                for(Bande bandeUUID : bande.getRivals()) {
                    rivals.add(new JsonPrimitive(bandeUUID.getBandeID()));
                }
                for(Bande bandeUUID : bande.getAllies()) {
                    allies.add(new JsonPrimitive(bandeUUID.getBandeID()));
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
                jsonObject.addProperty("unlockedTerritory", bande.isUnlockedTerritory());
                jsonObject.addProperty("unlockedHouse", bande.isUnlockedHouse());
                jsonObject.addProperty("allyHitMitigation", bande.getAllyHitMitigation());
                jsonObject.addProperty("rivalKills", bande.getRivalKills());

                return jsonObject;
            }).registerTypeAdapter(new TypeToken<List<Bande>>() {}.getType(), new JsonDeserializer<Bande>() {
                public Bande deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    JsonObject data = json.getAsJsonObject();

                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(data.get("ownerUUID").getAsString()));
                    BandeBuilder builder = new BandeBuilder(owner)
                            .setBandeID(data.get("bandeID").getAsString())
                            .setMembersUUID(gson.fromJson(data.get("membersUUID"), new com.google.common.reflect.TypeToken<HashMap<String, Integer>>() {}.getType()))
                            .setRivals(gson.fromJson(data.get("rivals"), new TypeToken<List<String>>() {}.getType()))
                            .setAllies(gson.fromJson(data.get("allies"), new TypeToken<List<String>>() {}.getType()))
                            .setMembersUUID(gson.fromJson(data.get("membersUUID"), new TypeToken<List<String>>() {
                            }.getType()))
                            .setRivals(gson.fromJson(data.get("rivals"), new TypeToken<List<Bande>>() {
                            }.getType()))
                            .setAllies(gson.fromJson(data.get("allies"), new TypeToken<List<Bande>>() {
                            }.getType()))
                            .setLevel(data.get("level").getAsInt())
                            .setKills(data.get("kills").getAsInt())
                            .setVagtKills(data.get("vagtKills").getAsInt())
                            .setOffiKills(data.get("offiKills").getAsInt())
                            .setInsKills(data.get("insKills").getAsInt())
                            .setDirKills(data.get("dirKills").getAsInt())
                            .setDeaths(data.get("deaths").getAsInt())
                            .setBank(data.get("bank").getAsLong())
                            .setName(data.get("name").getAsString())
                            .setUnlockedTerritory(data.get("unlockedTerritory").getAsBoolean())
                            .setUnlockedHouse(data.get("unlockedHouse").getAsBoolean());

                    if(data.has("allyHitMitigation")) {
                        builder.setAllyHitMitigation(data.get("allyHitMitigation").getAsInt());
                    }
                    if(data.has("rivalKills")) {
                        builder.setRivalKills(data.get("rivalKills").getAsInt());
                    }

                    return builder.create();
                }
            }).create();

    private final Gson advancedTerritoryGson = new GsonBuilder().registerTypeAdapter(BandeTerritorie.class, (JsonSerializer<BandeTerritorie>) (bandeTerritorie, typeOfSrc, context) -> {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("ownedBandeID", bandeTerritorie.getOwnedBandeID());
        jsonObject.addProperty("name", bandeTerritorie.getName());
        jsonObject.addProperty("x", bandeTerritorie.getX());
        jsonObject.addProperty("y", bandeTerritorie.getY());
        jsonObject.addProperty("z", bandeTerritorie.getZ());
        jsonObject.addProperty("world", bandeTerritorie.getWorld().getName());
        jsonObject.addProperty("minBread", bandeTerritorie.getMinBread());
        jsonObject.addProperty("maxBread", bandeTerritorie.getMaxBread());
        jsonObject.addProperty("breadInterval", bandeTerritorie.getBreadInterval());
        jsonObject.addProperty("generatedBread", bandeTerritorie.getGeneratedBread());
        jsonObject.addProperty("doNotTouch", bandeTerritorie.getDoNotTouch());

        return jsonObject;
    }).create();


    public Manager() {
        bandeFile = new File(BandePlugin.instance.getDataFolder().toString() + "/Bander.json");
        territoryFile = new File(BandePlugin.instance.getDataFolder().toString() + "/Territories.json");
        if(!bandeFile.exists()) {
            try {
                if(bandeFile.createNewFile()) {
                    List<Bande> bander = new ArrayList<>();
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(UUID.fromString("390f6268-c72e-4206-a8dc-4980cd655845"));
                    OfflinePlayer player2 = Bukkit.getOfflinePlayer(UUID.fromString("f6c6dd6c-5c9f-44a0-8150-41027ed74722"));
                    bander.add(new BandeBuilder(player1).setName("TestBande1").create());
                    bander.add(new BandeBuilder(player2).setName("TestBande2").create());

                    saveBande(bander);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!territoryFile.exists()) {
            try {
                if(territoryFile.createNewFile()) {
                    List<BandeTerritorie> territories = new ArrayList<>();
                    territories.add(new BandeTerritorie("", "test1", 10, 20, 30, "world", 5, 10, 60, 10, 0));
                    territories.add(new BandeTerritorie("", "test2", 40, 50, 60, "world", 15, 20, 120, 10, 0));

                    saveTerritory(territories);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        bandeDataArray = loadJsonFromFile(bandeFile);
        territoryDataArray = loadJsonFromFile(territoryFile);
    }

    public synchronized List<Bande> refreshBande() {
        bandeDataArray = loadJsonFromFile(bandeFile);
        Type type = new TypeToken<List<Bande>>() {}.getType();

        return gson.fromJson(bandeDataArray, type);
    }

    public synchronized List<BandeTerritorie> refreshTerrirtory() {
        territoryDataArray = loadJsonFromFile(territoryFile);
        Type type = new TypeToken<List<BandeTerritorie>>() {}.getType();
        return gson.fromJson(territoryDataArray, type);
    }

    public synchronized List<BandeTerritorie> loadTerritories() {
        Type type = new TypeToken<List<BandeTerritorie>>() {}.getType();
        return gson.fromJson(territoryDataArray, type);
    }

    public synchronized List<Bande> loadBande() {
        Type type = new TypeToken<List<Bande>>() {}.getType();
        return gson.fromJson(bandeDataArray, type);
    }

    public synchronized void saveBande(List<Bande> data) {
        if(data == null || data.isEmpty()) return;
        bandeDataArray = advancedBandeGson.toJsonTree(data).getAsJsonArray();
        saveJsonToFile(bandeDataArray, bandeFile);
    }
    public synchronized void saveTerritory(List<BandeTerritorie> data) {
        if(data == null || data.isEmpty()) return;
        territoryDataArray = advancedTerritoryGson.toJsonTree(data).getAsJsonArray();
        saveJsonToFile(territoryDataArray, territoryFile);
    }

    public synchronized JsonArray loadJsonFromFile(File dbFile) {
        try (FileReader reader = new FileReader(dbFile)) {
            return gson.fromJson(reader, JsonArray.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    public synchronized <T> void saveJsonToFile(T object, File dbFile) {
        try (FileWriter writer = new FileWriter(dbFile)) {
            String jsonString = prettyGson.toJson(object);
            writer.write(jsonString);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}


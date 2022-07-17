package xyz.hellocraft.chatSync.mc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import xyz.hellocraft.chatSync.ChatSync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleConfig {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("ChatSync.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static SimpleConfig INSTANCE;
    private static Map configMap;

    public SimpleConfig() {


        if (!Files.isRegularFile(CONFIG_FILE)) {
            ChatSync.LOGGER.info("Config file Not Found...");
            configMap = new HashMap();
            configMap.put("token", "none");
            return;
        }


        try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE)) {
            configMap = GSON.fromJson(reader, Map.class);
            if (configMap.getOrDefault("token", "none").equals("none")) {
                put("token", "please insert your token here.");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static SimpleConfig getInstance() {

        ChatSync.LOGGER.info(INSTANCE == null ? "Loading config..." : "Reloading config...");

        if (INSTANCE == null) {
            INSTANCE = new SimpleConfig();
        }

        writeFile();
        return INSTANCE;
    }

    public static void saveConfig() {
        if (INSTANCE != null) {
            writeFile();
        }
    }


    private static void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
            GSON.toJson(configMap, writer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public Object get(String key) {
        if (key.equals("token")) {
            String ret = String.valueOf(configMap.getOrDefault("token", "none"));
            if (Objects.equals(ret, "none")) {
                put("token", "none");
            }
            return ret;
        }
        return configMap.get(key);
    }

    public void put(String key, Object value) {
        configMap.put(key, value);
        saveConfig();
    }


}
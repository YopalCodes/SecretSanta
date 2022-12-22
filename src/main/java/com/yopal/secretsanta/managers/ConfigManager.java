package com.yopal.secretsanta.managers;

import com.yopal.secretsanta.Secretsanta;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    private static FileConfiguration config;

    public static void setupConfig(Secretsanta santa) {
        ConfigManager.config = santa.getConfig();
        santa.saveDefaultConfig();
    }

    public static void addPlayer(Secretsanta santa, Player player) {
        if (config.contains("players")) {

            // player already has joined
            List<String> list = config.getStringList("players");
            list.add(player.getUniqueId() + "");
            config.set("players", list);
        } else {

            // first player joins
            List<String> list = new ArrayList<>();
            list.add(player.getUniqueId() + "");
            config.set("players", list);
        }

       santa.saveConfig();

    }

    public static void removePlayer(Secretsanta santa, Player player) {
        List<String> list = config.getStringList("players");
        list.remove(player.getUniqueId() + "");
        config.set("players", list);
        santa.saveConfig();
    }

    public static ArrayList<Player> getAllPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (String playerUUID : config.getStringList("players")) {
            players.add(Bukkit.getPlayer(UUID.fromString(playerUUID)));
        }
        return players;
    }

    public static ArrayList<Location> returnBattleLocations(World world) {
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            locations.add(new Location(
                    world,
                    config.getDouble("SnowballLocations.Spawn" + i + ".x"),
                    config.getDouble("SnowballLocations.Spawn" + i + ".y"),
                    config.getDouble("SnowballLocations.Spawn" + i + ".z"))
            );

        }

        return locations;
    }

    public static ArrayList<Location> returnParkourLocations(World world) {
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 1; i < 26; i++) {
            locations.add(new Location(
                    world,
                    config.getDouble("ParkourLocations.CheckPoint" + i + ".x"),
                    config.getDouble("ParkourLocations.CheckPoint" + i + ".y"),
                    config.getDouble("ParkourLocations.CheckPoint" + i + ".z"))
            );

        }

        return locations;
    }

    public static Location returnLocation(String path) {
        return new Location(
                Bukkit.getWorld(config.getString("Locations." + path + ".world")),
                config.getDouble("Locations." + path + ".x"),
                config.getDouble("Locations." + path + ".y"),
                config.getDouble("Locations." + path + ".z")
        );
    }


    public static void reloadConfig(Secretsanta santa) { santa.reloadConfig(); config = santa.getConfig();}
}

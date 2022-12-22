package com.yopal.secretsanta.games;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.listener.ParkourListener;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.GameControl;
import com.yopal.secretsanta.utility.Countdown;
import com.yopal.secretsanta.utility.GameScoreboard;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Parkour {

    private Secretsanta santa;
    private Countdown countdown;
    private Location location;
    private BukkitTask task;
    private HashMap<UUID, Integer> score;

    public Parkour(Secretsanta santa) {
        this.santa = santa;
        this.score = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new ParkourListener(santa, this), santa);

        // creating world

        FileConfiguration config = santa.getConfig();
        World world = Bukkit.createWorld(new WorldCreator(config.getString("Locations.ParkourLocation.world")));
        world.setAutoSave(false);

        Location location = ConfigManager.returnLocation("ParkourLocation");
        location.setWorld(world);
        this.location = location;

        PlayerInteract.sendToAll("Have fun y'all! You guys deserve the day off.", UtilTypes.MESSAGE);

        for (Player player : ConfigManager.getAllPlayers()) {

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            // clearing inventories of snowballs
            player.getInventory().clear();

            // setting scoreboard
            GameScoreboard.setScoreboard(player, "Parkour");

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 24000, 3, true, true));
            player.teleport(ConfigManager.returnLocation("ParkourLocation"));
        }

        this.countdown = new Countdown(santa, 1200, this);
        countdown.start();

    }

    public void addPoint(Player player, int points) {
        if (!score.containsKey(player.getUniqueId())) {
            score.put(player.getUniqueId(), points);

            // update scoreboard
            GameScoreboard.updateScoreboard(player, score);
        } else {
            score.put(player.getUniqueId(), score.get(player.getUniqueId()) + points);

            // update scoreboard
            GameScoreboard.updateScoreboard(player, score);
        }

        if (score.get(player.getUniqueId()) == 25) {
            PlayerInteract.sendMessage(player, player.getName() + " has finished the parkour!");
        }
    }

    public void finish() {
        // wait 30 seconds for next match, teleport players, then reset the map

        PlayerInteract.sendToAll("It's time for SECRET SANTAAA", UtilTypes.MESSAGE);

        int max = Collections.max(score.values());
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : score.entrySet()) {
            if (entry.getValue() == max) {
                uuids.add(entry.getKey());
            }
        }

        for (UUID uuid : uuids) {
            GameControl.addGlobalPoint(uuid);
            PlayerInteract.sendToAll("Congratulations " + Bukkit.getPlayer(uuid).getName()  + "! You've received one Santa Sticker for your awesome parkour skills :)", UtilTypes.MESSAGE);
        }

        AtomicInteger countdownSeconds = new AtomicInteger(30);
        task = Bukkit.getScheduler().runTaskTimer(santa, () -> {
            if (countdownSeconds.get() == 0) {

                // resetting map
                task.cancel();
                GameControl.endGame();

                Bukkit.unloadWorld(location.getWorld(), false);
                World world = Bukkit.createWorld(new WorldCreator(location.getWorld().getName()));
                world.setAutoSave(false);

            }

            if (countdownSeconds.get() == 5) {
                for (Player playerGiveEffect : ConfigManager.getAllPlayers()) {
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true, true));
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1, true, true));
                }
            }

            if (countdownSeconds.get() <= 10 && countdownSeconds.get() != 0) {
                PlayerInteract.sendToAll("Game starting in " + countdownSeconds, UtilTypes.MESSAGE);
            }

            PlayerInteract.sendToAll(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds.get() == 1 ? "" : "s"), ChatColor.GRAY + "until SECRET SANTA starts", UtilTypes.TITLEWITHSUB);

            countdownSeconds.getAndDecrement();
        }, 100, 20);
    }
}

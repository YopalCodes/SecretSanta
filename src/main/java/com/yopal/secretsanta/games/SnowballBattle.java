package com.yopal.secretsanta.games;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.listener.PresentsListener;
import com.yopal.secretsanta.listener.SnowballListener;
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

public class SnowballBattle {
    private Secretsanta santa;
    private Countdown countdown;
    private Location location;
    private BukkitTask task;
    private HashMap<UUID, Integer> score;

    public SnowballBattle(Secretsanta santa) {
        this.santa = santa;
        this.score = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new SnowballListener(santa, this), santa);

        for (Player player : Bukkit.getOnlinePlayers()) {

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            // setting scoreboard
            GameScoreboard.setScoreboard(player, "SnowballBattle");

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 12600, 3, true, true));
            player.teleport(ConfigManager.returnLocation("SnowballBattleLocation"));
            player.setAllowFlight(true);
        }

        // creating world
        FileConfiguration config = santa.getConfig();
        World world = Bukkit.createWorld(new WorldCreator(config.getString("Locations.SnowballBattleLocation.world")));
        world.setAutoSave(false);

        Location location = ConfigManager.returnLocation("SnowballBattleLocation");
        location.setWorld(world);
        this.location = location;


        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(ConfigManager.returnLocation("SnowballBattleLocation"));
        }


        PlayerInteract.sendToAll("As a reward of finding the scattered presents, let's play a classic game of snowball fight! There's double jump and snowballs are one hit kills. Obtain snowballs by sneaking on snow blocks. Try not to get hit! Ho ho ho!", UtilTypes.MESSAGE);
        this.countdown = new Countdown(santa, 600, this);
        countdown.start();



    }

    public HashMap<UUID, Integer> getScore() {
        return score;
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
    }

    public void finish() {
        // wait 30 seconds for next match, teleport players, then reset the map

        PlayerInteract.sendToAll("Alright guys lets get back to work soon! I hope you had fun!", UtilTypes.MESSAGE);

        int max = Collections.max(score.values());
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : score.entrySet()) {
            if (entry.getValue() == max) {
                uuids.add(entry.getKey());
            }
        }

        for (UUID uuid : uuids) {
            GameControl.addGlobalPoint(uuid);
            PlayerInteract.sendToAll("Congratulations " + Bukkit.getPlayer(uuid).getName()  + "! You've received one Santa Sticker for your killing rampage :)", UtilTypes.MESSAGE);
        }

        GameControl.getLead();

        AtomicInteger countdownSeconds = new AtomicInteger(30);
        task = Bukkit.getScheduler().runTaskTimer(santa, () -> {
            if (countdownSeconds.get() == 0) {

                // resetting map
                task.cancel();
                GameControl.startGame(GameTypes.ELFOFTHEMONTH);

                Bukkit.unloadWorld(location.getWorld(), false);
                World world = Bukkit.createWorld(new WorldCreator(location.getWorld().getName()));
                world.setAutoSave(false);

            }

            if (countdownSeconds.get() == 5) {
                for (Player playerGiveEffect : Bukkit.getOnlinePlayers()) {
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true, true));
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1, true, true));
                }
            }

            if (countdownSeconds.get() <= 10 && countdownSeconds.get() != 0) {
                PlayerInteract.sendToAll("Game starting in " + countdownSeconds, UtilTypes.MESSAGE);
            }

            PlayerInteract.sendToAll(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds.get() == 1 ? "" : "s"), ChatColor.GRAY + "until elf of the month starts", UtilTypes.TITLEWITHSUB);

            countdownSeconds.getAndDecrement();
        }, 100, 20);
    }
}

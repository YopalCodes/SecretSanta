package com.yopal.secretsanta.games;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.listener.PresentsListener;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.GameControl;
import com.yopal.secretsanta.utility.Countdown;
import com.yopal.secretsanta.utility.GameScoreboard;
import com.yopal.secretsanta.utility.PlayerInteract;
import com.yopal.secretsanta.enums.UtilTypes;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FindThePresents {
    private Countdown countdown;
    private Secretsanta santa;
    private HashMap<UUID, Integer> score;
    private BukkitTask task;
    private Location location;

    public FindThePresents(Secretsanta santa) {
        this.santa = santa;
        this.score = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new PresentsListener(santa, this), santa);

        // creating world

        FileConfiguration config = santa.getConfig();
        World world = Bukkit.createWorld(new WorldCreator(config.getString("Locations.FindPresentLocation.world")));
        world.setAutoSave(false);

        Location location = ConfigManager.returnLocation("FindPresentLocation");
        location.setWorld(world);
        this.location = location;

        for (Player player : Bukkit.getOnlinePlayers()) {

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            // setting scoreboard
            GameScoreboard.setScoreboard(player, "FindThePresents");

            player.teleport(location);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12100, 2, true, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 12600, 3, true, true));
        }

        PlayerInteract.sendToAll("The Grinch has scattered all of my presents! Please find them so I can deliver them when Christmas comes, which will be in 10 minutes!", UtilTypes.MESSAGE);
        this.countdown = new Countdown(santa, 600, this);
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
    }

    public HashMap<UUID, Integer> getScore() {
        return score;
    }

    public void finish() {
            // wait 30 seconds for next match, teleport players, then reset the map

            PlayerInteract.sendToAll("Goooood work everyone! It seems we will continue to have another Christmas this year!", UtilTypes.MESSAGE);

            int max = Collections.max(score.values());
            List<UUID> uuids = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : score.entrySet()) {
                if (entry.getValue() == max) {
                    uuids.add(entry.getKey());
                }
            }

            for (UUID uuid : uuids) {
                GameControl.addGlobalPoint(uuid);
                PlayerInteract.sendToAll("Congratulations " + Bukkit.getPlayer(uuid).getName()  + "! You've received one Santa Sticker for your awesome work of collecting presents.", UtilTypes.MESSAGE);
            }

            GameControl.getLead();

            AtomicInteger countdownSeconds = new AtomicInteger(30);
            task = Bukkit.getScheduler().runTaskTimer(santa, () -> {
                if (countdownSeconds.get() == 0) {

                    // resetting map
                    task.cancel();
                    GameControl.startGame(GameTypes.SNOWBALLBATTLE);

                    Bukkit.unloadWorld(location.getWorld(), false);
                    World world = Bukkit.createWorld(new WorldCreator(location.getWorld().getName()));
                    world.setAutoSave(false);

                }

                if (countdownSeconds.get() == 5) {
                    for (Player playerGiveEffect : Bukkit.getOnlinePlayers()) {
                        playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true, true));
                        playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100,  1, true, true));
                    }
                }

                if (countdownSeconds.get() <= 10 && countdownSeconds.get() != 0) {
                    PlayerInteract.sendToAll("Game starting in " + countdownSeconds, UtilTypes.MESSAGE);
                }

                PlayerInteract.sendToAll(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds.get() == 1 ? "" : "s"), ChatColor.GRAY + "until snow battle starts", UtilTypes.TITLEWITHSUB);

                countdownSeconds.getAndDecrement();
            }, 100, 20);



    }

}

package com.yopal.secretsanta.managers;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.games.ElfOfTheMonth;
import com.yopal.secretsanta.games.FindThePresents;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.games.Parkour;
import com.yopal.secretsanta.games.SnowballBattle;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameControl {

    private static HashMap<UUID, Integer> playerScores = new HashMap<>();

    private static Secretsanta santa;
    private static BukkitTask task;

    public static void setupGame(Secretsanta santa) {
        GameControl.santa = santa;
        GameControl.playerScores = new HashMap<>();
    }

    public static void startGame(GameTypes type) {
        switch (type) {
            case FINDPRESENTS:
                FindThePresents game1 = new FindThePresents(santa);
                break;
            case SNOWBALLBATTLE:
                SnowballBattle game2 = new SnowballBattle(santa);
                break;
            case ELFOFTHEMONTH:
                ElfOfTheMonth game3 = new ElfOfTheMonth(santa);
                break;
            case PARKOUR:
                Parkour game4 = new Parkour(santa);
                break;
        }

    }

    public static void getLead() {
        Bukkit.getScheduler().runTaskLater(santa, () -> {
            int max = Collections.max(playerScores.values());
            List<UUID> uuids = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : playerScores.entrySet()) {
                if (entry.getValue() == max) {
                    uuids.add(entry.getKey());
                }
            }

            for (UUID uuid : uuids) {

                PlayerInteract.sendToAll(ChatColor.GOLD.toString() + ChatColor.BOLD + Bukkit.getPlayer(uuid).getName() + ChatColor.GRAY + " is currently in the lead with " + ChatColor.LIGHT_PURPLE + playerScores.get(uuid) + " Santa Sticker(s)", UtilTypes.MESSAGE);
            }

        }, 100);
    }

    public static void endGame() {

        for (Player player : ConfigManager.getAllPlayers()) {
            player.teleport(ConfigManager.returnLocation("EndLocation"));
        }

        Bukkit.getScheduler().runTaskLater(santa, () -> {
            int max = Collections.max(playerScores.values());
            List<UUID> uuids = new ArrayList<>();
            for (Map.Entry<UUID, Integer> entry : playerScores.entrySet()) {
                if (entry.getValue() == max) {
                    uuids.add(entry.getKey());
                }
            }

            AtomicInteger shootTotal = new AtomicInteger();
            for (UUID uuid : uuids) {
                Player player = Bukkit.getPlayer(uuid);

                task = Bukkit.getScheduler().runTaskTimer(santa, ()->{

                    if (shootTotal.get() > 5) {
                        task.cancel();
                    }

                    Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.LIME).with(FireworkEffect.Type.STAR).build());
                    fireworkMeta.setPower(2);
                    firework.setFireworkMeta(fireworkMeta);

                    shootTotal.getAndIncrement();
                },0, 20);


                PlayerInteract.sendToAll("Congratulations " + player.getName()  + "! You've won the Secret Santa!", UtilTypes.MESSAGE);
            }

        }, 100);
    }

    public static void addGlobalPoint(UUID uuid) {
        if (!playerScores.containsKey(uuid)) {
            playerScores.put(uuid, 1);
        } else {
            playerScores.put(uuid, playerScores.get(uuid) + 1);
        }

    }
    public static HashMap<UUID, Integer> getPlayerScores() { return playerScores;}

}

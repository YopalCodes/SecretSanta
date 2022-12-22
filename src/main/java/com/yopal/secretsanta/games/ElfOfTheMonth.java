package com.yopal.secretsanta.games;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.listener.ElfOfMonthListener;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.GameControl;
import com.yopal.secretsanta.utility.Countdown;
import com.yopal.secretsanta.utility.ElfOfMonthItems;
import com.yopal.secretsanta.utility.GameScoreboard;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ElfOfTheMonth {

    private Countdown countdown;
    private Secretsanta santa;
    private Location location;

    private HashMap<UUID, Integer> score;

    private HashMap<UUID, Material> giftTasks;
    private BukkitTask task;

    public ElfOfTheMonth(Secretsanta santa) {
        this.santa = santa;
        this.score = new HashMap<>();
        this.giftTasks = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new ElfOfMonthListener(santa, this), santa);

        // creating world

        FileConfiguration config = santa.getConfig();
        World world = Bukkit.createWorld(new WorldCreator(config.getString("Locations.ElfOfTheMonthLocation.world")));
        world.setAutoSave(false);

        Location location = ConfigManager.returnLocation("ElfOfTheMonthLocation");
        location.setWorld(world);
        this.location = location;

        PlayerInteract.sendToAll("We need to start making new presents for next Christmas. You've been given your tools: a pickaxe, a shovel, an axe, your handy-dandy crafter, and a grappling hook for the Floating Mines. Remember that you can smelt by shifting right clicking with an ore by using your magic powers. Left-click with the present made in order to turn it in and receive your next task. Be the elf of the month, good luck everyone!", UtilTypes.MESSAGE);

        for (Player player : ConfigManager.getAllPlayers()) {

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            // clearing inventories of snowballs
            player.getInventory().clear();

            // giving every player a new gift task
            newGiftTask(player);

            // giving players their items
            for (ItemStack item : ElfOfMonthItems.returnItems()) {
                player.getInventory().addItem(item);
            }

            // setting scoreboard
            GameScoreboard.setScoreboard(player, "ElfOfTheMonth");

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12100, 3, true, true));
            player.teleport(ConfigManager.returnLocation("ElfOfTheMonthLocation"));
        }

        this.countdown = new Countdown(santa, 600, this);
        countdown.start();

    }

    public void newGiftTask(Player player) {
        Random random = new Random();
        ArrayList<Material> gifts = ElfOfMonthItems.returnGifts();
        Material randomGift = gifts.get(random.nextInt(gifts.size()));
        giftTasks.put(player.getUniqueId(), randomGift);
        PlayerInteract.sendMessage(player, "You need to make a " + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + randomGift.name());
    }

    public boolean checkGiftTask(Player player, Material gift) {
        if (giftTasks.get(player.getUniqueId()) == gift) {
            return true;
        } else {
            return false;
        }
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

        PlayerInteract.sendToAll("AMAZING, LOVE THE EFFORT YOU GUYS PUT IN!!! You guys can take the day off, have some fun! I highly recommend this parkour place I found...", UtilTypes.MESSAGE);

        int max = Collections.max(score.values());
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : score.entrySet()) {
            if (entry.getValue() == max) {
                uuids.add(entry.getKey());
            }
        }

        for (UUID uuid : uuids) {
            GameControl.addGlobalPoint(uuid);
            PlayerInteract.sendToAll("Congratulations " + Bukkit.getPlayer(uuid).getName()  + "! You've received one Santa Sticker for your awesome work of making presents.", UtilTypes.MESSAGE);
        }

        GameControl.getLead();

        AtomicInteger countdownSeconds = new AtomicInteger(30);
        task = Bukkit.getScheduler().runTaskTimer(santa, () -> {
            if (countdownSeconds.get() == 0) {

                // resetting map
                task.cancel();
                GameControl.startGame(GameTypes.PARKOUR);

                Bukkit.unloadWorld(location.getWorld(), false);
                World world = Bukkit.createWorld(new WorldCreator(location.getWorld().getName()));
                world.setAutoSave(false);

            }

            if (countdownSeconds.get() == 5) {
                for (Player playerGiveEffect : ConfigManager.getAllPlayers()) {
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true, true));
                    playerGiveEffect.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100,  1, true, true));
                }
            }

            if (countdownSeconds.get() <= 10 && countdownSeconds.get() != 0) {
                PlayerInteract.sendToAll("Game starting in " + countdownSeconds, UtilTypes.MESSAGE);
            }

            PlayerInteract.sendToAll(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds.get() == 1 ? "" : "s"), ChatColor.GRAY + "until parkour starts", UtilTypes.TITLEWITHSUB);

            countdownSeconds.getAndDecrement();
        }, 100, 20);



    }

}

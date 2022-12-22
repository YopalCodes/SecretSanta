package com.yopal.secretsanta.command;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.GameControl;
import com.yopal.secretsanta.utility.PlayerInteract;
import com.yopal.secretsanta.enums.UtilTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SantaCommand implements CommandExecutor {

    private Secretsanta santa;
    private BukkitTask task;

    public SantaCommand(Secretsanta santa) {this.santa = santa;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!sender.hasPermission("santa.admin")) {
                PlayerInteract.sendMessage(player, "You don't have permission! Don't worry, at least you're not on the naughty list ;)");
                return false;
            }

            if (args.length == 1) {
                switch (args[0]) {
                    case "start":

                        PlayerInteract.sendToAll("Ho ho ho! Our secret santa will begin soon!", UtilTypes.MESSAGE);

                        AtomicInteger countdownSeconds = new AtomicInteger(30);
                        task = Bukkit.getScheduler().runTaskTimer(santa, () -> {
                            if (countdownSeconds.get() == 0) {
                                task.cancel();
                                GameControl.startGame(GameTypes.FINDPRESENTS);
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

                            PlayerInteract.sendToAll(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds.get() == 1 ? "" : "s"), ChatColor.GRAY + "until find the presents starts", UtilTypes.TITLEWITHSUB);

                            countdownSeconds.getAndDecrement();
                        }, 0, 20);
                        break;
                }
            }


        }
        return false;
    }
}

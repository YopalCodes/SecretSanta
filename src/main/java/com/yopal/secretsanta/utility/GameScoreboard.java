package com.yopal.secretsanta.utility;

import com.yopal.secretsanta.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class GameScoreboard {

    public static void setScoreboard(Player player, String gameType) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("santaBoard", "dummy");

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Secret" + ChatColor.GREEN + ChatColor.BOLD + " Santa");

        Score credit = obj.getScore(ChatColor.GRAY.toString() + ChatColor.ITALIC + "From yours truly, Yopal");
        credit.setScore(1);

        Score space1 = obj.getScore("");
        space1.setScore(2);

        Team playerScore = board.registerNewTeam("playerScore");
        playerScore.addEntry(ChatColor.GREEN.toString());
        playerScore.setPrefix(ChatColor.YELLOW + player.getName() + ": ");
        playerScore.setSuffix(ChatColor.GRAY + "0");
        obj.getScore(ChatColor.GREEN.toString()).setScore(3);

        Team inLead = board.registerNewTeam("inLead");
        inLead.addEntry(ChatColor.RED.toString());
        inLead.setPrefix(ChatColor.YELLOW + "No One: ");
        inLead.setSuffix(ChatColor.GRAY + "0");
        obj.getScore(ChatColor.RED.toString()).setScore(4);

        Score points = obj.getScore(ChatColor.GOLD.toString() + ChatColor.BOLD + "POINTS:");
        points.setScore(5);

        Score space2 = obj.getScore(" ");
        space2.setScore(6);

        Score game = obj.getScore(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Game: " + ChatColor.RESET + ChatColor.GRAY + gameType);
        game.setScore(7);

        player.setScoreboard(board);

    }

    public static void updateScoreboard(Player player, HashMap<UUID, Integer> score) {
        player.getScoreboard().getTeam("playerScore").setSuffix(ChatColor.GRAY + score.get(player.getUniqueId()).toString());

        int max = Collections.max(score.values());
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : score.entrySet()) {
            if (entry.getValue() == max) {
                uuids.add(entry.getKey());
            }
        }


        Random random = new Random();
        Player randomPlayer = Bukkit.getPlayer(uuids.get(random.nextInt(uuids.size())));
        for (Player gamePlayer : Bukkit.getOnlinePlayers()) {
            gamePlayer.getScoreboard().getTeam("inLead").setPrefix(ChatColor.YELLOW + randomPlayer.getName() + ": ");
            gamePlayer.getScoreboard().getTeam("inLead").setSuffix(ChatColor.GRAY + score.get(randomPlayer.getUniqueId()).toString());
        }



    }


}

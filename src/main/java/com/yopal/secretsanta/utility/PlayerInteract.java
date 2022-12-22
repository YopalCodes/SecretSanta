package com.yopal.secretsanta.utility;

import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerInteract {

    private static String prefix = ChatColor.GREEN + "[" + ChatColor.RED + "Santa" + ChatColor.GREEN  + "] " + ChatColor.GRAY;

    public static void sendMessage(Player player, String message) {
        player.sendMessage(prefix + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
    }

    public static void sendToAll(String message, String secondMesssage, UtilTypes type) {
        switch (type) {
            case TITLE:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendTitle(player, message);
                }
            case TITLEWITHSUB:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendTitle(player, message, secondMesssage);
                }
        }


    }

    public static void sendToAll(String message, UtilTypes type) {
        switch (type) {
            case MESSAGE:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendMessage(player, message);
                }
        }


    }

    public static void sendTitle(Player player, String title) {
        player.sendTitle(title, "");
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle);
    }

}

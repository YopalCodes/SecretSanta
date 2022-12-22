package com.yopal.secretsanta.listener;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.MusicManager;
import com.yopal.secretsanta.utility.GameScoreboard;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

    private Secretsanta santa;

    public ServerListener(Secretsanta santa) {this.santa = santa;}

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        e.setMaxPlayers(420);
        e.setMotd(ChatColor.GOLD.toString() + ChatColor.BOLD + "BREADGANG" + ChatColor.RED + ChatColor.BOLD + " Secret" + ChatColor.GREEN + ChatColor.BOLD + " Santa");
    }

    @EventHandler
    public void playerOnJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.teleport(ConfigManager.returnLocation("StartLocation"));

        PlayerInteract.sendMessage(player, "Hello! Welcome to the first Bread Gang Secret Santa! Ho ho ho!");
        ConfigManager.addPlayer(santa, player);

        MusicManager.playSong(e.getPlayer());
        GameScoreboard.setScoreboard(e.getPlayer(), "None");
    }

    @EventHandler
    public void playerOnLeave(PlayerQuitEvent e) {
        ConfigManager.removePlayer(santa, e.getPlayer());
    }


}

package com.yopal.secretsanta.listener;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.games.Parkour;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ParkourListener implements Listener {

    private Secretsanta santa;
    private Parkour parkour;
    private HashMap<UUID, ArrayList<Location>> playerCheckPoints;

    public ParkourListener(Secretsanta santa, Parkour parkour) {
        this.santa = santa;
        this.parkour = parkour;
        this.playerCheckPoints = new HashMap<>();
    }

    public boolean checkWorld(World world) {
        FileConfiguration config = santa.getConfig();
        if (world.getName().equals(Bukkit.getWorld(config.getString("Locations.ParkourLocation.world")).getName())) {
            return true;
        } else {
            return false;
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        // detecting when player is in one of the check point spots and checks if it has not already been inside the spot
        Location blockLocation = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        if (ConfigManager.returnParkourLocations(player.getWorld()).contains(blockLocation)) {

            ArrayList<Location> checkPointList;
            if (!playerCheckPoints.containsKey(player.getUniqueId())) {
                checkPointList = new ArrayList<>();
            } else {
                if (playerCheckPoints.get(player.getUniqueId()).contains(blockLocation)) {
                    return;
                }
                checkPointList = playerCheckPoints.get(player.getUniqueId());
            }

            checkPointList.add(blockLocation);
            parkour.addPoint(player, 1);
            playerCheckPoints.put(player.getUniqueId(), checkPointList);

            PlayerInteract.sendMessage(e.getPlayer(), "+1 Point!");
        }
    }

    @EventHandler
    public void playerHit(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!checkWorld(e.getEntity().getWorld())) {
                return;
            }

            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                e.setCancelled(true);
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        PlayerInteract.sendMessage(e.getPlayer(), "The parkour owner will kick you out if you keep breaking things!");
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        e.setCancelled(true);

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        PlayerInteract.sendMessage(e.getPlayer(), "The parkour owner will kick you out if you keep placing things!");
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        e.setCancelled(true);
    }
}

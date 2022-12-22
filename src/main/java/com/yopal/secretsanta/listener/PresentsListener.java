package com.yopal.secretsanta.listener;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.games.FindThePresents;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

public class PresentsListener implements Listener {

    private FindThePresents presents;
    private Secretsanta santa;
    public PresentsListener(Secretsanta santa, FindThePresents presents) {
        this.santa = santa;
        this.presents = presents;
    }

    public boolean checkWorld(World world) {
        FileConfiguration config = santa.getConfig();
        if (world.getName().equals(Bukkit.getWorld(config.getString("Locations.FindPresentLocation.world")).getName())) {
            return true;
        } else {
            return false;
        }
    }

    @EventHandler
    public void playerRightClick(PlayerInteractEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.PLAYER_HEAD)) {

                e.getPlayer().spawnParticle(Particle.CLOUD, e.getClickedBlock().getLocation(), 10, 0.5, 0.5, 0.5);
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                e.getClickedBlock().setType(Material.AIR);

                presents.addPoint(e.getPlayer(), 1);

                PlayerInteract.sendMessage(e.getPlayer(), "+1 Point!");
            }
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

        PlayerInteract.sendMessage(e.getPlayer(), "It's not a good idea to damage the town!");
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        e.setCancelled(true);

    }
}

package com.yopal.secretsanta.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.games.SnowballBattle;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SnowballListener implements Listener {

    private Secretsanta santa;
    private SnowballBattle battle;

    private Cache<UUID, Long> doubleJumpCooldown = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();

    public SnowballListener(Secretsanta santa, SnowballBattle battle) {
        this.battle = battle;
        this.santa = santa;

        // Constantly check if player is sneaking over snowblocks
        Bukkit.getScheduler().runTaskTimer(santa, ()->{
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (checkWorld(player.getWorld())) {
                    Location playerLoc = player.getLocation();
                    if (playerLoc.subtract(0, 1, 0).getBlock().getType().equals(Material.SNOW_BLOCK) && player.isSneaking()) {
                        player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                }

            }
        },0,10);


    }

    public boolean checkWorld(World world) {
        FileConfiguration config = santa.getConfig();
        if (world.getName().equals(Bukkit.getWorld(config.getString("Locations.SnowballBattleLocation.world")).getName())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkCooldown(Player player) {
        if (!doubleJumpCooldown.asMap().containsKey(player.getUniqueId())) {
            doubleJumpCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
            return true;
        } else {
            return false;
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
    public void onDoubleJump(PlayerToggleFlightEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        Player player = e.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
            if (checkCooldown(player)) {
                player.setAllowFlight(false);

                Vector vector = player.getLocation().getDirection().multiply(1).setY(1);
                player.setVelocity(vector);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 10, .5, .5, .5, 0.05F);

                Bukkit.getScheduler().runTaskLater(santa, ()-> {
                    player.setAllowFlight(true);
                }, 65);
            }
        }
    }


    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!checkWorld(e.getEntity().getWorld())) {
                return;
            }
        }

        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            if (e.getDamager().getType().equals(EntityType.SNOWBALL)) {
                Player player = (Player) e.getEntity();



                Projectile snowball = (Projectile) e.getDamager();
                if (snowball.getShooter() instanceof Player) {
                    Player killer = (Player) snowball.getShooter();

                    // skip if player killer is themself
                    if (player.getName() == killer.getName()) {
                        return;
                    }

                    killer.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 2);

                    battle.addPoint(killer, 1);
                    // Choose a random death message
                    randomDeathMessage(killer.getName(), player.getName());
                }
                player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 10, 0.05, 0.05, 0.05);

                Random random = new Random();
                ArrayList<Location> battleLocations = ConfigManager.returnBattleLocations(player.getWorld());
                player.teleport(battleLocations.get(random.nextInt(battleLocations.size())));
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        PlayerInteract.sendMessage(e.getPlayer(), "You can fight, not break things!");
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        e.setCancelled(true);

    }

    public void randomDeathMessage(String killerName, String victimName) {
        Random random = new Random();
        ArrayList<String> deathMessages = new ArrayList<>();

        deathMessages.add(killerName + " has obliterated " + victimName + "'s whole career");
        deathMessages.add(killerName + " put " + victimName + " on the Naughty List");
        deathMessages.add(victimName + " was eradicated by " + killerName + "'s snowball");
        deathMessages.add(killerName + " sent " + victimName + " into a Hypixel game");
        deathMessages.add(killerName + " helped " + victimName + " discover the definition of death");
        deathMessages.add(victimName + " cried after getting hit by " + killerName + "'s snowball");

        PlayerInteract.sendToAll(deathMessages.get(random.nextInt(deathMessages.size())), UtilTypes.MESSAGE);
    }
}

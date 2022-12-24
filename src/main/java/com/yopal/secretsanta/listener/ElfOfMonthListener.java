package com.yopal.secretsanta.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.games.ElfOfTheMonth;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.utility.ElfOfMonthItems;
import com.yopal.secretsanta.utility.PlayerInteract;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ElfOfMonthListener implements Listener {

    private Secretsanta santa;
    private ElfOfTheMonth elfOfMonth;

    private Cache<UUID, Long> grappleHookCooldown = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    public ElfOfMonthListener(Secretsanta santa, ElfOfTheMonth elfOfMonth) {
        this.santa = santa;
        this.elfOfMonth = elfOfMonth;
    }

    public boolean checkCooldown(Player player) {
        if (!grappleHookCooldown.asMap().containsKey(player.getUniqueId())) {
            grappleHookCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
            return true;

        } else {

            long distance = grappleHookCooldown.asMap().get(player.getUniqueId()) - System.currentTimeMillis();
            PlayerInteract.sendMessage(player, ChatColor.RED + "You must wait " + TimeUnit.MILLISECONDS.toSeconds(distance) + " seconds!");
            return false;
        }
    }

    public boolean checkWorld(World world) {
        FileConfiguration config = santa.getConfig();
        if (world.getName().equals(Bukkit.getWorld(config.getString("Locations.ElfOfTheMonthLocation.world")).getName())) {
            return true;
        } else {
            return false;
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!checkWorld(player.getWorld())) {
            return;
        }

        if (e.getItem() == null) {
            return;
        }

        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (elfOfMonth.checkGiftTask(player, e.getItem().getType())) {
                elfOfMonth.addPoint(player, 1);
                elfOfMonth.newGiftTask(player);
                player.getInventory().removeItem(new ItemStack(e.getItem().getType(), 1));
                player.spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 10, 0.05, 0.05, 0.05);
            }

        } else if (e.getItem().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Grapple Hook")) {
            if (checkCooldown(player)) {
                player.setVelocity(player.getLocation().getDirection().multiply(25));
                player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_STEP, 1, 1);
            }

        } else if (e.getItem().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Crafter")) {
            player.openWorkbench(null, true);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);

        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {

            Material itemType = player.getInventory().getItemInMainHand().getType();

            // for smelting
            if (player.isSneaking()) {
                Random random = new Random();

                switch (itemType) {
                    case RAW_IRON:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                    case RAW_GOLD:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                    case COBBLESTONE:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.STONE, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                    case STONE:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.SMOOTH_STONE, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                    case SPRUCE_WOOD:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.CHARCOAL, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                    case RAW_COPPER:
                        player.getInventory().removeItem(new ItemStack(itemType, 1));
                        player.getInventory().addItem(new ItemStack(Material.COPPER_INGOT, 1));
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, random.nextFloat(1.5f, 2f));
                        player.spawnParticle(Particle.LAVA, player.getLocation(), 7, 0.05, 0.05, 0.05);

                        break;
                }
            }



        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        if (ElfOfMonthItems.returnItems().contains(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            PlayerInteract.sendMessage(e.getPlayer(), "Why would you want to drop your tools?");
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (!checkWorld(e.getPlayer().getWorld())) {
            return;
        }

        if (ElfOfMonthItems.returnItems().contains(e.getItemInHand())) {
            e.setCancelled(true);
            PlayerInteract.sendMessage(e.getPlayer(), "Once you place it, it's gone!");
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



}

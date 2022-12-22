package com.yopal.secretsanta.utility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ElfOfMonthItems {

    public static ArrayList<Material> returnGifts() {
        ArrayList<Material> gifts = new ArrayList<>();
        gifts.add(Material.SPRUCE_BOAT);
        gifts.add(Material.EMERALD_BLOCK);
        gifts.add(Material.EMERALD_BLOCK);
        gifts.add(Material.SPRUCE_PLANKS);
        gifts.add(Material.SPRUCE_FENCE);
        gifts.add(Material.SPRUCE_FENCE_GATE);
        gifts.add(Material.SPRUCE_PRESSURE_PLATE);
        gifts.add(Material.SPRUCE_CHEST_BOAT);
        gifts.add(Material.SPRUCE_BUTTON);
        gifts.add(Material.SPRUCE_DOOR);
        gifts.add(Material.SPRUCE_TRAPDOOR);
        gifts.add(Material.SPRUCE_SLAB);
        gifts.add(Material.IRON_PICKAXE);
        gifts.add(Material.IRON_SHOVEL);
        gifts.add(Material.IRON_HOE);
        gifts.add(Material.IRON_BARS);
        gifts.add(Material.DIAMOND_HOE);
        gifts.add(Material.IRON_SWORD);
        gifts.add(Material.DIAMOND_SWORD);
        gifts.add(Material.BUCKET);
        gifts.add(Material.IRON_BLOCK);
        gifts.add(Material.DIAMOND_BLOCK);
        gifts.add(Material.GOLDEN_SWORD);
        gifts.add(Material.GOLDEN_AXE);
        gifts.add(Material.GOLDEN_PICKAXE);
        gifts.add(Material.LEVER);
        gifts.add(Material.JUKEBOX);
        gifts.add(Material.CHEST);
        gifts.add(Material.FURNACE);
        gifts.add(Material.MINECART);
        gifts.add(Material.CHEST_MINECART);
        gifts.add(Material.FURNACE_MINECART);
        gifts.add(Material.COPPER_BLOCK);
        return gifts;

    }

    public static ArrayList<ItemStack> returnItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        ItemStack pickaxe = new ItemBuilder(Material.DIAMOND_PICKAXE, 1).setDisplayName(ChatColor.LIGHT_PURPLE + "PickyAcky").setLore(
                ChatColor.GRAY + "MINE MINE MINE"
        ).makeUnbreakable().build();
        ItemStack axe = new ItemBuilder(Material.DIAMOND_AXE, 1).setDisplayName(ChatColor.LIGHT_PURPLE + "ChoppyAcky").setLore(
                ChatColor.GRAY + "CHOP CHOP CHOP"
        ).makeUnbreakable().build();
        ItemStack shovel = new ItemBuilder(Material.DIAMOND_SHOVEL, 1).setDisplayName(ChatColor.LIGHT_PURPLE + "DiggyAcky").setLore(
                ChatColor.GRAY + "DIG DIG DIG"
        ).makeUnbreakable().build();
        ItemStack grappleHook = new ItemBuilder(Material.CHAIN, 1).setDisplayName(ChatColor.LIGHT_PURPLE + "Grapple Hook").setLore(
                ChatColor.GRAY + "Just right-click at anything and it will send you where you need to go!"
        ).build();
        ItemStack crafter = new ItemBuilder(Material.CRAFTING_TABLE, 1).setDisplayName(ChatColor.LIGHT_PURPLE + "Crafter").setLore(
                ChatColor.GRAY + "Your handy-dandy crafter"
        ).build();

        items.add(pickaxe);
        items.add(axe);
        items.add(shovel);
        items.add(grappleHook);
        items.add(crafter);

        return items;
    }

}

package com.yopal.secretsanta.utility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * The constructor method.
     *
     * @param material the material of the item you are building.
     * @param amount   the amount of items you are building.
     */
    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    private void updateItemMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    /**
     * Sets the display name of the item, this name can be viewed by hovering over the * item in your inventory or holding it in your hand.
     *
     * @param name the name to set for the item.
     * @return the com.yopal.spleef.utility.ItemBuilder.
     */
    public ItemBuilder setDisplayName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    /**
     * @param lines the strings to set as item lore
     * @return the com.yopal.spleef.utility.ItemBuilder.
     */
    public ItemBuilder setLore(String... lines) {
        this.itemMeta.setLore(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder makeUnbreakable() {
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    /**
     * @return the ItemStack that has been created.
     */
    public ItemStack build() {
        this.updateItemMeta();
        return this.itemStack;
    }
}

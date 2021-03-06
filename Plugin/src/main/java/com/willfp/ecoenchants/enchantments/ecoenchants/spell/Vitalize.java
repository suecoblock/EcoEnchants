package com.willfp.ecoenchants.enchantments.ecoenchants.spell;

import com.willfp.ecoenchants.enchantments.itemtypes.Spell;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Vitalize extends Spell {
    public Vitalize() {
        super("vitalize");
    }

    @Override
    public void onRightClick(Player player, int level, PlayerInteractEvent event) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }
}

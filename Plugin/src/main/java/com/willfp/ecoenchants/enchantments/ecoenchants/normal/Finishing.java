package com.willfp.ecoenchants.enchantments.ecoenchants.normal;

import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.util.EnchantmentUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
public final class Finishing extends EcoEnchant {
    public Finishing() {
        super(
                "finishing", EnchantmentType.NORMAL
        );
    }

    // START OF LISTENERS


    @Override
    public void onMeleeAttack(LivingEntity attacker, LivingEntity victim, int level, EntityDamageByEntityEvent event) {

        if(!EnchantmentUtils.passedChance(this, level))
            return;

        double minhealth = this.getConfig().getDouble(EcoEnchants.CONFIG_LOCATION + "minimum-health-per-level");
        if (victim.getHealth() > level * minhealth)
            return;

        event.setDamage(30); // cba to do this properly
    }
}

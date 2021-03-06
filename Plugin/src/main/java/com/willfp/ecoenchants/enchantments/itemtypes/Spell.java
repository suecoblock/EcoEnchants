package com.willfp.ecoenchants.enchantments.itemtypes;

import com.willfp.ecoenchants.EcoEnchantsPlugin;
import com.willfp.ecoenchants.config.ConfigManager;
import com.willfp.ecoenchants.display.EnchantmentCache;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.util.EnchantChecks;
import com.willfp.ecoenchants.enchantments.util.SpellRunnable;
import com.willfp.ecoenchants.util.optional.Prerequisite;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Wrapper for Spell enchantments
 */
public abstract class Spell extends EcoEnchant {
    private final HashMap<UUID, SpellRunnable> cooldownTracker = new HashMap<>();
    private final Set<UUID> runningSpell = new HashSet<>();

    protected Spell(String key, Prerequisite... prerequisites) {
        super(key, EnchantmentType.SPELL, prerequisites);
    }

    public int getCooldownTime() {
        return this.getConfig().getInt(EcoEnchants.CONFIG_LOCATION + "cooldown");
    }

    public final Sound getActivationSound() {
        return Sound.valueOf(this.getConfig().getString(EcoEnchants.CONFIG_LOCATION + "activation-sound").toUpperCase());
    }

    @EventHandler
    public void onRightClickEventHandler(PlayerInteractEvent event) {
        if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        Player player = event.getPlayer();

        if(runningSpell.contains(player.getUniqueId())) return;

        runningSpell.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(EcoEnchantsPlugin.getInstance(), () -> runningSpell.remove(player.getUniqueId()), 5);

        if(!EnchantChecks.mainhand(player, this))
            return;

        int level = EnchantChecks.getMainhandLevel(player, this);

        if(!cooldownTracker.containsKey(player.getUniqueId()))
            cooldownTracker.put(player.getUniqueId(), new SpellRunnable(player, this));

        SpellRunnable runnable = cooldownTracker.get(player.getUniqueId());
        runnable.setTask(() -> {
            this.onRightClick(player, level, event);
        });

        long msLeft = runnable.getEndTime() - System.currentTimeMillis();

        long secondsLeft = (long) Math.ceil((double) msLeft / 1000);

        if(msLeft > 0) {
            String message = ConfigManager.getLang().getMessage("on-cooldown").replaceAll("%seconds%", String.valueOf(secondsLeft)).replaceAll("%name%", EnchantmentCache.getEntry(this).getRawName());
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.5f);
            return;
        }

        String message = ConfigManager.getLang().getMessage("used-spell").replaceAll("%name%", EnchantmentCache.getEntry(this).getRawName());
        player.sendMessage(message);
        player.playSound(player.getLocation(), this.getActivationSound(), SoundCategory.PLAYERS, 1, 1);
        runnable.run();
    }

    public abstract void onRightClick(Player player, int level, PlayerInteractEvent event);
}

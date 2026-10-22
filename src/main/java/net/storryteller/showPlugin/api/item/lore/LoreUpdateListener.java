package net.storryteller.showPlugin.api.item.lore;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import net.storryteller.showPlugin.api.enums.CustomItemEnum;
import net.storryteller.showPlugin.api.enums.LoreUpdateTrigger;
import net.storryteller.showPlugin.api.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class LoreUpdateListener implements Listener {

    @EventHandler
    public void onRightOrLeftClick(PlayerInteractEvent event) {
        if (event.getHand() == null) {
            return;
        }

        LoreUpdateTrigger trigger;

        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK) {
            trigger = LoreUpdateTrigger.RIGHT_CLICK;
        } else if (action == Action.LEFT_CLICK_AIR
                || action == Action.LEFT_CLICK_BLOCK) {
            trigger = LoreUpdateTrigger.LEFT_CLICK;
        } else {
            return;
        }

        refreshHand(event.getPlayer(), event.getHand(), trigger);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        refreshBothHands(
                event.getPlayer(),
                LoreUpdateTrigger.JUMP
        );
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        refreshBothHands(
                event.getPlayer(),
                LoreUpdateTrigger.SNEAK
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwitchHand(PlayerSwapHandItemsEvent event) {
        refreshBothHands(
                event.getPlayer(),
                LoreUpdateTrigger.SWITCH_HAND
        );
    }

    private void refreshBothHands(
            Player player,
            LoreUpdateTrigger trigger
    ) {
        refreshHand(player, EquipmentSlot.HAND, trigger);
        refreshHand(player, EquipmentSlot.OFF_HAND, trigger);
    }

    private void refreshHand(
            Player player,
            EquipmentSlot hand,
            LoreUpdateTrigger trigger
    ) {
        ItemStack item = hand == EquipmentSlot.HAND
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();

        CustomItemEnum type = CustomItem.isItemOfType(item);

        if (type == null || !type.updatesOn(trigger)) {
            return;
        }

        LoreManager.update(item, type.getLoreId());

        if (hand == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.getInventory().setItemInOffHand(item);
        }
    }
}
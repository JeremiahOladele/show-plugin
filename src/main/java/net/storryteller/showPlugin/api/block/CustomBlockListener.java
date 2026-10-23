package net.storryteller.showPlugin.api.block;

import net.storryteller.showPlugin.api.enums.CustomBlockEnum;
import net.storryteller.showPlugin.api.enums.CustomItemEnum;
import net.storryteller.showPlugin.api.item.CustomItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static net.storryteller.showPlugin.ShowPlugin.ANSI_RESET;
import static net.storryteller.showPlugin.ShowPlugin.ANSI_YELLOW;

public class CustomBlockListener implements Listener {
    private final CustomBlockManager blockManager;

    public CustomBlockListener(CustomBlockManager blockManager) {
        this.blockManager = blockManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {return;}
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {return;}

        ItemStack heldItem = event.getItem();
        CustomItemEnum itemType = CustomItem.isItemOfType(heldItem);
        if (itemType == null) {return;}

        CustomBlockEnum blockType = CustomBlockEnum.fromPlacementItem(itemType);
        if (blockType == null) {return;}

        event.setCancelled(true);

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {return;}

        Block targetBlock = clickedBlock.getRelative(event.getBlockFace());
        if (!targetBlock.getType().isAir()) {return;}

        Player player = event.getPlayer();

        Location location = targetBlock.getLocation().add(0.5, 0, 0.5);

        try {
            blockManager.place(blockType, location);
        } catch (RuntimeException exception) {
            player.sendMessage(
                    "Could not place that custom block. Please try again."
            );
            player.getServer().getLogger().severe(ANSI_RESET + ANSI_YELLOW +
                    "\n " +
                    "Failed to place block " + blockType + ": " + exception.getMessage() + ANSI_RESET
            );
            return;
        }

        if (player.getGameMode().name().equals("CREATIVE")) {return;}
        heldItem.setAmount(heldItem.getAmount() - 1);
        player.getInventory().setItemInMainHand(heldItem);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (!blockManager.isCustomHitbox(clicked)) {return;}

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (player.isSneaking()) {return;}

        CustomBlockEnum blockType = blockManager.getBlockType(clicked);
        if (blockType == null) {return;}

        CustomBlockInteraction.interact(player, clicked, blockType);
    }
}
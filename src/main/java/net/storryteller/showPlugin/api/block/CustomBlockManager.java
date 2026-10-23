package net.storryteller.showPlugin.api.block;

import net.storryteller.showPlugin.api.enums.CustomBlockEnum;
import net.storryteller.showPlugin.api.item.CustomItem;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class CustomBlockManager {
    private final NamespacedKey blockIdKey;
    private final NamespacedKey isHitboxKey;

    public CustomBlockManager(Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin cannot be null");

        this.blockIdKey = new NamespacedKey(plugin, "custom_block_id");
        this.isHitboxKey = new NamespacedKey(plugin, "custom_block_hitbox");
    }

    public void place(CustomBlockEnum type, Location location) {
        Objects.requireNonNull(type, "Block type cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        World world = Objects.requireNonNull(location.getWorld(), "World cannot be null");

        Location spawnLocation = location.clone();

        ArmorStand stand = world.spawn(
                spawnLocation,
                ArmorStand.class,
                armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.setSilent(true);
                    armorStand.setGravity(false);
                    armorStand.setInvisible(true);
                    armorStand.setPersistent(true);
                    armorStand.setInvulnerable(true);

                    ItemStack model = CustomItem.get(type.getModelItem());
                    armorStand.getEquipment().setHelmet(model);
                    armorStand.addEquipmentLock(
                            EquipmentSlot.HEAD,
                            ArmorStand.LockType.ADDING_OR_CHANGING
                    );
                    armorStand.addEquipmentLock(
                            EquipmentSlot.HEAD,
                            ArmorStand.LockType.REMOVING_OR_CHANGING
                    );
                    mark(armorStand, type, false);
                }
        );

        try {
            world.spawn(
                    spawnLocation,
                    Shulker.class,
                    hitbox -> {
                        hitbox.setPeek(0);
                        hitbox.setAI(false);
                        hitbox.setSilent(true);
                        hitbox.setInvisible(true);
                        hitbox.setPersistent(true);
                        hitbox.setCollidable(false);
                        hitbox.setInvulnerable(true);

                        mark(hitbox, type, true);
                    }
            );
        } catch (RuntimeException exception) {
            stand.remove();
            throw exception;
        }
    }

    private void mark(Entity entity, CustomBlockEnum type, boolean hitbox) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(blockIdKey, PersistentDataType.INTEGER, type.getId());
        data.set(isHitboxKey, PersistentDataType.BYTE, (byte) (hitbox ? 1 : 0));
    }

    public CustomBlockEnum getBlockType(Entity entity) {
        if (entity == null) {return null;}

        Integer id = entity.getPersistentDataContainer().get(blockIdKey, PersistentDataType.INTEGER);
        return id == null ? null : CustomBlockEnum.fromId(id);
    }

    public boolean isCustomHitbox(Entity entity) {
        if (entity == null) {return false;}

        Byte value = entity.getPersistentDataContainer().get(isHitboxKey, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }
}

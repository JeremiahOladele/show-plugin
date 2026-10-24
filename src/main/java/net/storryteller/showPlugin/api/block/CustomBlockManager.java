package net.storryteller.showPlugin.api.block;

import net.storryteller.showPlugin.api.enums.CustomBlockEnum;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;

import static net.storryteller.showPlugin.ShowPlugin.ANSI_RESET;
import static net.storryteller.showPlugin.ShowPlugin.ANSI_YELLOW;

public class CustomBlockManager {
    private final NamespacedKey blockIdKey;
    private final NamespacedKey isHitboxKey;
    private final NamespacedKey partnerKey;

    public CustomBlockManager(Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin cannot be null");

        this.blockIdKey = new NamespacedKey(plugin, "custom_block_id");
        this.isHitboxKey = new NamespacedKey(plugin, "custom_block_hitbox");
        this.partnerKey = new NamespacedKey(plugin, "custom_block_partner");
    }

    public void place(CustomBlockEnum type, Location location) {
        Objects.requireNonNull(type, "Block type cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        World world = Objects.requireNonNull(location.getWorld(), "World cannot be null");

        Location spawnLocation = location.clone().add(0, 0, 0);

        BlockDisplay stand = world.spawn(
                spawnLocation,
                BlockDisplay.class,
                entity -> {
                    entity.setBlock(type.getModelItem().getMaterial().createBlockData());
                    entity.setPersistent(false);
                    entity.setGravity(false);
                    entity.setTransformation(new Transformation(
                            new Vector3f(-0.5f, 0.0f, -0.5f),
                            new AxisAngle4f(0, 0, 0, 0),
                            new Vector3f(1.0f, 1.0f, 1.0f),
                            new AxisAngle4f(0, 0, 1, 0)));

                    mark(entity, type, true);
                }
        );

        try {
            Shulker hitbox = world.spawn(
                    spawnLocation,
                    Shulker.class,
                    entity -> {
                        entity.setPeek(0);
                        entity.setAI(false);
                        entity.setSilent(true);
                        entity.setInvisible(true);
                        entity.setPersistent(true);
                        entity.setCollidable(false);
                        entity.setInvulnerable(false);

                        mark(entity, type, true);
                    }
            );

            stand.getPersistentDataContainer().set(
                    partnerKey,
                    PersistentDataType.STRING,
                    hitbox.getUniqueId().toString()
            );
            hitbox.getPersistentDataContainer().set(
                    partnerKey,
                    PersistentDataType.STRING,
                    stand.getUniqueId().toString()
            );

        } catch (RuntimeException exception) {
            stand.remove();
            throw exception;
        }
    }

    public CustomBlockEnum removeCustomBlock(Entity entity) {
        if (entity == null || !isCustomHitbox(entity)) {return null;}

        CustomBlockEnum type = getBlockType(entity);
        if (type == null) {return null;}

        String partnerId = entity.getPersistentDataContainer().get(partnerKey, PersistentDataType.STRING);
        entity.remove();

        if (partnerId != null) {
            try {
                java.util.UUID uuid = java.util.UUID.fromString(partnerId);
                Entity partner = entity.getWorld().getEntity(uuid);
                if (partner != null && partner.isValid()) {partner.remove();}
            } catch (IllegalArgumentException ignored) {
                System.out.println(ANSI_RESET + ANSI_YELLOW +
                        "\n " +
                        "Invalid UUID :" + partnerId + ANSI_RESET);
            }
        }
        return type;
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

package net.storryteller.showPlugin.api.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.storryteller.showPlugin.api.enums.CustomItemEnum;
import net.storryteller.showPlugin.api.item.lore.LoreManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CustomItem {
    private static final MiniMessage MINI_MESSAGE =
            MiniMessage.miniMessage();

    private static final Map<CustomItemEnum, ItemStack> TEMPLATES =
            new EnumMap<>(CustomItemEnum.class);

    private static NamespacedKey itemIdKey;
    private static boolean initialized;

    private CustomItem() {

    }

    public static void initalize(Plugin plugin) {
        Objects.requireNonNull(plugin, "Plugin cannot be null");

        if (initialized) {
            throw new IllegalStateException(
                    "CustomItem has already been initialized."
            );
        }

        itemIdKey = new NamespacedKey(plugin, "custom_item_id");
        Set<Integer> usedIds = new HashSet<>();

        for (CustomItemEnum type : CustomItemEnum.values()) {
            if (!usedIds.add(type.getId())) {
                throw new IllegalStateException(
                        "Duplicate custom item ID: " + type.getId()
                );
            }
            TEMPLATES.put(type, buildItem(type));
        }
        initialized = true;

        plugin.getLogger().info(
                "Loaded " + TEMPLATES.size() + " custom items."
        );
    }

    private static ItemStack buildItem(CustomItemEnum type) {
        ItemStack item = new ItemStack(type.getMaterial());

        item.editMeta(meta -> {Component displayName = MINI_MESSAGE.deserialize(type.getName());

            meta.displayName(displayName);
            meta.setCustomModelData(type.getCustomModelData());

            meta.lore(LoreManager.getLore(type.getLoreId()));

            meta.getPersistentDataContainer().set(
                    itemIdKey,
                    PersistentDataType.INTEGER,
                    type.getId()
            );
        });

        return item;
    }

    public static ItemStack get(CustomItemEnum type) {
        requireInitialized();

        ItemStack template = TEMPLATES.get(
                Objects.requireNonNull(type, "Item type cannot be null")
        );

        if (template == null) {
            throw new IllegalArgumentException(
                    "No item template exists for " + type
            );
        }

        return template.clone();
    }
    public static ItemStack get(CustomItemEnum type, int amount) {
        ItemStack item = get(type);
        item.setAmount(amount);
        return item;
    }
    public static CustomItemEnum isItemOfType(ItemStack item) {
        requireInitialized();

        if (item == null || item.getType().isAir()) {return null;}

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {return null;}

        Integer id = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.INTEGER);
        if (id == null) {return null;}

        for (CustomItemEnum type : CustomItemEnum.values()) {
            if (type.getId() == id) {return type;}
        }
        return null;
    }
    public static boolean is(ItemStack item, CustomItemEnum expectedType) {
        return isItemOfType(item) == expectedType;
    }
    public static Integer getId(ItemStack item) {
        CustomItemEnum type = isItemOfType(item);
        return type == null ? null : type.getId();
    }

    private static void requireInitialized() {
        if (!initialized) {
            throw new IllegalStateException(
                    "CustomItem.initialize(plugin) must be called first."
            );
        }
    }
}

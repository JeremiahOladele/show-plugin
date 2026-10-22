package net.storryteller.showPlugin.api.item.lore;

import net.kyori.adventure.text.Component;
import net.storryteller.showPlugin.api.enums.CustomLoreEnum;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LoreManager {
    private LoreManager() {
    }

    public static List<Component> getLore(int loreId) {
        CustomLoreEnum definition = CustomLoreEnum.fromId(loreId);
        if (definition == null) {return List.of();}

        return definition.getLines()
                .stream()
                .map(LoreLine::toComponent)
                .toList();
    }

    public static void update(ItemStack item, int loreId) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {return;}

        meta.lore(getLore(loreId));
        item.setItemMeta(meta);
    }
}

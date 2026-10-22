package net.storryteller.showPlugin.api.enums;

import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public enum CustomItemEnum {
    POISON_DAGGER(
            0001,
            "Poison Dagger",
            Material.DIAMOND_SWORD,
            0001,
            0001,
            Collections.singleton(LoreUpdateTrigger.RIGHT_CLICK));

    private final int id;
    private final String name;
    private final Material material;
    private final int customModelData;
    private final int loreId;
    private final Set<LoreUpdateTrigger> updateTriggers;

    CustomItemEnum(
            int id,
            String name,
            Material material,
            int customModelData,
            int loreId, Set<LoreUpdateTrigger> updateTriggers
    ) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.customModelData = customModelData;
        this.loreId = loreId;
        this.updateTriggers = updateTriggers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public int getLoreId() {
        return loreId;
    }

    public boolean updatesOn(LoreUpdateTrigger trigger) {return updateTriggers.contains(trigger);}
}

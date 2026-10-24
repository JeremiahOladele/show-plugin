package net.storryteller.showPlugin.api.enums;

import net.storryteller.showPlugin.api.item.lore.LoreLine;

import java.util.List;

public enum CustomLoreEnum {
    POISON_DAGGER_LORE(
            0001,
            List.of(
                    new LoreLine(
                            "A Poisonous Blade.",
                            "#FF4500",
                            null,
                            true,
                            false
                    ),
                    new LoreLine(
                            "Right-click to create a poison cloud.",
                            "#FFD700",
                            null,
                            false,
                            true
                    )
            )
    ),
    DUNGEON_BLOCK_LORE(
            0002,
            List.of(
                    new LoreLine(
                            "Block",
                            "#FF4500",
                            null,
                            false,
                            false

                    )
            )
    )
    ;

    private final int id;
    private final List<LoreLine> lines;

    CustomLoreEnum(int id, List<LoreLine> lines) {
        this.id = id;
        this.lines = List.copyOf(lines);
    }

    public int getId() {
        return id;
    }

    public List<LoreLine> getLines() {
        return lines;
    }

    public static CustomLoreEnum fromId(int id) {
        for (CustomLoreEnum lore : values()) {
            if (lore.id == id) {return lore;}
        }

        return null;
    }
}

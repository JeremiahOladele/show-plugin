package net.storryteller.showPlugin.api.enums;

public enum CustomBlockEnum {
    ;

    private final int id;
    private final CustomItemEnum placementItem;
    private final CustomItemEnum modelItem;

    CustomBlockEnum(
            int id,
            CustomItemEnum placementItem,
            CustomItemEnum modelItem
    ) {
        this.id = id;
        this.placementItem = placementItem;
        this.modelItem = modelItem;
    }

    public int getId() {
        return id;
    }

    public CustomItemEnum getPlacementItem() {
        return placementItem;
    }

    public CustomItemEnum getModelItem() {
        return modelItem;
    }

    public static CustomBlockEnum fromId(int id) {
        for (CustomBlockEnum type : values()) {
            if (type.id == id) {return type;}
        }
        return null;
    }

    public static CustomBlockEnum fromPlacementItem(
            CustomItemEnum item
    ) {
        for (CustomBlockEnum type : values()) {
            if (type.placementItem == item) {return type;}
        }
        return null;
    }
}

package net.storryteller.showPlugin.api.enums;

public enum NudgeElements {

    Left,
    Right;

    public String glyphFor(int pixels) {
        String glyph = switch (pixels) {
            case 1   -> "\uE101";
            case 2   -> "\uE102";
            case 4   -> "\uE103";
            case 8   -> "\uE104";
            case 16  -> "\uE105";
            case 32  -> "\uE106";
            case 64  -> "\uE107";
            case 128 -> "\uE108";
            default -> throw new IllegalArgumentException(
                    "Unsupported nudge distance: " + pixels
            );
        };

        return this == Left ? glyph : glyph;
    }
}
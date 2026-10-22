package net.storryteller.showPlugin.api.item.lore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;
import java.util.Objects;

import static net.storryteller.showPlugin.ShowPlugin.ANSI_RESET;
import static net.storryteller.showPlugin.ShowPlugin.ANSI_YELLOW;

public final class LoreLine {

    private final String text;
    private final String color;
    private final String font;
    private final boolean bold;
    private final boolean italic;

    public LoreLine(
            String text,
            String color,
            String font,
            boolean bold,
            boolean italic
    ) {
        this.text = Objects.requireNonNull(text, "Text cannot be null");
        this.color = color;
        this.font = font;
        this.bold = bold;
        this.italic = italic;
    }

    public static LoreLine of(String text) {
        return new LoreLine(text, "white", null, false, false);
    }

    public Component toComponent() {
        Component component = Component.text(text).decoration(TextDecoration.BOLD, bold).decoration(TextDecoration.ITALIC, italic);

        if (color != null && !color.isBlank()) {
            TextColor parsedColor = parseColor(color);
            component = component.color(parsedColor);
        }

        if (font != null && !font.isBlank()) {
            component = component.font(Key.key(font));
        }

        return component;
    }

    private static TextColor parseColor(String input) {
        String value = input.toLowerCase(Locale.ROOT);

        NamedTextColor named = NamedTextColor.NAMES.value(value);

        if (named != null) {
            return named;
        }

        TextColor hex = TextColor.fromHexString(input);

        if (hex == null) {
            System.out.println(ANSI_RESET + ANSI_YELLOW +
                    "\n " +
                    "Invalid Color for : '" + input + "'" + ANSI_RESET);
        }

        return hex;
    }
}
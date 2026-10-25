package net.storryteller.showPlugin.api.enums;

public enum GuiElements {

    Book("\uE001"),
    PageNext("\uE002", true),
    PageNextDisabled("\uE003"),
    PagePrevious("\uE004", true),
    PagePreviousDisabled("\uE005");

    private final String glyph;
    private final boolean buttonCapable;

    GuiElements(String glyph) {this(glyph, false);}

    GuiElements(String glyph, boolean buttonCapable) {
        this.glyph = glyph;
        this.buttonCapable = buttonCapable;
    }

    public String glyph() {return glyph;}

    public boolean isButtonCapable() {return buttonCapable;}
}
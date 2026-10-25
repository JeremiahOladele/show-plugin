package net.storryteller.showPlugin.api.enums;

public enum TextElements {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    H("H"),
    I("I"),
    J("J"),
    K("K"),
    L("L"),
    M("M"),
    N("N"),
    O("O"),
    P("P"),
    Q("Q"),
    R("R"),
    S("S"),
    T("T"),
    U("U"),
    V("V"),
    W("W"),
    X("X"),
    Y("Y"),
    Z("Z"),

    Space(" "),
    Minus("-"),
    Plus("+"),
    Underscore("_"),
    Dash("-"),
    OpenBracket("["),
    CloseBracket("]"),
    FullStop(".");

    private final String text;

    TextElements(String text) {this.text = text;}

    public String text() {return text;}
}

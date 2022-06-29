package compiler.constants;

public enum SymbolToken {
    L_CURLY('{'),
    R_CURLY('}'),
    L_BRACE('('),
    R_BRACE(')'),
    L_SQUARE('['),
    R_SQUARE(']'),
    DOT('.'),
    COMMA(','),
    SEMI_COLON(';'),
    PLUS('+'),
    MINUS('-'),
    ASTERISK('*'),
    SLASH('/'),
    PIPE('|'),
    LT('<'),
    GT('>'),
    EQ('='),
    TILDE('~'),
    AMP('&');

    private final char val;
    private final String str;

    SymbolToken(char c) {
        this.val = c;
        this.str = String.valueOf(c);
    }

    public String str() {
        return str;
    }

    public char val() {
        return val;
    }

    public boolean is(char c) {
        return c == val;
    }

    public String getName() {
        return name();
    }
}

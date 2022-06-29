package compiler.constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Keyword {

    STATIC,
    CONSTRUCTOR,
    FIELD,
    CLASS,
    VAR,
    FUNCTION,
    METHOD,
    INT,
    CHAR,
    BOOLEAN,
    VOID,
    TRUE,
    FALSE,
    NULL,
    THIS,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN;

    private static final String[] keywords;

    static {
        List<String> t = new ArrayList<>();
        for (Keyword k : values()) {
            t.add(k.lower());
        }
        keywords = t.toArray(new String[values().length]);

    }

    public static Keyword get(String val) {
        return Keyword.valueOf(val.toUpperCase(Locale.ROOT));
    }

    public static boolean isKeyword(String val) {
        return false;
    }

    public static String[] keywords() {
        return keywords;
    }

    private String lower() {
        return name().toLowerCase();
    }

    public String keyword() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}

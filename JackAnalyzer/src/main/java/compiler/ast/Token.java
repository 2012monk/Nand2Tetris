package compiler.ast;

import compiler.constants.TokenType;
import compiler.parser.Location;

public class Token {

    private final TokenType type;
    private final String value;
    private final Location start;
    private final Location end;

    public Token(TokenType type, String value, Location start, Location end) {
        this.type = type;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.start = new Location(0, 0, 0);
        this.end = new Location(0, 0, 0);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }
}

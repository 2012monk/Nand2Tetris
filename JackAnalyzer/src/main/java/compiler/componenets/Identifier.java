package compiler.componenets;

import compiler.constants.DataType;
import compiler.constants.IdentifierType;

public class Identifier {

    public static final Identifier NONE = new Identifier();
    private String name;
    private IdentifierType type;

    public Identifier(String name, IdentifierType type) {
        this.name = name;
        this.type = type;
    }

    private Identifier() {
        this.type = IdentifierType.NONE;
    }

    public boolean is(IdentifierType type) {
        return this.type == type;
    }

    public String getName() {
        return name;
    }

    public IdentifierType getType() {
        return type;
    }
}

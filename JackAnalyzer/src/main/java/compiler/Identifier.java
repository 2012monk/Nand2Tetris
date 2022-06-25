package compiler;

import compiler.constants.IdentifierType;

public class Identifier {

    private int index;
    private IdentifierType type;
    private String id;

    public Identifier(int index, IdentifierType type, String id) {
        this.index = index;
        this.type = type;
        this.id = id;
    }
}

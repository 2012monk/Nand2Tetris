package compiler.componenets;

import compiler.constants.IdentifierType;

public class Identifier {

    private String name;
    private IdentifierType type;
    private Identifier parent;

    public Identifier(String name, IdentifierType type) {
        this.name = name;
        this.type = type;
    }

    public Identifier(String name, IdentifierType type, Identifier parent) {
        this.name = name;
        this.type = type;
        this.parent = parent;
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

    public Identifier getParent() {
        return parent;
    }

    public void setParent(Identifier id) {
        this.parent = id;
    }

    public boolean isVariable() {
        return false;
    }
}

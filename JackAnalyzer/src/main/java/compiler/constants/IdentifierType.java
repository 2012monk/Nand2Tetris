package compiler.constants;

public enum IdentifierType {

    CLASS_NAME("className"),
    SUB_ROUTINE_NAME("subRoutineName"),
    VAR_NAME("varName");

    private String id;

    IdentifierType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}

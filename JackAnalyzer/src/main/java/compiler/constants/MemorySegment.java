package compiler.constants;

public enum MemorySegment {
    LCL("local"),
    ARG("argument"),
    THIS("this"),
    THAT("that"),
    PTR("pointer"),
    TMP("temp"),
    STATIC("static"),
    CONSTANT("constant");

    private String id;

    MemorySegment(String id) {
        this.id = id;
    }

    public static MemorySegment callerSegment(IdentifierType type) {
        if (type == IdentifierType.ARGUMENT_NAME) return ARG;
        if (type == IdentifierType.FILED_NAME) return THIS;
        if (type == IdentifierType.VAR_NAME) return LCL;
        if (type == IdentifierType.STATIC_NAME) return STATIC;
        throw new IllegalArgumentException();
    }

    public String getId() {
        return id;
    }
}

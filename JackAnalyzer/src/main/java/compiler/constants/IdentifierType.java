package compiler.constants;

import java.util.HashMap;
import java.util.Map;

public enum IdentifierType {

    CLASS_NAME("className", Keyword.CLASS),

    CONSTRUCTOR_NAME("constructorName", Keyword.CONSTRUCTOR),
    FUNCTIONS_NAME("functionName", Keyword.FUNCTION),
    METHOD_NAME("methodName", Keyword.METHOD),

    STATIC_NAME("static", Keyword.STATIC),
    FILED_NAME("filed", Keyword.FIELD),

    VAR_NAME("var", Keyword.VAR),
    ARGUMENT_NAME("argument", Keyword.VAR),
    NONE("none", null);

    private static final Map<Keyword, IdentifierType> map = new HashMap<>();

    static {
        for (IdentifierType v : values()) {
            map.put(v.keyword, v);
        }
        map.put(Keyword.VAR, VAR_NAME);
    }

    private String id;
    private Keyword keyword;

    IdentifierType(String id, Keyword keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public static IdentifierType type(Keyword keyword) {
        return map.get(keyword);
    }

    public static IdentifierType subroutineType(LexicalType type) {
        if (type == LexicalType.METHOD) {
            return METHOD_NAME;
        }
        if (type == LexicalType.FUNCTION) {
            return FUNCTIONS_NAME;
        }
        if (type == LexicalType.CONSTRUCTOR) {
            return CONSTRUCTOR_NAME;
        }
        throw new IllegalArgumentException();
    }

    public static IdentifierType varType(LexicalType type) {
        if (type == LexicalType.PARAMETER) return ARGUMENT_NAME;
        if (type == LexicalType.VAR) return VAR_NAME;
        if (type == LexicalType.FIELD) return FILED_NAME;
        if (type == LexicalType.STATIC) return STATIC_NAME;
        throw new IllegalArgumentException(type.getName());
    }

    public String id() {
        return id;
    }

    public String getName() {
        return id;
    }
}

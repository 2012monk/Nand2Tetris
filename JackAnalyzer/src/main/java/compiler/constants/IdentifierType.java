package compiler.constants;

import java.util.HashMap;
import java.util.Map;

public enum IdentifierType {

    CLASS_NAME("className", Keyword.CLASS, Scope.GLOBAL),

    CONSTRUCTOR_NAME("constructorName", Keyword.CONSTRUCTOR, Scope.CLASS),
    FUNCTIONS_NAME("functionName", Keyword.FUNCTION, Scope.CLASS),
    METHOD_NAME("methodName", Keyword.METHOD, Scope.CLASS),

    STATIC_NAME("static", Keyword.STATIC, Scope.CLASS),
    FILED_NAME("filed", Keyword.FIELD, Scope.CLASS),

    VAR_NAME("var", Keyword.VAR, Scope.SUB_ROUTINE),
    ARGUMENT_NAME("argument", Keyword.VAR, Scope.SUB_ROUTINE),
    NONE("none", null, null);

    private static final Map<Keyword, IdentifierType> map = new HashMap<>();

    static {
        for (IdentifierType v : values()) {
            map.put(v.keyword, v);
        }
        map.put(Keyword.VAR, VAR_NAME);
    }

    private String id;
    private Keyword keyword;
    private Scope scope;

    IdentifierType(String id, Keyword keyword, Scope scope) {
        this.id = id;
        this.keyword = keyword;
        this.scope = scope;
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

    public String id() {
        return id;
    }

    public String getName() {
        return id;
    }
}

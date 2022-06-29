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

    public static IdentifierType typeG(LexicalType type) {
        Keyword k = type.keyword();
        return map.get(k);
    }

    public String id() {
        return id;
    }

    public Scope scope() {
        return scope;
    }

    public Keyword keyword() {
        return keyword;
    }

    public String getName() {
        return id;
    }
}

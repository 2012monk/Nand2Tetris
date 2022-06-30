package compiler.constants;

import java.util.HashMap;
import java.util.Map;

public enum VMCommand {

    NEG("neg", null, LexicalType.MINUS),
    NOT("not", null, LexicalType.TILDE),
    AND("and", LexicalType.AMP),
    OR("or", LexicalType.PIPE),
    ADD("add", LexicalType.PLUS),
    SUB("sub", LexicalType.MINUS),
    EQ("eq", LexicalType.EQ),
    GT("gt", LexicalType.GT),
    LT("lt", LexicalType.LT);

    private static final Map<LexicalType, VMCommand> map = new HashMap<>();
    private static final Map<LexicalType, VMCommand> unary = new HashMap<>();

    static {
        for (VMCommand v : values()) {
            map.put(v.type, v);
            unary.put(v.unaryOp, v);
        }
    }

    private final String id;
    private final LexicalType type;
    private LexicalType unaryOp;

    VMCommand(String str, LexicalType type) {
        this.id = str;
        this.type = type;
    }

    VMCommand(String id, LexicalType op, LexicalType unary) {
        this.id = id;
        this.type = op;
        this.unaryOp = unary;
    }

    public static VMCommand getOP(LexicalType type) {
        return map.get(type);
    }

    public static VMCommand getUnaryOP(LexicalType type) {
        return unary.get(type);
    }

    public String getId() {
        return id;
    }
}

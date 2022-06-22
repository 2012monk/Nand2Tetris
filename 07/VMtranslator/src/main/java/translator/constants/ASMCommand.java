package translator.constants;

import java.util.HashMap;
import java.util.Map;

public enum ASMCommand {
    NEG("neg", "-"),
    NOT("not", "!"),
    AND("and", "&"),
    OR("or", "|"),
    ADD("add", "+"),
    SUB("sub", "-"),
    EQ("eq", "JEQ"),
    GT("gt", "JGT"),
    LT("lt", "JLT")
    ;

    private static final  Map<String, ASMCommand> map = new HashMap<>();
    static {
        for (ASMCommand c : ASMCommand.values()) {
            map.put(c.vmCommand, c);
        }
    }
    private String vmCommand;
    private String op;
    ASMCommand(String vmCommand, String op) {
        this.vmCommand = vmCommand;
        this.op = op;
    }

    public String op() {
        return op;
    }

    public static ASMCommand command(String vmCommand) {
        return map.get(vmCommand);
    }
}

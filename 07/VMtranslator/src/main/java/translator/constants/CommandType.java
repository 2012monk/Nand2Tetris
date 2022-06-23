package translator.constants;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    C_ARITHMETIC(0, "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"),
    C_PUSH(2, "push"),
    C_POP(2, "pop"),
    C_LABEL(1, "label"),
    C_GOTO(1, "goto"),
    C_IF(1, "if-goto"),
    C_FUNCTION(2, "function"),
    C_CALL(2, "call"),
    C_RETURN(0, "return");

    private static final Map<String, CommandType> types = new HashMap<>();

    static {
        for (CommandType type : CommandType.values()) {
            for (String command : type.commands) {
                types.put(command, type);
            }
        }
    }

    private final String[] commands;
    private final int argCount;

    CommandType(int argCount, String... commands) {
        this.argCount = argCount;
        this.commands = commands;
    }

    public static CommandType type(String cmd) {
        return types.get(cmd);
    }

    public int argc() {
        return argCount;
    }
}

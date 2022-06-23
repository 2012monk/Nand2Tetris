package translator.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import translator.CodeWriter;
import translator.Parser;

public enum CommandType {
    C_ARITHMETIC(1,
        (w, p) -> w.writeArithmetic(p.arg1()),
        "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"),
    C_PUSH(2, (w, p) -> w.writePush(p.arg1(), p.arg2()), "push"),
    C_POP(2, (w, p) -> w.writePop(p.arg1(), p.arg2()), "pop"),
    C_LABEL(1, (w, p) -> w.writeLabel(p.arg1()), "label"),
    C_GOTO(1, (w, p) -> w.writeGoto(p.arg1()), "goto"),
    C_IF(1, (w, p) -> w.writeIf(p.arg1()), "if-goto"),
    C_FUNCTION(2, (w, p) -> w.writeFunction(p.arg1(), p.arg2()), "function"),
    C_CALL(2, (w, p) -> w.writeCall(p.arg1(), p.arg2()), "call"),
    C_RETURN(0, (w, p) -> w.writeReturn(), "return");

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
    private BiConsumer<CodeWriter, Parser> t;

    CommandType(int argCount, BiConsumer<CodeWriter, Parser> t, String... commands) {
        this.argCount = argCount;
        this.commands = commands;
        this.t = t;
    }

    public static CommandType type(String cmd) {
        return types.get(cmd);
    }

    public int argc() {
        return argCount;
    }

    public void translate(CodeWriter writer, Parser parser) {
        t.accept(writer, parser);
    }
}

package translator.constants;

public enum CommandType {
    C_ARITHMETIC("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"),
    C_PUSH("push"),
    C_POP("pop"),
    C_LABEL("label"),
    C_GOTO("goto", "if-goto"),
    C_IF("if"),
    C_FUNCTION("function"),
    C_RETURN("return"),
    C_CALL("call");

    CommandType(String... commands) {
    }
}

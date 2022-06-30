package compiler.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public enum LexicalType {
    CLASS_DECLARATION,
    CLASS_BODY,
    CLASS_VARIABLE_DECLARATION,

    FIELD_VARIABLE_DECLARATION,
    STATIC_VARIABLE_DECLARATION,
    LOCAL_VARIABLE_DECLARATION,
    ARGUMENT_DECLARATION,
    VARIABLE_DECLARATION,

    SUBROUTINE_PREFIX,
    SUBROUTINE_DECLARATION,
    FUNCTION_DECLARATION,
    METHOD_DECLARATION,
    CONSTRUCTOR_DECLARATION,
    SUBROUTINE_BODY,

    VARIABLE_TYPE,
    DATA_TYPE,
    RETURN_TYPE,

    PARAMETER_LIST,
    ARGUMENT_LIST,

    STATEMENTS,
    LET_STATEMENT,
    IF_STATEMENT,
    DO_STATEMENT,
    RETURN_STATEMENT,
    WHILE_STATEMENT,
    CONDITION_EXPRESSION,
    BLOCK_STATEMENTS,

    IF_BODY,
    ELSE_BODY,

    EXPRESSION_LIST,
    TERM,

    TERNARY_EXPRESSION,

    INTEGER_CONSTANT,
    STRING_CONSTANT,
    KEYWORD_CONSTANT,

    ARRAY_ACCESS_EXPRESSION,
    ARRAY_ACCESS_SUFFIX,
    UNARY_EXPRESSION,
    BINARY_EXPRESSION,

    UNARY_OPERATOR,
    BINARY_OPERATOR,

    SUBROUTINE_CALL,
    SUBROUTINE_CALL_SUFFIX,

    TERMINAL,
    SUBROUTINE_NAME,
    CLASS_NAME,
    FUNCTION_NAME,
    METHOD_NAME,
    CONSTRUCTOR_NAME,
    ARGUMENT_NAME,
    LOCAL_VARIABLE_NAME,
    FIELD_VARIABLE_NAME,
    STATIC_VARIABLE_NAME,
    SYMBOL_TOKEN,
    REF_TYPE,
    EXPRESSION, MODIFIER,
    KEYWORD, IDENTIFIER,
    VARIABLE_NAME,
    SELF_CALL,
    CALL_TYPE,
    PARAMETER,

    L_CURLY,
    R_CURLY,
    L_BRACE,
    R_BRACE,
    L_SQUARE,
    R_SQUARE,
    DOT,
    COMMA,
    SEMI_COLON,
    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    PIPE,
    LT,
    GT,
    EQ,
    TILDE,
    AMP,


    STATIC,
    CONSTRUCTOR,
    FIELD,
    CLASS,
    VAR,
    FUNCTION,
    METHOD,
    INT,
    CHAR,
    BOOLEAN,
    VOID,
    TRUE,
    FALSE,
    NULL,
    THIS,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN;

    private static final Map<String, LexicalType> types = new HashMap<>();
    private static final Set<LexicalType> terminal = new HashSet<>();
    private static final Set<LexicalType> varDec = new HashSet<>();

    static {
        for (LexicalType t : values()) {
            types.put(t.name(), t);
        }
        terminal.addAll(
            List.of(
                CLASS_NAME, FUNCTION_NAME, METHOD_NAME, CONSTRUCTOR_NAME, ARGUMENT_NAME,
                LOCAL_VARIABLE_NAME, FIELD_VARIABLE_NAME, STATIC_VARIABLE_NAME,
                SYMBOL_TOKEN, INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD_CONSTANT, SUBROUTINE_NAME
            )
        );
        varDec.addAll(
            List.of(
                CLASS_VARIABLE_DECLARATION, FIELD_VARIABLE_DECLARATION, STATIC_VARIABLE_DECLARATION,
                LOCAL_VARIABLE_DECLARATION, ARGUMENT_DECLARATION, VARIABLE_DECLARATION
            )
        );
    }

    public static LexicalType symbol(SymbolToken s) {
        return valueOf(s.name());
    }

    public static LexicalType keyword(Keyword k) {
        return valueOf(k.name());
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Keyword keyword() {
        return Keyword.valueOf(name());
    }

    public DataType dataType() {
        if (this == IDENTIFIER) {
            return DataType.TYPE_CLASS;
        }
        return DataType.get(keyword());
    }
}

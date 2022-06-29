package compiler.constants;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum TokenType {
    KEY_WORD(TokenType::isKeyword, "keyword", 0),
    IDENTIFIER(TokenType::isIdentifier, "identifier", 2),
    STRING_CONSTANT(TokenType::isStringConst, "stringConstant", 3),
    SYMBOL(TokenType::isSymbol, "symbol", 1),
    INT_CONSTANT(TokenType::isIntConst, "integerConstant", 4);

    private static final Set<String> KEY_WORDS =
        new HashSet<>(
            List.of("class", "constructor", "function", "method", "field", "static", "var", "int",
                "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if",
                "else", "while", "return"));
    private static final Set<String> SYMBOLS =
        new HashSet<>(
            List.of("{", "}", "(", ")", "/", "[", "]", ".", ",", ";", "+", "-", "*", "&", "<", ">",
                "=", "~", "|"));
    private static final Predicate<String> STRING_CONST =
        Pattern.compile("\"((?!\\R).)+\"").asMatchPredicate();
    private static final Predicate<String> IDENTIFIER_PRE =
        Pattern.compile("^(?!\\d)[\\w\\d_]+").asMatchPredicate();
    private static final int INT_CONST_MAX = 1 << 16;
    private static final Pattern KEY_WORD_PATTERN =
        Pattern.compile("class|constructor|function|method|field|static|var|int" +
            "|char|boolean|void|true|false|null|this|let|do|if|else|while|return");
    private static final String SYMBOL_CHARS = "(\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\,|\\;|\\+|\\-|\\*|\\/|\\&|\\||\\<)";
    private static final String SYMBOL_CHARS2 = "[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final Pattern SYMBOL_SPLITTER =
        Pattern.compile(String.format("((?<=%s)|(?=%s))", SYMBOL_CHARS2, SYMBOL_CHARS2));
    private static final Pattern SYMBOL_PATTERN =
        Pattern.compile(SYMBOL_CHARS2);

    private static final PriorityQueue<TokenType> set = new PriorityQueue<>(Comparator.comparingInt(
        t -> t.priority));

    static {
        set.addAll(Arrays.asList(TokenType.values()));
    }

    private final String tokenName;
    private final Predicate<String> tester;
    private final int priority;

    TokenType(Predicate<String> tester, String tokenName, int priority) {
        this.tester = tester;
        this.tokenName = tokenName;
        this.priority = priority;
    }

    public static TokenType parse(String word) {
        for (TokenType type : set) {
            if (type.tester.test(word)) {
                return type;
            }
        }
        throw new IllegalArgumentException("unexpected Token:" + word);
    }

    private static boolean isKeyword(String v) {
        return KEY_WORDS.contains(v);
    }

    private static boolean isSymbol(String v) {
        return SYMBOLS.contains(v);
    }

    private static boolean isIntConst(String v) {
        if (!v.matches("[0-9]*")) {
            return false;
        }
        return Integer.parseInt(v) < INT_CONST_MAX;
    }

    private static boolean isStringConst(String v) {
        return STRING_CONST.test(v);
    }

    private static boolean isIdentifier(String v) {
        return IDENTIFIER_PRE.test(v);
    }

    public static Pattern symbolSplitter() {
        return SYMBOL_SPLITTER;
    }

    public String tokenName() {
        return tokenName;
    }

}

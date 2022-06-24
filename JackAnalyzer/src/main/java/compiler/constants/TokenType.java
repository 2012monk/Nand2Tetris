package compiler.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum TokenType {
    IDENTIFIER(TokenType::isIdentifier),
    STRING_CONSTANT(TokenType::isStringConst),
    SYMBOL(TokenType::isSymbol),
    INT_CONSTANT(TokenType::isIntConst),
    KEY_WORD(TokenType::isKeyword);

    private static final Set<String> KEY_WORDS =
        new HashSet<>(List.of("class","constructor","function","method","field","static","var","int"));
    private static final Set<String> SYMBOLS =
        new HashSet<>(List.of("{", "}", "(", ")" ,"/", "[", "]", ".", ",", ";", "+", "-", "*", "&", "<", ">", "=", "~", "|"));
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

    //    private static final String DELIMITERS =
//        "(//.*\\R*|/[*](.|\\s)*\\*/|(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\s+)";
//    private static final String SYMBOL_PATTERN =
//        "[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\*\\/\\&\\|\\<\\>\\=\\~]";

    private static final Map<Predicate<String>, TokenType> map = new HashMap<>();
    static {
        for (TokenType v : TokenType.values()) {
            map.put(v.tester, v);
        }
    }
    private final Predicate<String> tester;
    TokenType(Predicate<String> tester) {
        this.tester = tester;
    }

    public static TokenType parse(String word) {
        return map
            .entrySet()
            .stream()
            .filter(e -> e.getKey().test(word))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("unexpected Token"))
            .getValue();
    }

    private boolean isMatch(String v) {
        return tester.test(v);
    }

    private static boolean isKeyword(String v) {
        return KEY_WORDS.contains(v);
    }
    private static boolean isSymbol(String v) {
        return SYMBOLS.contains(v);
    }
    private static boolean isIntConst(String v) {
        if (!v.matches("\\d")) return false;
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
}

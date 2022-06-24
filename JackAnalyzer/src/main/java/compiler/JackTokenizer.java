package compiler;

import compiler.constants.Keyword;
import compiler.constants.TokenType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JackTokenizer {

    private static final Pattern delimiters =
        Pattern.compile("(//.*\\R*|/[*](.|\\s)*\\*/|(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\s+)");
    private static final String WS_OUTER_STRING_CONST = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\s+";
    private static final String COMMENTS = "//.*\\R*|/[*](.|\\s)*[*]/";
    private static final String DELIMITERS = "(" + COMMENTS + "|" + WS_OUTER_STRING_CONST + ")";
    private static final String SYMBOL_PATTERN = "[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String SYMBOL_SPLITTER =
        String.format("((?<=%s)|(?=%s))", SYMBOL_PATTERN, SYMBOL_PATTERN);
    private static final Pattern TOKEN_DELIMITER =
        Pattern.compile(DELIMITERS+"|"+SYMBOL_SPLITTER);
    private static final String ERR_NO_MORE_TOKEN = "no more token";
    private static final String ERR_TYPE_MISMATCH = "invalid type value";
    private final Queue<String> words;
    private TokenType currentType;

    public JackTokenizer(String path) {
        words = openFile(path);
        currentType = TokenType.parse(words.peek());
    }

    public TokenType tokenType() {
        return currentType;
    }

    public Keyword keyWord() {
        if (words.isEmpty()) throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        if (currentType != TokenType.KEY_WORD) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return Keyword.get(words.peek());
    }

    public char symbol() {
        if (words.isEmpty()) throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        if (currentType != TokenType.SYMBOL) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return words.peek().charAt(0);
    }

    public String identifier() {
        if (words.isEmpty()) throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        if (currentType != TokenType.IDENTIFIER) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return words.peek();
    }

    public int intVal() {
        if (words.isEmpty()) throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        if (currentType != TokenType.INT_CONSTANT) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return Integer.parseInt(words.peek());
    }

    public String stringVal() {
        if (words.isEmpty()) throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
//        if (currentType != TokenType.STRING_CONSTANT) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return words.peek();
    }

    public boolean hasMoreTokens() {
        return !words.isEmpty();
    }

    public void advance() {
        words.poll();
        if (words.isEmpty()) {
            currentType = null;
            return;
        }
        currentType = TokenType.parse(words.peek());
    }

    private Queue<String> openFile(String path) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return TOKEN_DELIMITER
                .splitAsStream(sb)
                .filter(x -> x!=null && !x.isEmpty() && !x.isBlank())
                .collect(Collectors.toCollection(LinkedList::new));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

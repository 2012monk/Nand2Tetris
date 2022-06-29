package compiler;

import compiler.ast.Token;
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
    private static final String COMMENTS =
        "(//.*+|/[*]{1,2}([^*]*[^*/]|\\R++|[*]+[^*/])*+[*]/)";
    private static final String DELIMITERS = "\\s++";
    private static final String SYMBOL_PATTERN = "[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String SYMBOL_SPLITTER =
        "((?<=" + SYMBOL_PATTERN + ")|(?=" + SYMBOL_PATTERN + "))";
    private static final Pattern WS_OUTER_QUOT_P =
        Pattern.compile(WS_OUTER_STRING_CONST+"|"+SYMBOL_SPLITTER, Pattern.MULTILINE);
    private static final Pattern TOKEN_DELIMITER =
        Pattern.compile(DELIMITERS + "|" + SYMBOL_SPLITTER, Pattern.MULTILINE);
    private static final Pattern COMMENTS_P =
        Pattern.compile(COMMENTS);
    private static final Pattern WS_PATTERN =
        Pattern.compile(WS_OUTER_STRING_CONST);
    private static final Pattern SYMBOL_P =
        Pattern.compile(SYMBOL_PATTERN);

    private static final String ERR_NO_MORE_TOKEN = "no more token";
    private static final String ERR_TYPE_MISMATCH = "invalid token";

    private final Queue<String> words;
    private TokenType currentType;

    public JackTokenizer(String path) {
        words = openFile(path);
        currentType = TokenType.parse(words.peek());
    }

    public TokenType tokenType() {
        return currentType;
    }

    public Keyword keyword() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        if (currentType != TokenType.KEY_WORD) {
            raiseInvalidType(TokenType.KEY_WORD, currentType);
        }
        return Keyword.get(words.peek());
    }

    public char symbol() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        if (currentType != TokenType.SYMBOL) {
            raiseInvalidType(TokenType.SYMBOL, currentType);
        }
        return words.peek().charAt(0);
    }

    private void raiseInvalidType(TokenType exp, TokenType actual) {
        throw new IllegalArgumentException(
            String.format("%s expected:%s actual:%s token:%s", ERR_TYPE_MISMATCH, exp, actual, words.peek()));
    }

    public String identifier() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        if (currentType != TokenType.IDENTIFIER) {
            raiseInvalidType(TokenType.IDENTIFIER, currentType);
        }
        return words.peek();
    }

    public int intVal() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        if (currentType != TokenType.INT_CONSTANT) {
            raiseInvalidType(TokenType.INT_CONSTANT, currentType);
        }
        return Integer.parseInt(words.peek());
    }

    public String stringVal() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        if (currentType != TokenType.STRING_CONSTANT) throw new IllegalArgumentException(ERR_TYPE_MISMATCH);
        return words.peek().replaceAll("\"", "");
    }

    public String raw() {
        if (words.isEmpty()) {
            throw new NoSuchElementException(ERR_NO_MORE_TOKEN);
        }
        return words.peek().replaceAll("\"", "");
    }

    public Token getToken() {
        return new Token(tokenType(), raw());
    }

    public boolean hasMoreTokens() {
        return !words.isEmpty();
    }

    public void advance() {
//        System.out.println(words.peek() + " word poped");
        words.poll();
        if (words.isEmpty()) {
            currentType = null;
            return;
        }
        currentType = TokenType.parse(words.peek());
    }

    // remove comments
    // split with quot
    // split with white space or symbols
    private Queue<String> openFile(String path) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String cm = sb.toString().replaceAll(COMMENTS, "");
            String[] sp = cm.split("\"");
            Queue<String> q = new LinkedList<>();
            Pattern p = Pattern.compile("\\s++|"+SYMBOL_SPLITTER);
            for (int i = 0; i < sp.length; i++) {
                if (i % 2 == 1) {
                    q.add("\"" + sp[i] + "\"");
                    continue;
                }
                q.addAll(p.splitAsStream(sp[i])
                    .filter(x -> x != null && !x.isEmpty() && !x.isBlank())
                    .collect(Collectors.toList())
                );
            }
            System.out.println(q);
            return q;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

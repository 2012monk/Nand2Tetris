package compiler;

import compiler.constants.TokenType;
import java.util.HashMap;
import java.util.Map;

public class JackXMLWriter {

    private static final Map<String, String> xmlConverts = new HashMap<>() {{
        put("<", "&lt;");
        put(">", "&gt;");
        put("\"", "&quot;");
        put("&", "&amp;");
    }};
    private static final String TERMINAL_FORMAT = "<%s> %s </%s>";
    private static final String NON_TERMINAL_OPEN = "<%s>";
    private static final String NON_TERMINAL_CLOSE = "</%s>";
    private final JackFileWriter writer;
    private boolean isNonIndent;
    private int indent = 0;
    private String offset = "";
    private String currentNode;

    public JackXMLWriter(String dest, boolean NON_INDENT) {
        this.isNonIndent = NON_INDENT;
        this.writer = new JackFileWriter(dest);
    }

    public JackXMLWriter(String dest) {
        this(dest, false);
    }

    public <T> void writeTerminal(TokenType type, T val) {
        writer.writeLines(offset+ formatTerminal(type, String.valueOf(val)));
        System.out.printf(offset + "%s\n", formatTerminal(type, String.valueOf(val)));
    }

    public void openNonTerminal(String identifier) {
        currentNode = identifier;
        writer.writeLines(offset+ String.format(NON_TERMINAL_OPEN, identifier));
        System.out.printf(offset + "%s\n", String.format(NON_TERMINAL_OPEN, identifier));
        pushIndent();
    }

    public void closeNonTerminal(String identifier) {
        popIndent();
        writer.writeLines(offset+ String.format(NON_TERMINAL_CLOSE, identifier));
        System.out.printf(offset + "%s\n", String.format(NON_TERMINAL_CLOSE, identifier));
    }

    private void pushIndent() {
        if (isNonIndent) {
            return;
        }
        indent += 2;
        offset = " ".repeat(Math.max(0, indent));
    }

    private void popIndent() {
        if (isNonIndent) {
            return;
        }
        indent -= 2;
        offset = " ".repeat(Math.max(0, indent));
    }

    private String formatTerminal(TokenType type, String val) {
        String t = type.tokenName();
        if (xmlConverts.containsKey(val)) {
            val = xmlConverts.get(val);
        }
        return String.format(TERMINAL_FORMAT, t, val, t);
    }

    public void close() {
        writer.close();
    }
}

package compiler;

import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.TokenType;
import java.util.HashMap;
import java.util.Map;

public class JackXMLWriter implements JackWriter {

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
    private int indent = 0;
    private String offset = "";

    public JackXMLWriter(String dest, boolean isAppend) {
        this.writer = new JackFileWriter(dest, isAppend);
    }

    public JackXMLWriter(String dest) {
        this(dest, false);
    }

    public <T> void writeTerminal(TokenType type, T val) {
        writer.writeLines(offset + formatTerminal(type.tokenName(), String.valueOf(val)));
        System.out.printf(offset + "%s\n", formatTerminal(type.tokenName(), String.valueOf(val)));
    }

    @Override
    public void writeTerminal(Identifier identifier) {
        if (identifier instanceof VariableIdentifier) {
            writeVariable((VariableIdentifier) identifier);
            return;
        }
        writer.writeLines(offset + formatTerminal(identifier.getType().id(), identifier.getName()));
        System.out.printf(offset + "%s\n",
            formatTerminal(identifier.getType().id(), identifier.getName()));
    }

    @Override
    public void writeVariable(VariableIdentifier identifier) {
        String tag = String.format("%s.%s%d", identifier.getParent().getName(),
            identifier.getType().id(), identifier.getIndex());
        String text = offset + formatTerminal(tag, identifier.getName());
        System.out.println(text);
        writer.writeLines(text);
    }

    public void openNonTerminal(String identifier) {
        writer.writeLines(offset + String.format(NON_TERMINAL_OPEN, identifier));
        System.out.printf(offset + "%s\n", String.format(NON_TERMINAL_OPEN, identifier));
        pushIndent();
    }

    public void closeNonTerminal(String identifier) {
        popIndent();
        writer.writeLines(offset + String.format(NON_TERMINAL_CLOSE, identifier));
        System.out.printf(offset + "%s\n", String.format(NON_TERMINAL_CLOSE, identifier));
    }

    protected void pushIndent() {
        indent += 2;
        offset = " ".repeat(Math.max(0, indent));
    }

    protected void popIndent() {
        indent -= 2;
        offset = " ".repeat(Math.max(0, indent));
    }

    private String formatTerminal(String type, String val) {
        if (xmlConverts.containsKey(val)) {
            val = xmlConverts.get(val);
        }
        return String.format(TERMINAL_FORMAT, type, val, type);
    }

    public void close() {
        writer.close();
    }
}

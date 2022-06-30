package compiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import compiler.constants.TokenType;
import compiler.core.JackTokenizer;
import compiler.utils.JackFileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JackTokenizerTest {

    private static final String TOKEN_FORMAT = "<%s> %s </%s>";
    private static final String START = "<tokens>";
    private static final String END = "</tokens>";
    private Map<String, String> xmlConverts = new HashMap<>() {{
        put("<", "&lt;");
        put(">", "&gt;");
        put("\"", "&quot;");
        put("&", "&amp;");
    }};

    void tokenize(String path) throws IOException {
        File file = new File(path);
        File[] files = new File[1];
        if (file.isDirectory()) {
            files = file.listFiles();
        } else {
            files[0] = file;
        }
        if (files == null) {
            throw new IllegalArgumentException();
        }
        for (File f : files) {
            if (!f.getName().endsWith(".jack")) {
                continue;
            }
            tokenizeFile(f.getAbsolutePath(), f.getName());
        }
    }


    void tokenizeFile(String path, String name) throws IOException {
        String dest = path.replace(".jack", "T.xml");
        String comp = dest.replace(name, "ans"+name);
        JackFileWriter writer = new JackFileWriter(dest);
        JackTokenizer tokenizer = new JackTokenizer(path);
        writer.writeLines(START);
        while (tokenizer.hasMoreTokens()) {
            TokenType type = tokenizer.tokenType();
            writer.writeLines(formatToken(type, tokenizer.stringVal()));
            tokenizer.advance();
        }
        writer.writeLines(END);
        writer.close();
        test(comp, dest);
    }

    void test(String src, String dest) throws IOException {
        assertArrayEquals(
            Files.readAllBytes(Path.of(src)),
            Files.readAllBytes(Path.of(dest))
        );
    }

    String formatToken(TokenType type, String val) {
        String t = type.tokenName();
        if (xmlConverts.containsKey(val)) {
            val = xmlConverts.get(val);
        }
        return String.format(TOKEN_FORMAT, t, val, t);
    }

    @Test
    void testRemoveWhiteSpaceComments() throws IOException {
        String testFile = "../10/ArrayTest/Main.jack";
        tokenize(testFile);
    }

    @Test
    void testSquareLDance() throws IOException {
        String testFile = "../10/ExpressionLessSquare/";
        tokenize(testFile);
    }

    @Test
    void testSquareDance() throws IOException {
        String testFile = "../10/Square/";
        tokenize(testFile);
    }
}
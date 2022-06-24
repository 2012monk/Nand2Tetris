package compiler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JackTokenizerTest {


    @Test
    void testRemoveWhiteSpaceComments() {
        String testFile = "../10/ArrayTest/Main.jack";
        JackTokenizer tokenizer = new JackTokenizer(testFile);
        while (tokenizer.hasMoreTokens()) {
//            System.out.printf("%s %s\n", tokenizer.tokenType(), tokenizer.stringVal());
            System.out.println(tokenizer.stringVal());
//            System.out.println(tokenizer.stringVal());
            tokenizer.advance();
        }
    }
}
package translator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import translator.constants.CommandType;

class ParserTest {


    void test(String file, String compFile) {

    }

    @Test
    void arithmeticTest() {
        String path = "src/test/resources/parser/arithmetic_1.vm";
        Parser parser = new Parser(path);
        List<String> ans = Arrays.asList("add", "sub", "lt", "gt");
        List<String> result = new ArrayList<>();
        while (parser.hasMoreCommands()) {
            result.add(parser.arg1());
            parser.advance();
        }
        assertArrayEquals(ans.toArray(), result.toArray());
    }

    @Test
    void exceptionTest() {
        String path = "src/test/resources/parser/arguments.vm";
        Parser parser = new Parser(path);
        while (parser.hasMoreCommands()) {
            if (parser.commandType().argc() < 2) {
                assertThrows(IllegalArgumentException.class, parser::arg2);
            }
            if (parser.commandType().argc() < 1 && parser.commandType() != CommandType.C_ARITHMETIC) {
                assertThrows(IllegalArgumentException.class, parser::arg1);
            }
            parser.advance();
        }
    }
}
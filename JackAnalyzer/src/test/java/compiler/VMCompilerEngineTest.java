package compiler;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class VMCompilerEngineTest {


    @Test
    void seven() {
        test("../11/Seven/");
    }

    @Test
    void complexArray() {
        test("../11/ComplexArrays/");
    }

    @Test
    void bin() {
        test("../11/ConvertToBin/");
    }

    @Test
    void square() {
        test("../11/Square/");
    }

    @Test
    void squareSquare() {
        test("../11/Square/Square.jack");

    }

    void test(String path) {
        new Compiler(path).compile();
        System.out.println(Path.of((path.replace(".jack", "C.vm"))));

    }
}

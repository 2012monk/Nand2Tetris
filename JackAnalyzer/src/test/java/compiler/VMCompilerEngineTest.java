package compiler;

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
    void average() {
        test("../11/Average/");
    }

    @Test
    void pong() {
        test("../11/Pong/");
    }

    void test(String path) {
        new Compiler(path).compile();

    }
}

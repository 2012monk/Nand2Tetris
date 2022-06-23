package translator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VMTranslatorTest {

    void translate(String path) {
        new VMTranslator(path).translate();
    }

    @Test
    void pointerTest() {
        String path = "../MemoryAccess/PointerTest/PointerTest.vm";
        translate(path);
    }
    @Test
    void basic() {
        String path = "../MemoryAccess/BasicTest/BasicTest.vm";
        translate(path);
    }

    @Test
    void ari() {
        String path = "../StackArithmetic/SimpleAdd/SimpleAdd.vm";
        translate(path);
    }
    @Test
    void stack() {
        String path = "../StackArithmetic/StackTest/StackTest.vm";
        translate(path);
    }

    @Test
    void staticTest() {
        String path = "../MemoryAccess/StaticTest/StaticTest.vm";
        translate(path);
    }

    @Test
    void basicLoop() {
        String path = "../../08/ProgramFlow/BasicLoop/BasicLoop.vm";
        translate(path);
    }

    @Test
    void fibonacci() {
        String path = "../../08/ProgramFlow/FibonacciSeries/FibonacciSeries.vm";
        translate(path);
    }
}
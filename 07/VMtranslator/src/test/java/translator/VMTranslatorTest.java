package translator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VMTranslatorTest {

    @Test
    void pointerTest() {
        String path = "../MemoryAccess/PointerTest/PointerTest.vm";
        VMTranslator t = new VMTranslator(path);
        t.translate();
    }
    @Test
    void basic() {
        String path = "../MemoryAccess/BasicTest/BasicTest.vm";
        VMTranslator t = new VMTranslator(path);
        t.translate();
    }

    @Test
    void ari() {
        String path = "../StackArithmetic/SimpleAdd/SimpleAdd.vm";
        VMTranslator t = new VMTranslator(path);
        t.translate();
    }
    @Test
    void stack() {
        String path = "../StackArithmetic/StackTest/StackTest.vm";
        VMTranslator t = new VMTranslator(path);
        t.translate();
    }

    @Test
    void staticTest() {
        String path = "../MemoryAccess/StaticTest/StaticTest.vm";
        VMTranslator t = new VMTranslator(path);
        t.translate();
    }
}
package compiler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompilationEngineTest {

    String file = "../10/Square/Main.jack";
    CompilationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new CompilationEngine(file, "test.xml");
    }

    @Test
    void compile() {
    }

    @Test
    void compileClass() {
        engine.compileClass();
    }

    @Test
    void compileClassVarDec() {
    }

    @Test
    void compileSubroutine() {
    }

    @Test
    void compileParameterList() {
    }

    @Test
    void compileVarDec() {
    }

    @Test
    void compileStatements() {
    }

    @Test
    void compileDo() {
    }

    @Test
    void compileLet() {
    }

    @Test
    void compileWhile() {
    }

    @Test
    void compileReturn() {
    }

    @Test
    void compileIf() {
    }

    @Test
    void compileExpr() {
    }

    @Test
    void compileTerm() {
    }

    @Test
    void compileExprList() {
    }
}
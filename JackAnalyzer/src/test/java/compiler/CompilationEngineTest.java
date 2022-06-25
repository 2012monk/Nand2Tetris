package compiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompilationEngineTest {

    String path = "../10/Square/";
    String name = "Main.jack";
    String compPath;
    //    String file = "../10/ExpressionLessSquare/Main.jack";
    CompilationEngine engine;

    void comp() throws IOException {
        assertArrayEquals(
            Files.readAllBytes(Path.of(compPath)),
            Files.readAllBytes(Path.of("test.xml"))
        );
    }

    @BeforeEach
    void setUp() {
        String dest = path + name.replace("jack", "xml");
        compPath = path + "ans" + name.replace("jack", "xml");
        engine = new CompilationEngine(path + name, dest);
    }

    @Test
    void compile() {
    }

    @Test
    void compileClass() throws IOException {
        engine.compileClass();
        comp();
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
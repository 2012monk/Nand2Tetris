import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AssemblerTest {

    void test(String asm, String hack, String compFile) throws IOException {

        Assembler assembler = new Assembler(asm);
        assembler.assemble();
        byte[] tc = Files.readAllBytes(Path.of(hack));
        byte[] comp = Files.readAllBytes(Path.of(compFile));
        assertArrayEquals(comp, tc);
    }

    @Test
    void assembleTest() throws IOException {
        String prefix = "src/test/java/add/";
        String testFile = prefix+ "Add.asm";
        String destFilePath = prefix+"Add.hack";
        String compFilePath = prefix+"compAdd.hack";
        test(testFile, destFilePath, compFilePath);
    }

    @Test
    void t2() throws IOException {
        String prefix = "src/test/java/rect/";
        String testFile = prefix+ "Rect.asm";
        String destFilePath = prefix+"Rect.hack";
        String compFilePath = prefix+"compRect.hack";
        test(testFile, destFilePath, compFilePath);
    }

    @Test
    void max() throws IOException {
        String prefix = "src/test/java/max/";
        test(prefix+"Max.asm", prefix + "Max.hack", prefix + "compMax.hack");
    }

    @Test
    void pong() throws IOException {
        String prefix = "src/test/java/pong/";
        test(prefix + "Pong.asm", prefix+"Pong.hack", prefix+"compPong.hack");
    }
}
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AssemblerTest {

    @Test
    void assembleTest() {
        String testFile = "Add.asm";
        String destFilePath = "Add.hack";
        String compFilePath = "compAdd.hack";
        Assembler assembler = new Assembler();
        assembler.assemble(testFile);
        assertDoesNotThrow(() -> {
            byte[] tc = Files.readAllBytes(Path.of(destFilePath));
            byte[] comp = Files.readAllBytes(Path.of(compFilePath));
            assertEquals(comp, tc);
        });
    }
}
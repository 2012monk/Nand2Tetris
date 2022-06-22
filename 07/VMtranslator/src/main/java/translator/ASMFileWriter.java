package translator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ASMFileWriter {

    private final BufferedWriter bw;

    public ASMFileWriter(String destPath) {
        try {
            bw = new BufferedWriter(new FileWriter(destPath, false));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ASMFileWriter writeLines(String ...lines) {
        return writeLine(String.join("\n", lines));
    }

    public ASMFileWriter writeLine(String line) {
        write(line);
        write("\n");
        return this;
    }

    public ASMFileWriter appendLine(String line) {
        write(line);
        return this;
    }

    private void write(String line) {
        try {
            bw.write(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

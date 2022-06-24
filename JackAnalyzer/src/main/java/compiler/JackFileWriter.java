package compiler;


import java.io.FileWriter;
import java.io.IOException;

public class JackFileWriter {

    private final FileWriter fw;

    public JackFileWriter(String path) {
        try {
            fw = new FileWriter(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLines(String ...lines) {
        try {
            fw.write(String.join("\n", lines));
            fw.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendLine(String line) {
        try {
            fw.write(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

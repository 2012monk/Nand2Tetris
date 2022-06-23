package translator;

import static translator.constants.CommandType.C_ARITHMETIC;
import static translator.constants.CommandType.C_GOTO;
import static translator.constants.CommandType.C_IF;
import static translator.constants.CommandType.C_LABEL;
import static translator.constants.CommandType.C_POP;
import static translator.constants.CommandType.C_PUSH;

import java.io.File;
import translator.constants.CommandType;

public class VMTranslator {

    private final CodeWriter writer;
    private final File[] files;

    public VMTranslator(String path) {
        File file = new File(path);
        String destPath = path.replace(".vm", ".asm");
        File[] files = new File[]{file};
        if (!file.exists()) {
            throw new IllegalArgumentException("file not exists");
        }
        if (file.isDirectory()) {
            destPath = path + file.getName() + ".asm";
            files = file.listFiles();
        }
        if (files == null) {
            throw new IllegalArgumentException("directory is empty!");
        }
        this.files = files;
        writer = new CodeWriter(destPath);
    }

    public void translate() {
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".vm")) {
                continue;
            }
            writer.setFileName(name.replace(".vm", ""));
            translate(new Parser(file.getAbsolutePath()));
        }
        writer.close();
    }

    private void translate(Parser parser) {
        while (parser.hasMoreCommands()) {
            CommandType t = parser.commandType();
            if (t == C_ARITHMETIC) {
                writer.writeArithmetic(parser.arg1());
            }
            if (t == C_PUSH || t == C_POP) {
                writer.writePushPop(t, parser.arg1(), parser.arg2());
            }
            if (t == C_GOTO) {
                writer.writeGoto(parser.arg1());
            }
            if (t == C_IF) {
                writer.writeIf(parser.arg1());
            }
            if (t == C_LABEL) {
                writer.writeLabel(parser.arg1());
            }
            parser.advance();
        }
    }
}

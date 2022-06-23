package translator;


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
        writer.writeInit();
        while (parser.hasMoreCommands()) {
            CommandType t = parser.commandType();
            t.translate(writer, parser);
            parser.advance();
        }
    }
}

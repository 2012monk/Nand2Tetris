package compiler.core;

import compiler.utils.JackFileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compiler {

    private CompilationEngine engine;
    private List<Path> files;

    public Compiler(String path){
        try {
            openFiles(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void compileFile(String path) {
        String dest = path.replace(".jack", ".vm");
        engine = new CompilationEngine(new JackTokenizer(path), new JackFileWriter(dest));
        engine.compile();
    }

    private void openFiles(String path) throws IOException {
        Path p = Path.of(path);
        if (!Files.isDirectory(p)) {
            files = Collections.singletonList(p);
            return;
        }
        try (Stream<Path> ps = Files.walk(Path.of(path))) {
            files = ps.filter(f -> f.toString().endsWith(".jack"))
                .collect(Collectors.toList());
        }
        System.out.println(files);
    }

    public void compile() {
        for (Path path : files) {
            compileFile(path.toString());
        }
    }
}

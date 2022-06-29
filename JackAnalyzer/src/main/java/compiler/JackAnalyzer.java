package compiler;

public class JackAnalyzer {

    private CompilationEngineV1 engine;

    public JackAnalyzer(String path) {
        if (!path.endsWith(".jack")) {
            throw new IllegalArgumentException("file must be .jack");
        }
        String dest = path.replace("jack", "xml");
        engine = new CompilationEngineV1(new JackTokenizer(path), new JackXMLWriter(dest, false));
    }

    public void analyze() {
        engine.compile();
    }
}

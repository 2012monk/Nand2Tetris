package compiler;

public class JackAnalyzer {

    private CompilationEngine engine;

    public JackAnalyzer(String path) {
        if (!path.endsWith(".jack")) {
            throw new IllegalArgumentException("file must be .jack");
        }
        String dest = path.replace("jack", "xml");
        engine = new CompilationEngine(new JackTokenizer(path), new JackXMLWriter(dest, false));
    }

    public void analyze() {
        engine.compile();
    }
}

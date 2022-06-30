package compiler;

import compiler.ast.ASTNode;
import compiler.core.JackTokenizer;
import compiler.parser.Parser;
import compiler.utils.XMLUtil;

public class JackAnalyzer {

    private Parser parser;
    private String dest;

    public JackAnalyzer(String path) {
        if (!path.endsWith(".jack")) {
            throw new IllegalArgumentException("file must be .jack");
        }
        dest = path.replace("jack", "xml");
        parser = new Parser(new JackTokenizer(path));
    }

    public void analyze() {
        try {
            XMLUtil.writeXml(dest, getAST());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ASTNode getAST() {
        return parser.parse();
    }
}

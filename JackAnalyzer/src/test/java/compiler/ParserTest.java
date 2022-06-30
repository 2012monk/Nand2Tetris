package compiler;

import compiler.ast.ASTNode;
import compiler.core.JackTokenizer;
import compiler.parser.Parser;
import compiler.utils.XMLUtil;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserTest {

    String compPath;
    Parser engine;

    public static void writeXml(String dest, ASTNode node)
        throws ParserConfigurationException, IOException, TransformerException {
        Document d = XMLUtil.convertAST(node);
        XMLUtil.writeXml(dest, d);
    }

    public static void comp(ASTNode node, String compPath) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = f.newDocumentBuilder();
        Document expect = builder.parse(compPath);
        Document actual = XMLUtil.convertAST(node);
        Element compNode = expect.getDocumentElement();
        Element actualNode = actual.getDocumentElement();
        assert comp(compNode, actualNode);
    }

    public static boolean comp(Node e, Node a) {
        if (e == null && a == null) {
            return true;
        }
        if (e == null || a == null) {
            return false;
        }

        String ename = e.getNodeName(), aname = a.getNodeName();
//        if (ename != null && !ename.equals(aname)) return false;
        NodeList elist = e.getChildNodes();
        NodeList alist = a.getChildNodes();
        if (elist.getLength() != alist.getLength()) {
            return false;
        }
        if (elist.getLength() == 0 && !e.getTextContent().equals(a.getTextContent())) {
            return false;
        }
        for (int i = 0; i < elist.getLength(); i++) {
            if (!comp(elist.item(i), alist.item(i))) {
                return false;
            }
        }
        return true;
    }

    @Test
    void squareMain() throws Exception {
        test("../10/Square/", "Main.jack");
    }

    @Test
    void squareGame() throws Exception {
        test("../10/Square/", "SquareGame.jack");

    }

    @Test
    void square() throws Exception {
        test("../10/Square/", "Square.jack");
    }

    @Test
    void arrayMain() throws Exception {
        test("../10/ArrayTest/", "Main.jack");
    }

    void test(String path, String name)
        throws ParserConfigurationException, IOException, TransformerException {
        compPath = path + "ans" + name.replace("jack", "xml");
        engine = new Parser(new JackTokenizer(path + name));
        ASTNode node = engine.parse();
        String dest = "./src/test/resources/result.xml";
        writeXml(dest, node);
    }
}

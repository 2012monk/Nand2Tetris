package compiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import compiler.core.CompilationEngineV1;
import compiler.core.JackTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class CompilationEngineXMLTest {

    String compPath;
    String dest;
    CompilationEngineV1 engine;

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

    void test(String path, String  name) throws Exception {
        dest = path + name.replace("jack", "xml");
        compPath = path + "ans" + name.replace("jack", "xml");
        engine = new CompilationEngineV1(new JackTokenizer(path + name), new JackXMLWriter(dest));
        engine.compile();
        comp();
    }

    void comp() throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = f.newDocumentBuilder();
        Document expect = builder.parse(compPath);
        Document actual = builder.parse(dest);
        Element compNode = expect.getDocumentElement();
        Element actualNode = actual.getDocumentElement();
        assert comp(compNode, actualNode);
    }

    boolean comp(Node e, Node a) {
        if (e == null && a == null) return true;
        if (e == null || a == null) return false;

        String ename = e.getNodeName(), aname = a.getNodeName();
//        if (ename != null && !ename.equals(aname)) return false;
        NodeList elist = e.getChildNodes();
        NodeList alist = a.getChildNodes();
        if (elist.getLength() != alist.getLength()) return false;
        if (elist.getLength() == 0 && !e.getTextContent().equals(a.getTextContent()))  return false;
        for (int i = 0; i < elist.getLength(); i++) {
            if (!comp(elist.item(i), alist.item(i))) return false;
        }
        return true;
    }
}
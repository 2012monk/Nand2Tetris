package compiler;

import compiler.ast.ASTNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLUtil {


    public static Document convertAST(ASTNode node) throws ParserConfigurationException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        Document doc = f.newDocumentBuilder().newDocument();
        doc.appendChild(parse(node, doc));
        return doc;
    }

    private static Element parse(ASTNode node, Document doc) {
        Element e;
        if (node.children().isEmpty() && node.isTerminal()) {
            e = doc.createElement(node.getType().getName());
            e.setTextContent(node.getToken().getValue());
            return e;
        }
        e = doc.createElement(node.getType().getName());
        for (ASTNode child : node.children()) {
            e.appendChild(parse(child, doc));
        }
        return e;
    }

    public static Document convertAST(String path) throws ParserConfigurationException {
        Parser parser = new Parser(new JackTokenizer(path));
        return convertAST(parser.parse());
    }

    public static void writeXml(String dest, ASTNode node)
        throws ParserConfigurationException, IOException, TransformerException {
        writeXml(dest, convertAST(node));
    }

    public static void writeXml(String dest, Document doc)
        throws TransformerException, IOException {
        TransformerFactory f = TransformerFactory.newInstance();
        Transformer t = f.newTransformer();
        DOMSource src = new DOMSource(doc);
        StreamResult result = new StreamResult(Files.newOutputStream(Path.of(dest)));
        t.transform(src, result);
    }
}

package compiler.parser;

import compiler.ast.ASTNode;
import compiler.exceptions.ParseFailedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ParserUtil {


    public static ASTNode orNode(List<Supplier<ASTNode>> suppliers) {
        for (Supplier<ASTNode> s : suppliers) {
            try {
                return s.get();
            } catch (Exception ignored) {
            }
        }
        throw new ParseFailedException();
    }

    public static List<ASTNode> optionalNode(Supplier<ASTNode> suppliers) {
        try {
            return List.of(suppliers.get());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<ASTNode> optionalNodes(Supplier<List<ASTNode>> suppliers) {
        try {
            return suppliers.get();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<ASTNode> zeroOrMoreNodes(Supplier<List<ASTNode>> r) {
        List<ASTNode> ret = new ArrayList<>();
        while (true) {
            try {
                ret.addAll(r.get());
            } catch (Exception ignored) {
                return ret;
            }
        }
    }

    public static List<ASTNode> zeroOrMoreNode(Supplier<ASTNode> r) {
        List<ASTNode> ret = new ArrayList<>();

        while (true) {
            try {
                ret.add(r.get());
            } catch (Exception ignored) {
                return ret;
            }
        }
    }
}

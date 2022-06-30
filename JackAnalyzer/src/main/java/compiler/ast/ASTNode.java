package compiler.ast;

import compiler.constants.LexicalType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ASTNode {

    private LexicalType type;
    private LinkedList<ASTNode> children = new LinkedList<>();
    private Token token;

    public ASTNode(LexicalType type) {
        this.type = type;
    }

    public ASTNode(LexicalType type, Token token) {
        this.type = type;
        this.token = token;
    }

    public ASTNode add(ASTNode node) {
        children.add(node);
        return this;
    }

    public ASTNode add(List<ASTNode> nodes) {
        children.addAll(nodes);
        return this;
    }

    public List<ASTNode> children() {
        return children;
    }

    public List<ASTNode> reverseChildren() {
        List<ASTNode> r = new ArrayList<>(children);
        Collections.reverse(r);
        return r;
    }

    public LexicalType getType() {
        return type;
    }

    public ASTNode addAll(List<ASTNode> nodes) {
        children.addAll(nodes);
        return this;
    }

    public void changeType(LexicalType type) {
        this.type = type;
    }

//    public ASTNode add(Optional<ASTNode> optional) {
//        if (optional.isEmpty()) {
//            return this;
//        }
//        return add(optional.get());
//    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        return token.getValue();
    }

    public boolean isTerminal() {
        return token != null;
    }

    public ASTNode getFirstChild() {
        return children.getFirst();
    }

    public ASTNode lastChild() {
        return children.getLast();
    }

    // post order traversal
    public ASTNode findFirst(LexicalType type) {
        for (ASTNode c : children) {
            ASTNode r = c.findFirst(type);
            if (r != null) {
                return r;
            }
        }
        if (type == this.type) {
            return this;
        }
        return null;
    }

    public ASTNode findPreOrder(LexicalType type) {
        if (type == this.type) {
            return this;
        }
        for (ASTNode c : children) {
            ASTNode r = c.findPreOrder(type);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public Optional<ASTNode> findInImmediate(LexicalType type) {
        return children.stream()
            .filter(t -> t.getType() == type)
            .findFirst();
    }

    public List<ASTNode> findChildren(LexicalType type) {
        List<ASTNode> r = new ArrayList<>();
        findChildren(type, r);
        return r;
    }

    public void findChildren(LexicalType type, List<ASTNode> r) {
        for (ASTNode child : children) {
            child.findChildren(type, r);
        }
        if (type == this.type) {
            r.add(this);
        }
    }

    public ASTNode getLastChild() {
        return children.getLast();
    }

    public ASTNode getChild(int i) {
        return children.get(i);
    }
}

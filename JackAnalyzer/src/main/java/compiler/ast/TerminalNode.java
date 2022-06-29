package compiler.ast;

import compiler.constants.LexicalType;

public class TerminalNode extends ASTNode {

    private final Token token;

    public TerminalNode(LexicalType type, Token token) {
        super(type);
        this.token = token;
    }

//    public TerminalNode(Token token) {
//        super(TERMINAL);
//        this.token = token;
//    }

    public Token getToken() {
        return token;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}

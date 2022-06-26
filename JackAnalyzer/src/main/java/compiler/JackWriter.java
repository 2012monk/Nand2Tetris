package compiler;

import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.IdentifierType;
import compiler.constants.TokenType;

public interface JackWriter {

    <T> void writeTerminal(TokenType type, T val);
    void writeTerminal(Identifier identifier);
    void writeVariable(VariableIdentifier identifier);
    void openNonTerminal(String identifier);
    void closeNonTerminal(String identifier);
    void close();
}

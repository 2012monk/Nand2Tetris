package compiler;

import compiler.constants.TokenType;

public interface JackWriter {

    <T> void writeTerminal(TokenType type, T val);
    void openNonTerminal(String identifier);
    void closeNonTerminal(String identifier);
    void close();
}

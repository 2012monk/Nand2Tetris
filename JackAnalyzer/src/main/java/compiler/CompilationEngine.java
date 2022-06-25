package compiler;

import static compiler.constants.IdentifierType.*;
import static compiler.constants.Keyword.*;
import static compiler.constants.TokenType.*;

import compiler.constants.IdentifierType;
import compiler.constants.Keyword;
import compiler.constants.TokenType;

public class CompilationEngine {

    private static final String ERR_INVALID_SYNTAX = "invalid syntax";
    private JackTokenizer jtz;
    private JackWriter writer;
    private IdentifierTable table;
    private int lineNo = 0;

    public CompilationEngine(JackTokenizer jtz, JackWriter writer) {
        this.writer = writer;
        this.jtz = jtz;
        table = new IdentifierTable();
    }

    public CompilationEngine(String src, String dest) {
        jtz = new JackTokenizer(src);
        writer = new JackXMLWriter(dest);
        table = new IdentifierTable();
    }

    public void compile() {
        compileClass();
        writer.close();
    }

    public void compileClass() {
        addNode("class");
        compileKeyword(CLASS);
        declareIdentifier(CLASS_NAME);
        compileSymbol('{');
        oneOrZero(() -> varRepeat(this::compileClassVarDec));
        oneOrZero(() -> varRepeat(this::compileSubroutine));
        compileSymbol('}');
        closeNode("class");
    }

    public void compileClassVarDec() {
        if (jtz.keyword() != STATIC && jtz.keyword() != FIELD) {
            raiseErrInvalidSyntax();
        }
        addNode("classVarDec");
        compileKeyword(STATIC, FIELD);
        compileType();
        declareIdentifier(VAR_NAME);
        varRepeat(() -> {
            compileSymbol(',');
            declareIdentifier(VAR_NAME);
        });
        compileSymbol(';');
        closeNode("classVarDec");
    }

    private void compileKeyword(Keyword... keywords) {
        Keyword cur = jtz.keyword();
        for (Keyword k : keywords) {
            if (cur == k) {
                writer.writeTerminal(KEY_WORD, cur.keyword());
                jtz.advance();
                return;
            }
        }
        raiseErrInvalidSyntax();
    }

    public void compileSubroutine() {
        if (jtz.keyword() != CONSTRUCTOR && jtz.keyword() != METHOD && jtz.keyword() != FUNCTION) {
            raiseErrInvalidSyntax();
        }
        addNode("subroutineDec");
        compileKeyword(CONSTRUCTOR, FUNCTION, METHOD);
        or(() -> compileKeyword(VOID), this::compileType);
        declareIdentifier(SUB_ROUTINE_NAME);
        compileSymbol('(');
        compileParameterList();
        compileSymbol(')');
        compileSubroutineBody();
        closeNode("subroutineDec");
    }

    public void compileSubroutineBody() {
        addNode("subroutineBody");
        compileSymbol('{');
        oneOrZero(() -> varRepeat(this::compileVarDec));
        compileStatements();
        compileSymbol('}');
        closeNode("subroutineBody");
    }

    public void compileParameterList() {
        addNode("parameterList");
        oneOrZero(() -> {
            compileType();
            declareIdentifier(VAR_NAME);
            zeroOrMore(() -> {
                compileSymbol(',');
                compileType();
                declareIdentifier(VAR_NAME);
            });
        });
        closeNode("parameterList");
    }

    public void compileVarDec() {
        if (jtz.keyword() != VAR) {
            raiseErrInvalidSyntax();
        }
        addNode("varDec");
        compileKeyword(VAR);
        compileType();
        declareIdentifier(VAR_NAME);
        varRepeat(() -> {
            compileSymbol(',');
            compileType();
            declareIdentifier(VAR_NAME);
        });
        compileSymbol(';');
        closeNode("varDec");
    }

    public void compileStatements() {
        addNode("statements");
        varRepeat(() -> or(this::compileLet, this::compileWhile, this::compileIf,
            this::compileDo, this::compileReturn));
        closeNode("statements");
    }

    public void compileLet() {
        if (jtz.keyword() != LET) {
            raiseErrInvalidSyntax();
        }
        addNode("letStatement");
        compileKeyword(LET);
        declareIdentifier(VAR_NAME);
        oneOrZero(() -> {
            compileSymbol('[');
            compileExpr();
            compileSymbol(']');
        });
        compileSymbol('=');
        compileExpr();
        compileSymbol(';');
        closeNode("letStatement");
    }

    public void compileIf() {
        if (jtz.keyword() != IF) {
            raiseErrInvalidSyntax();
        }
        addNode("ifStatement");
        compileKeyword(IF);
        compileSymbol('(');
        compileExpr();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');
        oneOrZero(() -> {
            compileKeyword(ELSE);
            compileSymbol('{');
            compileStatements();
            compileSymbol('}');
        });
        closeNode("ifStatement");
    }

    public void compileWhile() {
        if (jtz.keyword() != WHILE) {
            raiseErrInvalidSyntax();
        }
        addNode("whileStatement");
        compileKeyword(WHILE);
        compileSymbol('(');
        compileExpr();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');
        closeNode("whileStatement");
    }

    public void compileDo() {
        if (jtz.keyword() != DO) {
            raiseErrInvalidSyntax();
        }
        addNode("doStatement");
        compileKeyword(DO);
        compileSubroutineCall();
        compileSymbol(';');
        closeNode("doStatement");
    }

    private void compileSubroutineCall() {
        referenceIdentifier();
        oneOrZero(() -> {
            compileSymbol('.');
            referenceIdentifier();
        });
        compileSymbol('(');
        compileExprList();
        compileSymbol(')');
    }


    public void compileReturn() {
        if (jtz.keyword() != RETURN) {
            raiseErrInvalidSyntax();
        }
        addNode("returnStatement");
        compileKeyword(RETURN);
        oneOrZero(this::compileExpr);
        compileSymbol(';');
        closeNode("returnStatement");

    }

    public void compileExpr() {
        TokenType type = jtz.tokenType();
        if (type != INT_CONSTANT && type != STRING_CONSTANT && type != IDENTIFIER
            && type != KEY_WORD && type != SYMBOL) {
            raiseErrInvalidSyntax();
        }
        if (type == KEY_WORD && (jtz.keyword() != TRUE && jtz.keyword() != FALSE &&
            jtz.keyword() != NULL && jtz.keyword() != THIS)) {
            raiseErrInvalidSyntax();
        }
        if (type == SYMBOL && (jtz.symbol() != '-' && jtz.symbol() != '~' && jtz.symbol() != '(')) {
            raiseErrInvalidSyntax();
        }
        addNode("expression");
        compileTerm();
        zeroOrMore(() -> {
            compileOp();
            compileTerm();
        });
        closeNode("expression");
    }

    public void compileTerm() {
        addNode("term");
        Runnable intConst = () -> {
            writer.writeTerminal(INT_CONSTANT, jtz.intVal());
            jtz.advance();
        };
        Runnable strConst = () -> {
            writer.writeTerminal(STRING_CONSTANT, jtz.stringVal());
            jtz.advance();
        };
        Runnable expr = () -> {
            compileSymbol('(');
            compileExpr();
            compileSymbol(')');
        };
        Runnable unaryOp = () -> {
            compileUnaryOp();
            compileTerm();
        };
        Runnable keyConst = () -> compileKeyword(TRUE, FALSE, NULL, THIS);
        Runnable refExpr = () -> {
            compileSymbol('[');
            compileExpr();
            compileSymbol(']');
        };
        Runnable subCall = () -> {
            oneOrZero(() -> {
                compileSymbol('.');
                referenceIdentifier();
            });
            compileSymbol('(');
            compileExprList();
            compileSymbol(')');
        };
        Runnable refCall = () -> {
            referenceIdentifier();
            oneOrZero(() -> or(subCall, refExpr));
        };
        or(keyConst, intConst, strConst, refCall, expr, unaryOp);
        closeNode("term");
    }

    private void compileUnaryOp() {
        compileSymbol('-', '~');
    }

    private void compileOp() {
        compileSymbol('+', '-', '*', '/', '/', '&', '|', '<', '>', '=');
    }

    public void compileExprList() {
        addNode("expressionList");
        Runnable multiVars = () -> {
            compileSymbol(',');
            compileExpr();
        };
        oneOrZero(this::compileExpr);
        zeroOrMore(multiVars);
        closeNode("expressionList");
    }

    private void compileType() {
        or(() -> compileKeyword(INT, CHAR, BOOLEAN),
            this::referenceIdentifier);
    }

    private void compileSymbol(char... cs) {
        for (char c : cs) {
            if (jtz.symbol() != c) {
                continue;
            }
            writer.writeTerminal(jtz.tokenType(), jtz.symbol());
            jtz.advance();
            return;
        }
        raiseErrInvalidSyntax(String.valueOf(cs));
    }

    private void declareIdentifier(IdentifierType type) {
        // TODO check if violate the identifier rules
        String id = jtz.identifier();
        table.addIdentifier(id, type);
        writer.writeTerminal(IDENTIFIER, jtz.identifier());
        jtz.advance();
    }

    private void referenceIdentifier() {
        String id = jtz.identifier();
        writer.writeTerminal(IDENTIFIER, jtz.identifier());
        jtz.advance();
    }

    private void raiseErrInvalidSyntax(String... expected) {
        String e = String.join(" or ", expected);
        String actual = jtz.tokenType().name();
        throw new IllegalArgumentException(String.format("%s expected:%s actual: %s %s",
            ERR_INVALID_SYNTAX, e, actual, jtz.raw()));
    }

    private void addNode(String id) {
        writer.openNonTerminal(id);
        table.openSection();
    }

    private void closeNode(String id) {
        writer.closeNonTerminal(id);
        table.closeSection();
    }

    private void oneOrZero(Runnable rs) {
        try {
            rs.run();
        } catch (Exception ignore) {
        }
    }

    private void or(Runnable... rs) {
        for (Runnable r : rs) {
            try {
                r.run();
                return;
            } catch (IllegalArgumentException ignored) {
            }
        }
        raiseErrInvalidSyntax();
    }

    private void zeroOrMore(Runnable... rs) {
        while (true) {
            try {
                for (Runnable r : rs) {
                    r.run();
                }
            } catch (Exception ignored) {
                return;
            }
        }
    }

    private void varRepeat(Runnable r) {
        while (true) {
            try {
                r.run();
            } catch (Exception e) {
                return;
            }
        }
    }

}


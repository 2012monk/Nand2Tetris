package compiler;

import static compiler.constants.IdentifierType.CLASS_NAME;
import static compiler.constants.IdentifierType.SUB_ROUTINE_NAME;
import static compiler.constants.IdentifierType.VAR_NAME;
import static compiler.constants.Keyword.BOOLEAN;
import static compiler.constants.Keyword.CHAR;
import static compiler.constants.Keyword.CLASS;
import static compiler.constants.Keyword.CONSTRUCTOR;
import static compiler.constants.Keyword.DO;
import static compiler.constants.Keyword.ELSE;
import static compiler.constants.Keyword.FALSE;
import static compiler.constants.Keyword.FIELD;
import static compiler.constants.Keyword.FUNCTION;
import static compiler.constants.Keyword.IF;
import static compiler.constants.Keyword.INT;
import static compiler.constants.Keyword.LET;
import static compiler.constants.Keyword.METHOD;
import static compiler.constants.Keyword.NULL;
import static compiler.constants.Keyword.RETURN;
import static compiler.constants.Keyword.STATIC;
import static compiler.constants.Keyword.THIS;
import static compiler.constants.Keyword.TRUE;
import static compiler.constants.Keyword.VAR;
import static compiler.constants.Keyword.VOID;
import static compiler.constants.Keyword.WHILE;
import static compiler.constants.TokenType.IDENTIFIER;
import static compiler.constants.TokenType.INT_CONSTANT;
import static compiler.constants.TokenType.KEY_WORD;
import static compiler.constants.TokenType.STRING_CONSTANT;

import compiler.constants.IdentifierType;
import compiler.constants.Keyword;

public class CompilationEngine {

    private static final String ERR_INVALID_SYNTAX = "invalid syntax";
    private JackTokenizer jtz;
    private JackXMLWriter writer;
    private IdentifierTable table;
    private int lineNo = 0;

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
        open("class");
        compileKeyword(CLASS);
        declareIdentifier(CLASS_NAME);
        compileSymbol('{');
        and(() -> oneOrZero(this::compileClassVarDec),
            () -> oneOrZero(this::compileSubroutine)
        );
        compileSymbol('}');
        close("class");
    }

    public void compileClassVarDec() {
        if (jtz.keyword() != STATIC && jtz.keyword() != FIELD) {
            raiseErrInvalidSyntax();
        }
        open("classVarDec");
        compileKeyword(STATIC, FIELD);
        compileType();
        declareIdentifier(VAR_NAME);
        varRepeat(() -> {
            compileSymbol(',');
            declareIdentifier(VAR_NAME);
        });
        compileSymbol(';');
        close("classVarDec");
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
        open("subroutineDec");
        compileKeyword(CONSTRUCTOR, FUNCTION, METHOD);
        or(() -> compileKeyword(VOID), this::compileType);
        declareIdentifier(SUB_ROUTINE_NAME);
        compileSymbol('(');
        compileParameterList();
        compileSymbol(')');
        compileSubroutineBody();
        close("subroutineDec");
    }

    public void compileSubroutineBody() {
        open("subroutineBody");
        compileSymbol('{');
        oneOrZero(this::compileVarDec);
        compileStatements();
        compileSymbol('}');
        close("subroutineBody");
    }

    public void compileParameterList() {
        open("parameterList");
        oneOrZero(() -> {
            compileType();
            declareIdentifier(VAR_NAME);
            varRepeat(() -> {
                compileSymbol(',');
                compileParameterList();
            });
        });
        close("parameterList");
    }

    public void compileVarDec() {
        if (jtz.keyword() != VAR) {
            raiseErrInvalidSyntax();
        }
        open("varDec");
        compileKeyword(VAR);
        compileType();
        declareIdentifier(VAR_NAME);
        varRepeat(() -> {
            compileSymbol(',');
            compileType();
            declareIdentifier(VAR_NAME);
        });
        compileSymbol(';');
        close("varDec");
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

    public void compileStatements() {
        or(this::compileLet, this::compileWhile, this::compileIf,
            this::compileDo, this::compileReturn);
    }

    public void compileLet() {
        if (jtz.keyword() != LET) {
            raiseErrInvalidSyntax();
        }
        open("letStatement");
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
        close("letStatement");
    }

    public void compileIf() {
        if (jtz.keyword() != IF) {
            raiseErrInvalidSyntax();
        }
        open("ifStatement");
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
        close("ifStatement");
    }

    public void compileWhile() {
        if (jtz.keyword() != WHILE) {
            raiseErrInvalidSyntax();
        }
        open("whileStatement");
        compileSymbol('(');
        compileExpr();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');
        close("whileStatement");
    }

    public void compileDo() {
        if (jtz.keyword() != DO) {
            raiseErrInvalidSyntax();
        }
        open("do");
        compileKeyword(DO);
        compileSubroutineCall();
        compileSymbol(';');
        close("do");
    }

    private void compileSubroutineCall() {
        Runnable callSubroutine = () ->
            referenceIdentifier(SUB_ROUTINE_NAME);
        Runnable callClassSubroutine = () -> {
            referenceIdentifier(CLASS_NAME);
            compileSymbol('.');
            referenceIdentifier(SUB_ROUTINE_NAME);
        };
        and(() -> or(callClassSubroutine, callSubroutine),
            () -> {
                compileSymbol('(');
                compileExprList();
                compileSymbol(')');
            });
    }


    public void compileReturn() {
        if (jtz.keyword() != RETURN) {
            raiseErrInvalidSyntax();
        }
        open("return");
        oneOrZero(this::compileExpr);
        compileSymbol(';');
        close("return");

    }

    public void compileExpr() {
        open("term");
        compileTerm();
        varRepeat(() -> {
            compileOp();
            compileTerm();
        });
        close("term");
    }

    public void compileTerm() {
        Runnable intConst = () -> {
            writer.writeTerminal(INT_CONSTANT, jtz.intVal());
            jtz.advance();
        };
        Runnable strConst = () -> {
            writer.writeTerminal(STRING_CONSTANT, jtz.stringVal());
            jtz.advance();
        };
        Runnable keyConst = () -> compileKeyword(TRUE, FALSE, NULL, THIS);
        Runnable refExpr = () -> {
            referenceIdentifier(VAR_NAME);
            compileSymbol('[');
            compileExpr();
            compileSymbol(']');
        };
        Runnable expr = () -> {
            compileSymbol('{');
            compileExpr();
            compileSymbol('}');
        };
        Runnable subCall = this::compileSubroutineCall;
        Runnable unaryOp = () -> {
            compileUnaryOp();
            compileTerm();
        };
        or(intConst, strConst, keyConst, refExpr, expr, subCall, unaryOp);
    }

    private void compileUnaryOp() {
        compileSymbol('-', '~');
    }

    private void compileOp() {
        compileSymbol('+', '-', '*', '/', '/', '&', '|', '<', '>', '=');
    }

    public void compileExprList() {

    }

    private void compileType() {
        or(() -> compileKeyword(INT, CHAR, BOOLEAN),
            () -> referenceIdentifier(CLASS_NAME));
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
        raiseErrInvalidSyntax();
    }

    private void declareIdentifier(IdentifierType type) {
        // TODO check if violate the identifier rules
        String id = jtz.identifier();
        table.addIdentifier(id, type);
        writer.writeTerminal(IDENTIFIER, jtz.identifier());
        jtz.advance();
    }

    private void referenceIdentifier(IdentifierType type) {
        String id = jtz.identifier();
        if (!table.contains(id, type)) {
            raiseErrInvalidSyntax();
        }
        writer.writeTerminal(IDENTIFIER, jtz.identifier());
        jtz.advance();
    }

    private void raiseErrInvalidSyntax() {
        throw new IllegalArgumentException(
            ERR_INVALID_SYNTAX + " at line " + lineNo + " value :" + jtz.stringVal());
    }

    private void open(String id) {
        writer.openNonTerminal(id);
        table.openSection();
    }

    private void close(String id) {
        writer.closeNonTerminal(id);
        table.closeSection();
    }

    private void and(Runnable... rs) {
        for (Runnable r : rs) {
            r.run();
        }
    }

    private void oneOrZero(Runnable... rs) {
        try {
            or(rs);
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
}


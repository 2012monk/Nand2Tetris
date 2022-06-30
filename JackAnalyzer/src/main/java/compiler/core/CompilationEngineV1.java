package compiler.core;

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
import static compiler.constants.TokenType.SYMBOL;

import compiler.JackWriter;
import compiler.ast.ASTNode;
import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import compiler.constants.Keyword;
import compiler.constants.LexicalType;
import compiler.constants.TokenType;
import java.util.regex.Pattern;

public class CompilationEngineV1 {

    private static final Pattern IDENTIFIER_PATTERN =
        Pattern.compile("^(?!\\d)[\\w_\\d]+");
    private static final String ERR_INVALID_SYNTAX = "invalid syntax";
    private static final String ERR_INVALID_IDENTIFIER = "invalid identifier ";
    private final JackTokenizer jtz;
    private final JackWriter writer;
    private final IdentifierTable table;

    public CompilationEngineV1(JackTokenizer jtz, JackWriter writer) {
        this.writer = writer;
        this.jtz = jtz;
        table = new IdentifierTable();
    }

    public void compile() {
        compileClass();
        writer.close();
    }

    public void compileClass() {
        addNode("class");
        compileKeyword(CLASS);
        declareIdentifier(IdentifierType.CLASS_NAME);
        compileSymbol('{');
        optional(() -> zeroOrMore(this::compileClassVarDec));
        optional(() -> zeroOrMore(this::compileSubroutine));
        compileSymbol('}');
        closeNode("class");
    }

    public void compileClassVarDec() {
        if (jtz.keyword() != STATIC && jtz.keyword() != FIELD) {
            raiseErrInvalidSyntax();
        }
        addNode("classVarDec");
        ASTNode node = new ASTNode(LexicalType.CLASS_VARIABLE_DECLARATION);
        compileVariables(STATIC, FIELD);
        closeNode("classVarDec");
    }

    private void compileVariables(Keyword... keywords) {
        IdentifierType type = IdentifierType.type(compileKeyword(keywords));
        DataType dataType = DataType.get(compileType());
        compileVariable(type, dataType);
        zeroOrMore(() -> {
            compileSymbol(',');
            compileVariable(type, dataType);
        });
        compileSymbol(';');
    }

    private void compileVariable(IdentifierType type, DataType dataType) {
        String id = jtz.identifier();
        jtz.advance();
        VariableIdentifier var = table.declareVariable(id, type, dataType);
        writer.writeVariable(var);
    }

    public void compileSubroutine() {
        if (jtz.keyword() != CONSTRUCTOR && jtz.keyword() != METHOD && jtz.keyword() != FUNCTION) {
            raiseErrInvalidSyntax();
        }
        addNode("subroutineDec");
        declareSubroutine();
        compileSymbol('(');
        compileParameterList();
        compileSymbol(')');
        compileSubroutineBody();
        closeNode("subroutineDec");
    }

    private void declareSubroutine() {
        Keyword key = compileKeyword(CONSTRUCTOR, FUNCTION, METHOD);
        Keyword t = compileType(INT, BOOLEAN, CHAR, VOID);
        Identifier id = declareIdentifier(IdentifierType.type(key));
        int argc = table.varCount(IdentifierType.VAR_NAME);
        System.out.printf("subroutine %s %s %s locals=%d",
            key.getName(), id.getName(), t.getName(), argc);
    }

    public void compileSubroutineBody() {
        addNode("subroutineBody");
        compileSymbol('{');
        optional(() -> zeroOrMore(this::compileVarDec));
        compileStatements();
        compileSymbol('}');
        closeNode("subroutineBody");
    }

    public void compileParameterList() {
        addNode("parameterList");
        optional(() -> {
            compileParameter();
            zeroOrMore(() -> {
                compileSymbol(',');
                compileParameter();
            });
        });
        closeNode("parameterList");
    }

    private void compileParameter() {
        DataType t = DataType.get(compileType());
        compileVariable(IdentifierType.ARGUMENT_NAME, t);
    }

    public void compileVarDec() {
        if (jtz.keyword() != VAR) {
            raiseErrInvalidSyntax();
        }
        addNode("varDec");
        compileVariables(VAR);
        closeNode("varDec");
    }

    public void compileStatements() {
        addNode("statements");
        zeroOrMore(() -> or(this::compileLet, this::compileWhile, this::compileIf,
            this::compileDo, this::compileReturn));
        closeNode("statements");
    }

    public void compileLet() {
        if (jtz.keyword() != LET) {
            raiseErrInvalidSyntax();
        }
        addNode("letStatement");
        compileKeyword(LET);
        referenceIdentifier();
        optional(this::compileArrayIndex);
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
        compileCondition();
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');
        optional(() -> {
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
        compileCondition();
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
        Identifier id = referenceIdentifier();
        compileFunctionCall();
        compileArguments();
    }

    private void compileFunctionCall() {
        optional(() -> {
            compileSymbol('.');
            referenceIdentifier();
        });
    }

    public void compileReturn() {
        if (jtz.keyword() != RETURN) {
            raiseErrInvalidSyntax();
        }
        addNode("returnStatement");
        compileKeyword(RETURN);
        optional(this::compileExpr);
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
        Runnable unaryOp = () -> {
            compileUnaryOp();
            compileTerm();
        };
        Runnable subCall = () -> {
            optional(this::compileFunctionCall);
            compileArguments();
        };
        Runnable refCall = () -> {
            referenceIdentifier();
            optional(() -> or(subCall, this::compileArrayIndex));
        };
        or(this::compileKeywordConstant, this::compileIntConst, this::compileStringConst, refCall,
            this::compileCondition, unaryOp);
        closeNode("term");
    }

    private void compileArguments() {
        compileSymbol('(');
        compileExprList();
        compileSymbol(')');
    }

    private void compileCondition() {
        compileSymbol('(');
        compileExpr();
        compileSymbol(')');
    }

    private void compileArrayIndex() {
        compileSymbol('[');
        compileExpr();
        compileSymbol(']');
    }

    private void compileKeywordConstant() {
        compileKeyword(TRUE, FALSE, NULL, THIS);
    }

    public void compileExprList() {
        addNode("expressionList");
        Runnable multiVars = () -> {
            compileSymbol(',');
            compileExpr();
        };
        optional(this::compileExpr);
        zeroOrMore(multiVars);
        closeNode("expressionList");
    }

    private void compileUnaryOp() {
        compileSymbol('-', '~');
    }

    private void compileOp() {
        compileSymbol('+', '-', '*', '/', '/', '&', '|', '<', '>', '=');
    }

    private Keyword compileType() {
        return compileType(INT, BOOLEAN, CHAR);
    }

    private Keyword compileType(Keyword... keyword) {
        try {
            return (compileKeyword(keyword));
        } catch (Exception e) {
            referenceIdentifier();
            return CLASS;
        }
    }

    private void compileIntConst() {
        writer.writeTerminal(INT_CONSTANT, jtz.intVal());
        jtz.advance();
    }

    private void compileStringConst() {
        writer.writeTerminal(STRING_CONSTANT, jtz.stringVal());
        jtz.advance();
    }

    private Keyword compileKeyword(Keyword... keywords) {
        Keyword cur = jtz.keyword();
        for (Keyword k : keywords) {
            if (cur == k) {
                writer.writeTerminal(KEY_WORD, cur.keyword());
                jtz.advance();
                return k;
            }
        }
        raiseErrInvalidSyntax();
        return cur;
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

    private Identifier declareIdentifier(IdentifierType type) {
        String id = jtz.identifier();
        if (!IDENTIFIER_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException(ERR_INVALID_IDENTIFIER);
        }
        Identifier identifier = table.declareIdentifier(id, type);
        writer.writeTerminal(identifier);
        jtz.advance();
        return identifier;
    }

    private Identifier referenceIdentifier() {
        String id = jtz.identifier();
        Identifier ref = table.reference(id);
        writer.writeTerminal(ref);
        jtz.advance();
        return ref;
    }

    private void raiseErrInvalidSyntax(String... expected) {
        String e = String.join(" or ", expected);
        throw new IllegalArgumentException(String.format("%s\nsymbol:%s\ntype:%s\nexpected: %s\n",
            ERR_INVALID_SYNTAX, jtz.tokenType(), jtz.raw(), e));
    }

    private void addNode(String id) {
        writer.openNonTerminal(id);
    }

    private void closeNode(String id) {
        writer.closeNonTerminal(id);
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

    private void zeroOrMore(Runnable r) {
        while (true) {
            try {
                r.run();
            } catch (Exception ignored) {
                return;
            }
        }
    }

    private void optional(Runnable rs) {
        try {
            rs.run();
        } catch (Exception ignore) {
        }
    }
}


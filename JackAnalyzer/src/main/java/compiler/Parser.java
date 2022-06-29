package compiler;

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
import static compiler.constants.SymbolToken.COMMA;
import static compiler.constants.SymbolToken.DOT;
import static compiler.constants.SymbolToken.EQ;
import static compiler.constants.SymbolToken.L_BRACE;
import static compiler.constants.SymbolToken.L_CURLY;
import static compiler.constants.SymbolToken.L_SQUARE;
import static compiler.constants.SymbolToken.MINUS;
import static compiler.constants.SymbolToken.R_BRACE;
import static compiler.constants.SymbolToken.R_CURLY;
import static compiler.constants.SymbolToken.R_SQUARE;
import static compiler.constants.SymbolToken.SEMI_COLON;
import static compiler.constants.SymbolToken.TILDE;

import compiler.ast.ASTNode;
import compiler.ast.Token;
import compiler.constants.Keyword;
import compiler.constants.LexicalType;
import compiler.constants.SymbolToken;
import compiler.exceptions.InvalidSyntaxException;
import compiler.exceptions.ParseFailedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Parser {


    private static final Pattern IDENTIFIER_PATTERN =
        Pattern.compile("^(?!\\d)[\\w_\\d]+");
    private static final String ERR_INVALID_SYNTAX = "invalid syntax";
    private static final String ERR_INVALID_IDENTIFIER = "invalid identifier ";
    private final JackTokenizer jtz;

    public Parser(JackTokenizer jtz) {
        this.jtz = jtz;
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

    public ASTNode parse() {
        return parseClass();
//        writer.close();
    }

    public ASTNode parseClass() {
        return new ASTNode(LexicalType.CLASS_DECLARATION)
            .add(parseKeyword(CLASS))
            .add(declare(LexicalType.CLASS_NAME))
            .add(symbolNode(L_CURLY))
            .add(zeroOrMoreNode(this::parseClassVarDec))
            .add(zeroOrMoreNode(this::parseSubroutine))
            .add(symbolNode(R_CURLY));

    }

    private ASTNode parseSubroutine() {
        return new ASTNode(LexicalType.SUBROUTINE_DECLARATION)
            .add(new ASTNode(LexicalType.SUBROUTINE_PREFIX)
                .add(parseKeywords(CONSTRUCTOR, FUNCTION, METHOD))
            )
            .add(retType())
            .add(declare(LexicalType.SUBROUTINE_NAME))
            .add(parameterList())
            .add(subroutineBody());
    }

    private ASTNode parameterList() {
        return new ASTNode(LexicalType.PARAMETER_LIST)
            .add(symbolNode(L_BRACE))
            .add(optionalNode(this::parameter))
            .add(zeroOrMoreNodes(() ->
                List.of(symbolNode(COMMA), parameter())))
            .add(symbolNode(R_BRACE));
    }

    private ASTNode parameter() {
        return new ASTNode(LexicalType.PARAMETER)
            .add(parseDataType())
            .add(declare(LexicalType.ARGUMENT_NAME));
    }

    private ASTNode subroutineBody() {
        return new ASTNode(LexicalType.SUBROUTINE_BODY)
            .add(symbolNode(L_CURLY))
            .add(zeroOrMoreNode(() -> prefixedVarDec(VAR)))
            .add(statements())
            .add(symbolNode(R_CURLY));
    }

    private ASTNode declare(LexicalType type) {
//        if (jtz.tokenType() != TokenType.IDENTIFIER) {
//            throw new InvalidSyntaxException();
//        }
        jtz.identifier();
        ASTNode node = new ASTNode(type)
            .add(new ASTNode(LexicalType.IDENTIFIER, jtz.getToken()));
        jtz.advance();
        return node;
    }

    public ASTNode parseClassVarDec() {
        return new ASTNode(LexicalType.CLASS_VARIABLE_DECLARATION)
            .add(prefixedVarDec(STATIC, FIELD));
    }

    private ASTNode prefixedVarDec(Keyword... prefixes) {
        return new ASTNode(LexicalType.VARIABLE_DECLARATION)
            .add(new ASTNode(LexicalType.MODIFIER)
                .add(parseKeywords(prefixes)))
            .addAll(parseVariableDec());
    }

    private List<ASTNode> parseVariableDec() {
        List<ASTNode> r = new ArrayList<>();
        r.add(parseDataType());
        r.add(declare(LexicalType.VARIABLE_NAME));
        r.addAll(zeroOrMoreNodes(() ->
            List.of(symbolNode(COMMA),
                declare(LexicalType.VARIABLE_NAME))));
        r.add(symbolNode(SEMI_COLON));
        return r;
    }

    private ASTNode symbolNode(SymbolToken... symbols) {
        char c = jtz.symbol();
        for (SymbolToken s : symbols) {
            if (s.is(c)) {
                ASTNode node = new ASTNode(LexicalType.symbol(s), jtz.getToken());
//                ASTNode node = new ASTNode(s)
//                    .add(new ASTNode(jtz.getToken()));
                jtz.advance();
                return node;
            }
        }
        throw new ParseFailedException(Arrays.toString(symbols));
    }

    private ASTNode parseDataType() {
        return new ASTNode(LexicalType.DATA_TYPE)
            .add(orNode(List.of(
                () -> parseKeywords(INT, BOOLEAN, CHAR),
                this::ref
            )));
    }

    private ASTNode retType() {
        return new ASTNode(LexicalType.RETURN_TYPE)
            .add(orNode(List.of(
                () -> parseKeywords(INT, BOOLEAN, CHAR, VOID),
                this::ref
            )));
    }

    private ASTNode ref() {
//        if (jtz.tokenType() != TokenType.IDENTIFIER) {
//            throw new InvalidSyntaxException();
//        }
        jtz.identifier();
        ASTNode node = new ASTNode(LexicalType.IDENTIFIER, jtz.getToken());
//        ASTNode node = new ASTNode(LexicalType.REF_TYPE, jtz.getToken());
        jtz.advance();
        return node;
    }

    private ASTNode parseKeywords(Keyword... keywords) {
        for (Keyword k : keywords) {
            try {
                return parseKeyword(k);
            } catch (Exception ignored) {
            }
        }
        throw new ParseFailedException();
    }

    private ASTNode parseKeyword(Keyword keyword) {
        if (keyword != jtz.keyword()) {
            throw new InvalidSyntaxException();
        }
        ASTNode node = new ASTNode(LexicalType.keyword(keyword), jtz.getToken());
//            ASTNode node = new ASTNode(jtz.getToken());
        jtz.advance();
        return node;
    }

    private ASTNode statements() {
        return new ASTNode(LexicalType.STATEMENTS)
            .add(zeroOrMoreNode(this::allStatement));
    }

    private ASTNode allStatement() {
        return orNode(
            List.of(this::parseLet, this::parseDo,
                this::parseWhile, this::parseReturn, this::parseIf)
        );
    }

    private ASTNode parseIf() {
        return new ASTNode(LexicalType.IF_STATEMENT)
            .add(parseKeywords(IF))
            .add(conditionBlock())
            .add(ifBody())
            .add(optionalNodes(() -> List.of(
                parseKeywords(ELSE),
                elseBody()
            )));
    }

    private ASTNode ifBody() {
        return new ASTNode(LexicalType.IF_BODY)
            .add(blockStatements());
    }

    private ASTNode elseBody() {
        return new ASTNode(LexicalType.ELSE_BODY)
            .add(blockStatements());
    }

    private ASTNode blockStatements() {
        return new ASTNode(LexicalType.BLOCK_STATEMENTS)
            .add(symbolNode(L_CURLY))
            .add(statements())
            .add(symbolNode(R_CURLY));
    }

    private ASTNode parseReturn() {
        return new ASTNode(LexicalType.RETURN_STATEMENT)
            .add(parseKeywords(RETURN))
            .add(optionalNode(this::expr))
            .add(symbolNode(SEMI_COLON));
    }

    private ASTNode parseWhile() {
        return new ASTNode(LexicalType.WHILE_STATEMENT)
            .add(parseKeywords(WHILE))
            .add(conditionBlock())
            .add(blockStatements());
    }

    private ASTNode parseDo() {
        return new ASTNode(LexicalType.DO_STATEMENT)
            .add(parseKeywords(DO))
            .add(subCall())
            .add(symbolNode(SEMI_COLON));
    }

    private ASTNode subCall() {
        ASTNode ref = ref();
        return orNode(List.of(
            () -> subCall(ref),
            () -> selfCall(ref)
        ));
//        return new ASTNode(LexicalType.SUBROUTINE_CALL)
//            .add(orNode(List.of(
//                () -> subCall(ref),
//                () -> selfCall(ref)
//            ))
//            )
//            .add(args());
    }
//
//    private ASTNode methodCall() {
//        return new ASTNode(LexicalType.REF_TYPE)
//            .add(new ASTNode(LexicalType.REF_TYPE)
//                .add(ref()))
//            .add(symbolNode(DOT))
//            .add(new ASTNode(LexicalType.SUBROUTINE_NAME)
//                .add(ref())
//            );
//    }
//
//    private ASTNode selfCall() {
//        return new ASTNode(LexicalType.REF_TYPE)
//            .add(new ASTNode(LexicalType.SUBROUTINE_NAME))
//            .add(ref());
//    }

    private ASTNode args() {
        return new ASTNode(LexicalType.ARGUMENT_LIST)
            .add(symbolNode(L_BRACE))
            .add(exprList())
            .add(symbolNode(R_BRACE));
    }

    private ASTNode parseLet() {
        return new ASTNode(LexicalType.LET_STATEMENT)
            .add(parseKeywords(LET))
            .add(varExpr())
//            .add(new ASTNode(LexicalType.REF_TYPE)
//                    .add(ref())
//                    .add(optionalNode(this::arrayAccessSuffix)))
            .add(symbolNode(EQ))
            .add(expr())
            .add(symbolNode(SEMI_COLON));
    }

    private ASTNode expr() {
        return new ASTNode(LexicalType.EXPRESSION)
            .add(term())
            .add(zeroOrMoreNode(this::binaryExpr));
    }

    private ASTNode ternaryExpr() {
        return new ASTNode(LexicalType.TERNARY_EXPRESSION)
            .add(term())
            .add(zeroOrMoreNode(this::binaryExpr));
    }

    private ASTNode binaryExpr() {
        return new ASTNode(LexicalType.BINARY_EXPRESSION)
            .add(op())
            .add(term());
    }

    private ASTNode unaryExpr() {
        return new ASTNode(LexicalType.UNARY_EXPRESSION)
            .add(unaryOperator())
            .add(term());
    }

    private ASTNode op() {
        return new ASTNode(LexicalType.OPERATOR)
            .add(symbolNode(SymbolToken.PLUS, SymbolToken.MINUS, SymbolToken.ASTERISK,
                SymbolToken.SLASH, SymbolToken.AMP, SymbolToken.PIPE,
                SymbolToken.LT, SymbolToken.GT, SymbolToken.EQ));
    }

    private ASTNode term() {
        return orNode(List.of(this::constTerm, this::refTerm));
    }

    private ASTNode constTerm() {
        return new ASTNode(LexicalType.TERM)
            .add(orNode(List.of(
                this::intConst,
                this::strConst,
                this::keywordConst,
                this::conditionBlock,
                this::unaryExpr)));
    }

    private ASTNode refTerm() {
        ASTNode ref = ref();
        return new ASTNode(LexicalType.TERM)
            .add(orNode(List.of(
                () -> subCall(ref),
                () -> selfCall(ref),
                () -> arrayAccessExpr(ref),
                () -> new ASTNode(LexicalType.REF_TYPE).add(ref)
            )));
    }

    private ASTNode varExpr() {
        ASTNode ref = ref();
        return orNode(List.of(
            () -> arrayAccessExpr(ref),
            () -> new ASTNode(LexicalType.REF_TYPE)
                .add(ref))
        );
    }

    private ASTNode arrayAccessExpr(ASTNode ref) {
        return new ASTNode(LexicalType.ARRAY_ACCESS_EXPRESSION)
            .add(ref)
            .add(arrayAccessSuffix());
    }

    private ASTNode selfCall(ASTNode subroutine) {
        return new ASTNode(LexicalType.SUBROUTINE_CALL)
            .add(new ASTNode(LexicalType.SUBROUTINE_NAME)
                .add(subroutine)
            )
            .add(args());
    }

    private ASTNode subCall(ASTNode className) {
        return new ASTNode(LexicalType.SUBROUTINE_CALL)
            .add(new ASTNode(LexicalType.REF_TYPE)
                .add(className))
            .add(symbolNode(DOT))
            .add(new ASTNode(LexicalType.SUBROUTINE_NAME)
                .add(ref())
            )
            .add(args());
    }

    private ASTNode funcCall() {
        return new ASTNode(LexicalType.SUBROUTINE_CALL)
            .add(optionalNodes(() -> List.of(symbolNode(DOT),
                new ASTNode(LexicalType.SUBROUTINE_NAME)
                    .add(ref()))))
            .add(args());
    }

    private ASTNode exprList() {
        return new ASTNode(LexicalType.EXPRESSION_LIST)
            .add(optionalNode(this::expr))
            .add(zeroOrMoreNodes(() ->
                    List.of(
                        symbolNode(COMMA),
                        expr()
                    )
                )
            );
    }

    private ASTNode conditionBlock() {
        return new ASTNode(LexicalType.CONDITION_EXPRESSION)
            .add(symbolNode(L_BRACE))
            .add(expr())
            .add(symbolNode(R_BRACE));
    }

    private ASTNode keywordConst() {
        return new ASTNode(LexicalType.KEYWORD_CONSTANT)
            .add(parseKeywords(TRUE, FALSE, NULL, THIS));
    }

    private ASTNode strConst() {
        Token t = jtz.getToken();
        jtz.stringVal();
        jtz.advance();
        return new ASTNode(LexicalType.STRING_CONSTANT, t);
//        return new ASTNode(LexicalType.STRING_CONSTANT)
//            .add(new ASTNode(t));
    }

    private ASTNode intConst() {
        Token t = jtz.getToken();
        jtz.intVal();
        jtz.advance();
        return new ASTNode(LexicalType.INTEGER_CONSTANT, t);
//        return new ASTNode(LexicalType.INTEGER_CONSTANT)
//            .add(new ASTNode(t));
    }

    private ASTNode unaryOperator() {
        return new ASTNode(LexicalType.UNARY_OPERATOR)
            .add(symbolNode(TILDE, MINUS));
    }

    private ASTNode arrayAccessSuffix() {
        return new ASTNode(LexicalType.ARRAY_ACCESS_SUFFIX)
            .add(symbolNode(L_SQUARE))
            .add(expr())
            .add(symbolNode(R_SQUARE));
    }

    private void raiseErrInvalidSyntax(String... expected) {
        String e = String.join(" or ", expected);
        throw new IllegalArgumentException(String.format("%s\nsymbol:%s\ntype:%s\nexpected: %s\n",
            ERR_INVALID_SYNTAX, jtz.tokenType(), jtz.raw(), e));
    }

    private ASTNode orNode(List<Supplier<ASTNode>> suppliers) {
        for (Supplier<ASTNode> s : suppliers) {
            try {
                return s.get();
            } catch (Exception ignored) {
            }
        }
        throw new ParseFailedException();
    }

    private List<ASTNode> optionalNode(Supplier<ASTNode> suppliers) {
        try {
            return List.of(suppliers.get());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<ASTNode> optionalNodes(Supplier<List<ASTNode>> suppliers) {
        try {
            return suppliers.get();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

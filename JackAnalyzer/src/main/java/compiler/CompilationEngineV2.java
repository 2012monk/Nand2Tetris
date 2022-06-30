package compiler;

import compiler.ast.ASTNode;
import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import compiler.constants.LexicalType;
import compiler.constants.MemorySegment;
import compiler.constants.VMCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompilationEngineV2 {

    private static final String IF_TRUE = "IF_TRUE_";
    private static final String IF_FALSE = "IF_FALSE_";
    private static final String IF_END = "IF_END_";
    private static final String WHILE_COND = "WHILE_CONDITION_";
    private static final String WHILE_END = "WHILE_END_";
    private static int i = 0;
    private final Logger LOG = Logger.getLogger("compileEngineLogger");
    private IdentifierTable table;
    private ASTNode parsed;
    private VMWriter vm;
    private Parser parser;
    private int ifIndex = 0;
    private int wIndex = 0;

    public CompilationEngineV2(JackTokenizer jtz, JackFileWriter writer) {
        this.parser = new Parser(jtz);
        vm = new VMWriter(writer);
        table = new IdentifierTable();
    }

    public void compile() {
        parsed = parser.parse();
        try {
            XMLUtil.writeXml("src/test/resources/" + i++ + ".xml", parsed);
        } catch (Exception ignored) {
        }
        compileClass(parsed);
        vm.close();
    }

    private void compileClass(ASTNode node) {
        table.declareClass(node.findFirst(LexicalType.CLASS_NAME)
            .getFirstChild().getToken().getValue());
        List<ASTNode> varDecs = new ArrayList<>();
        node.findChildren(LexicalType.CLASS_VARIABLE_DECLARATION, varDecs);
        for (ASTNode n : varDecs) {
            defineVar(n);
        }
        List<ASTNode> subDecs = new ArrayList<>();
        node.findChildren(LexicalType.SUBROUTINE_DECLARATION, subDecs);
        for (ASTNode n : subDecs) {
            defineSubroutine(n);
        }
        for (ASTNode n : subDecs) {
            compileSubroutine(n);
        }
    }

    private void defineVar(ASTNode node) {
        if (node == null) {
            return;
        }
        LexicalType prefix = node.findFirst(LexicalType.MODIFIER).getFirstChild().getType();
        ASTNode dnode = firstChild(node, LexicalType.DATA_TYPE);
        DataType t = DataType.TYPE_CLASS;
        if (dnode.getType() != LexicalType.IDENTIFIER) {
            t = dnode.getType().dataType();
        }
        for (ASTNode c : node.findChildren(LexicalType.VARIABLE_NAME)) {
            String name = c.getFirstChild()
                .getToken().getValue();
            if (t == DataType.TYPE_CLASS) {
                table.declareVariable(name, prefix.id(), t, dnode.getValue());
                continue;
            }
            table.declareVariable(name, prefix.id(), t);
        }
    }

    private void defineParameter(ASTNode node) {
        ASTNode dnode = firstChild(node, LexicalType.DATA_TYPE);
        DataType t = DataType.TYPE_CLASS;
        if (dnode.getType() != LexicalType.IDENTIFIER) {
            t = dnode.getType().dataType();
        }
        for (ASTNode c : node.findChildren(LexicalType.ARGUMENT_NAME)) {
            String name = c.getFirstChild()
                .getToken().getValue();
            if (t == DataType.TYPE_CLASS) {
                table.declareVariable(name, IdentifierType.ARGUMENT_NAME, t, dnode.getValue());
                continue;
            }
            table.declareVariable(name, IdentifierType.ARGUMENT_NAME, t);
        }
    }

    private void defineVars(ASTNode node) {
        List<ASTNode> nodes = node.findChildren(LexicalType.VARIABLE_DECLARATION);
        for (ASTNode c : nodes) {
            defineVar(c);
        }
        List<ASTNode> params = node.findPreOrder(LexicalType.PARAMETER_LIST)
            .findChildren(LexicalType.PARAMETER);
        for (ASTNode p : params) {
            defineParameter(p);
        }
    }

    private void compileSubroutine(ASTNode node) {
        System.out.println("START SUBROUTINE");
        compileSubroutineDec(node);
        compileStatements(node.findPreOrder(LexicalType.STATEMENTS));
        LexicalType ret = firstChild(node, LexicalType.RETURN_TYPE).getType();
        if (ret != LexicalType.VOID) {
            compileReturn(node.findFirst(LexicalType.RETURN_STATEMENT));
            return;
        }
        vm.writePush(MemorySegment.CONSTANT, 0);
        vm.writeReturn();
    }

    private void compileStatements(ASTNode node) {
        if (node == null) {
            return;
        }
        LOG.info("statements children :" + node.children().size());
        for (ASTNode c : node.children()) {
            LexicalType t = c.getType();
            LOG.info("start statement : " + t);
            if (t == LexicalType.IF_STATEMENT) {
                compileIf(c);
            }
            if (t == LexicalType.DO_STATEMENT) {
                compileCall(c.findFirst(LexicalType.SUBROUTINE_CALL));
                vm.writePop(MemorySegment.TMP, 0);
            }
            if (t == LexicalType.WHILE_STATEMENT) {
                compileWhile(c);
            }
            if (t == LexicalType.LET_STATEMENT) {
                compileLet(c);
            }
        }
    }

    private void compileIf(ASTNode node) {
        ASTNode ifBody = node.findInImmediate(LexicalType.IF_BODY).orElseThrow();
        compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        Optional<ASTNode> el = node.findInImmediate(LexicalType.ELSE_BODY);
        if (el.isPresent()) {
            ifElseBlock(ifBody.findPreOrder(LexicalType.STATEMENTS),
                el.get().findPreOrder(LexicalType.STATEMENTS), ifIndex++);
        } else {
            ifBlock(ifBody.findPreOrder(LexicalType.STATEMENTS), ifIndex++);

        }
    }

    private void ifBlock(ASTNode ifSt, int idx) {
        vm.writeIf(IF_TRUE + idx);
        vm.writeGoto(IF_FALSE + idx);
        vm.writeLabel(IF_TRUE + idx);
        compileStatements(ifSt);
        vm.writeLabel(IF_FALSE + idx);
    }

    private void ifElseBlock(ASTNode ifSt, ASTNode elseSt, int idx) {
        vm.writeIf(IF_TRUE + idx);
        vm.writeGoto(IF_FALSE + idx);
        vm.writeLabel(IF_TRUE + idx);
        compileStatements(ifSt);
        vm.writeGoto(IF_END + idx);
        vm.writeLabel(IF_FALSE + idx);
        compileStatements(elseSt);
        vm.writeLabel(IF_END + idx);
    }

    private void compileExprs(ASTNode node) {
        LOG.log(Level.INFO, "compile args");
        for (ASTNode c : node.children()) {
            if (c.getType() == LexicalType.EXPRESSION) {
                LOG.log(Level.INFO, "args - expression " + c.getFirstChild().getType());
                compileExpr(c);
            }
        }
    }

    private void compileWhile(ASTNode node) {
        int idx = wIndex++;
        vm.writeLabel(WHILE_COND + idx);
        compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        vm.writeArithmetic(VMCommand.NOT);
        vm.writeIf(WHILE_END + idx);
        node.findPreOrder(LexicalType.BLOCK_STATEMENTS)
            .findInImmediate(LexicalType.STATEMENTS)
            .ifPresent(this::compileStatements);
        vm.writeGoto(WHILE_COND + idx);
        vm.writeLabel(WHILE_END + idx);
    }

    private void compileLet(ASTNode node) {
        LOG.log(Level.INFO, "let statement expr");
        compileExpr(node.findInImmediate(LexicalType.EXPRESSION).orElseThrow());
//        ASTNode v = node.children().get(1);
        node.findInImmediate(LexicalType.ARRAY_ACCESS_EXPRESSION)
            .ifPresent(n -> {
                compileArrayAccess(n);
                vm.writePop(MemorySegment.THAT, 0);
            });
        node.findInImmediate(LexicalType.REF_TYPE)
            .ifPresent(n -> vm.writePop((VariableIdentifier)
                table.reference(n.getFirstChild().getValue())));
    }

    private void compileArrayAccess(ASTNode node) {
        if (node == null) {
            return;
        }
        LOG.info("");
        VariableIdentifier ref = (VariableIdentifier) table.reference(
            node.getFirstChild().getValue());
        compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        vm.writePush(ref);
        vm.writeArithmetic(VMCommand.ADD);
        vm.writePop(MemorySegment.PTR, 1);
    }

    private void compileReturn(ASTNode node) {
        compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        vm.writeReturn();
    }

    private void compileExpr(ASTNode node) {
        for (ASTNode c : node.children()) {
            LexicalType t = c.getType();
            if (t == LexicalType.BINARY_EXPRESSION) {
                compileBinaryExpr(c);
            }
            if (t == LexicalType.TERM) {
                compileTerm(c);
            }
        }
    }

    private void compileTerm(ASTNode node) {
        node = node.getFirstChild();
        LexicalType type = node.getType();
        LOG.log(Level.INFO, "compile term " + type.getName());
        if (type == LexicalType.REF_TYPE) {
            pushNode(node.getFirstChild());
        }
        if (type == LexicalType.INTEGER_CONSTANT) {
            vm.writePush(MemorySegment.CONSTANT, Integer.parseInt(node.getValue()));
        }
        if (type == LexicalType.STRING_CONSTANT) {
            vm.compileStringConst(node.getValue());
        }
        if (type == LexicalType.ARRAY_ACCESS_EXPRESSION) {
            compileArrayAccess(node);
            vm.writePush(MemorySegment.THAT, 0);
        }
        if (type == LexicalType.SUBROUTINE_CALL) {
            compileCall(node);
        }
        if (type == LexicalType.CONDITION_EXPRESSION) {
            compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        }
        if (type == LexicalType.KEYWORD_CONSTANT) {
            vm.compileKeywordConst(node.getFirstChild().getType());
        }
        if (type == LexicalType.UNARY_EXPRESSION) {
            compileUnary(node);
        }
    }

    private void compileUnary(ASTNode node) {
        compileTerm(node.findPreOrder(LexicalType.TERM));
        vm.compileUnaryOp(firstChild(node, LexicalType.UNARY_OPERATOR).getType());
    }

    private void compileCall(ASTNode node) {
        String func = firstChild(node, LexicalType.SUBROUTINE_NAME).getValue();
        String ref = table.getCurrentClass();
        int argc = 0;
        try {
            ref = node.findInImmediate(LexicalType.REF_TYPE)
                .orElseThrow()
                .getFirstChild()
                .getValue();
        } catch (Exception ignored) {
        }
        Identifier id = table.reference(ref);

        LOG.info(func + " " + table.reference(func).getType() + " " + ref + "  " + id.getType());
        if (id.isVariable()) { // method call
            argc = 1;
            vm.writePush((VariableIdentifier) id);
            ref = ((VariableIdentifier) id).getRef();
        } else if (table.reference(func).getType() == IdentifierType.METHOD_NAME) {
            argc = 1;
            vm.writePush(MemorySegment.PTR, 0);
        }
        ASTNode args = node.findFirst(LexicalType.EXPRESSION_LIST);

        compileExprs(args);
        argc += args.children()
            .stream()
            .filter(c -> c.getType() == LexicalType.EXPRESSION)
            .count();
        vm.compileCall(ref, func, argc);
    }

    private void pushNode(ASTNode node) {
        Identifier id = table.reference(node.getValue());
        vm.writePush((VariableIdentifier) id);
    }

    private void compileBinaryExpr(ASTNode node) {
        LOG.info("");
        compileTerm(node.getLastChild());
        vm.compileOp(firstChild(node, LexicalType.OPERATOR).getType());
    }

    private void defineSubroutine(ASTNode node) {
        LexicalType type = node.findInImmediate(LexicalType.SUBROUTINE_PREFIX)
            .orElseThrow()
            .getFirstChild()
            .getType();
        String name = node.findInImmediate(LexicalType.SUBROUTINE_NAME)
            .orElseThrow()
            .getFirstChild()
            .getValue();
        table.declareIdentifier(name, IdentifierType.subroutineType(type));
    }

    private void compileSubroutineDec(ASTNode node) {
        LexicalType type = firstChild(node, LexicalType.SUBROUTINE_PREFIX).getType();
        String name = firstChild(node, LexicalType.SUBROUTINE_NAME).getValue();
        String className = table.getCurrentClass();
        table.startSubroutine(name);
        defineVars(node);
        int memberCount, localArgs = table.varCount(IdentifierType.VAR_NAME);
        LOG.info(String.format("%s %s %s %d",
            className, name, type.getName(), localArgs));

        if (type == LexicalType.METHOD) {
            vm.compileMethodDec(className, name, localArgs);
        }

        if (type == LexicalType.FUNCTION) {
            vm.compileFunctionDec(className, name, localArgs);
        }

        if (type == LexicalType.CONSTRUCTOR) {
            memberCount = table.varCount(IdentifierType.STATIC_NAME);
            memberCount += table.varCount(IdentifierType.FILED_NAME);
            vm.compileConstructorDec(className, name, localArgs, memberCount);
        }
    }

    public ASTNode firstChild(ASTNode node, LexicalType type) {
        return node.findFirst(type).getFirstChild();
    }
}

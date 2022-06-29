package compiler;

import compiler.ast.ASTNode;
import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import compiler.constants.LexicalType;
import compiler.constants.MemorySegment;
import compiler.constants.VMCommand;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompilationEngineV2 {

    private static final String IF_TRUE = "IF_TRUE_";
    private static final String IF_FALSE = "IF_FALSE_";
    private static final String IF_END = "IF_END_";
    private static final String WHILE_COND = "WHILE_CONDITION_";
    private static final String WHILE_END = "WHILE_END_";
    private Logger log = Logger.getLogger("compileEngineLogger");
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
            XMLUtil.writeXml("src/test/resources/tml.xml", parsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            compileClass(parsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        System.out.println(nodes.size());
        for (ASTNode c : nodes) {
            defineVar(c);
        }
        List<ASTNode> params = node.findPreOrder(LexicalType.PARAMETER_LIST).findChildren(LexicalType.PARAMETER);
        for (ASTNode p : params) {
            defineParameter(p);
        }
    }

    private void compileSubroutine(ASTNode node) {
        System.out.println("START SUBROUTINE");
        table.startSubroutine();
        compileSubroutineDec(node);
        compileStatements(node.findPreOrder(LexicalType.STATEMENTS));
        LexicalType ret = firstChild(node, LexicalType.RETURN_TYPE).getType();
        System.out.println(ret);
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
        log.log(Level.INFO, "start " + node.children().size());
        for (ASTNode c : node.children()) {
            LexicalType t = c.getType();
//            log.log(Level.INFO, "next : " + t.getName());
            System.out.println(t);
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
        compileExpr(node.findFirst(LexicalType.CONDITION_EXPRESSION));

        vm.writeArithmetic(VMCommand.NOT);
        vm.writeIf(IF_TRUE + ifIndex);
        vm.writeGoto(IF_FALSE + ifIndex);

        compileStatements(node.findFirst(LexicalType.BLOCK_STATEMENTS));

        vm.writeLabel(IF_TRUE + ifIndex);
        vm.writeGoto(IF_END + ifIndex);
        vm.writeLabel(IF_FALSE + ifIndex);

        compileStatements(node.findFirst(LexicalType.ELSE_BODY));

        vm.writeLabel(IF_END + ifIndex++);
    }

    private void compileExprs(ASTNode node) {
        log.log(Level.INFO, "compile args");
        for (ASTNode c : node.children()) {
            if (c.getType() == LexicalType.EXPRESSION) {
                log.log(Level.INFO, "args - expression " + c.getFirstChild().getType());
                compileExpr(c);
            }
        }
    }

    private void compileWhile(ASTNode node) {
        vm.writeLabel(WHILE_COND + wIndex);
        compileExpr(node.findFirst(LexicalType.EXPRESSION));
        vm.writeArithmetic(VMCommand.NOT);
        vm.writeIf(WHILE_END + wIndex);
        compileStatements(node.findPreOrder(LexicalType.BLOCK_STATEMENTS)
            .findPreOrder(LexicalType.STATEMENTS));
        vm.writeGoto(WHILE_COND + wIndex);
        vm.writeLabel(WHILE_END + wIndex++);
    }

    private void compileLet(ASTNode node) {
        for (ASTNode c : node.children()) {
            if (c.getType() == LexicalType.EXPRESSION) {
                log.log(Level.INFO, "let statement expr");
                compileExpr(c);
            }
        }
        ASTNode v = node.children().get(1);
        if (v.getType() != LexicalType.ARRAY_ACCESS_EXPRESSION) {
            vm.writePop((VariableIdentifier) table.reference(
                v.getFirstChild().getValue()));
            return;
        }
        compileArrayAccess(v);
        vm.writePop(MemorySegment.THAT, 0);
    }

    private void compileArrayAccess(ASTNode node) {
        if (node == null) {
            return;
        }
        System.out.println(node.getType());
        System.out.println(node.getFirstChild().getType());
        System.out.println(node.getFirstChild().getToken());
        VariableIdentifier ref = (VariableIdentifier) table.reference(
            node.getFirstChild().getValue());
        compileExpr(node.findPreOrder(LexicalType.EXPRESSION));
        vm.writePush(ref);
        vm.writeArithmetic(VMCommand.ADD);
        vm.writePop(MemorySegment.PTR, 1);
    }

    private void compileReturn(ASTNode node) {
        compileExpr(node.getLastChild());
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
        log.log(Level.INFO, "compile term " + type.getName());
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
        log.log(Level.INFO, node.getType().getName());
        ASTNode refNode = firstChild(node, LexicalType.REF_TYPE);
        String func = firstChild(node, LexicalType.SUBROUTINE_NAME).getValue();
        String ref = table.getCurrentClass();
        if (refNode != null) {
            ref = refNode.getValue();
        }
        log.log(Level.INFO, func + " " + ref);
        if (table.reference(func).getType() == IdentifierType.METHOD_NAME) {
            vm.writePush(MemorySegment.PTR, 0);
        }
        Identifier id = table.reference(ref);
        if (id.isVariable()) { // method call
            vm.writePush((VariableIdentifier) id);
            ref = ((VariableIdentifier) id).getRef();
        }
        for (ASTNode child : node.children()) {
            log.log(Level.INFO, child.getType().getName());
        }
        ASTNode args = node.findFirst(LexicalType.EXPRESSION_LIST);

        compileExprs(args);
        int argc = findChildren(args, LexicalType.COMMA).size() + 1;
        vm.compileCall(ref, func, argc);
    }

    private void pushNode(ASTNode node) {
        Identifier id = table.reference(node.getValue());
        System.out.println(id.getType() + id.getName());
        System.out.println(node.getValue());
        vm.writePush((VariableIdentifier) id);
    }

    private void compileBinaryExpr(ASTNode node) {
        log.log(Level.INFO, "binaryExpr " + node.getType());
        compileTerm(node.getLastChild());
        vm.compileOp(firstChild(node, LexicalType.OPERATOR).getType());
        log.log(Level.INFO, "operator compile" + firstChild(node, LexicalType.OPERATOR).getType());
    }

    private void compileSubroutineDec(ASTNode node) {
        defineVars(node);
        int memberCount, localArgs = table.varCount(IdentifierType.VAR_NAME);
        LexicalType type = firstChild(node, LexicalType.SUBROUTINE_PREFIX).getType();
        String name = firstChild(node, LexicalType.SUBROUTINE_NAME).getValue();
        String className = table.getCurrentClass();
        System.out.println(className + "   " + name);
        if (type == LexicalType.METHOD) {
            table.declareIdentifier(name, IdentifierType.METHOD_NAME);
            vm.compileMethodDec(table.getCurrentClass(), name, localArgs);
        }
        if (type == LexicalType.FUNCTION) {
            table.declareIdentifier(name, IdentifierType.FUNCTIONS_NAME);
            vm.compileFunctionDec(className, name, localArgs);
        }
        if (type == LexicalType.CONSTRUCTOR) {
            table.declareIdentifier(name, IdentifierType.CONSTRUCTOR_NAME);
            memberCount = table.varCount(IdentifierType.STATIC_NAME);
            memberCount += table.varCount(IdentifierType.FILED_NAME);
            vm.compileConstructorDec(className, name, localArgs, memberCount);
        }
    }

    public ASTNode firstChild(ASTNode node, LexicalType type) {
        return node.findFirst(type).getFirstChild();
    }

    public List<ASTNode> findChildren(ASTNode node, LexicalType type) {
        List<ASTNode> r = new ArrayList<>();
        node.findChildren(type, r);
        return r;
    }
}

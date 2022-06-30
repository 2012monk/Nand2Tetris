package compiler;

import static compiler.constants.MemorySegment.ARG;
import static compiler.constants.MemorySegment.CONSTANT;
import static compiler.constants.MemorySegment.PTR;

import compiler.componenets.VariableIdentifier;
import compiler.constants.LexicalType;
import compiler.constants.MemorySegment;
import compiler.constants.VMCommand;

public class VMWriter {

    private static final String RETURN = "return";
    private static final String PUSH = "push";
    private static final String POP = "pop";
    private static final String LABEL = "label";
    private static final String IF_GOTO = "if-goto";
    private static final String GOTO = "goto";
    private static final String FUNCTION = "function";
    private static final String CALL = "call";
    private static final String STRING_NEW = "String.new";
    private static final String STR_APPEND = "String.appendChar";
    private static final String MULT = "Math.multiply";
    private static final String DIV = "Math.divide";
    private static final String ALLOC = "Memory.alloc";
    private static final String DE_ALLOC = "Memory.deAlloc";
    private final JackFileWriter writer;

    public VMWriter(JackFileWriter writer) {
        this.writer = writer;
    }

    public void writePush(MemorySegment segment, int index) {
        writer.appendLines(PUSH, segment.getId(), String.valueOf(index));
        writer.newLine();
    }

    public void writePop(MemorySegment segment, int index) {
        writer.appendLines(POP, segment.getId(), String.valueOf(index));
        writer.newLine();
    }

    public void writeArithmetic(VMCommand command) {
        writer.appendLines(command.getId());
        writer.newLine();
    }

    public void writeLabel(String label) {
        writer.appendLines(LABEL, label);
        writer.newLine();
    }

    public void writeIf(String label) {
        writer.appendLines(IF_GOTO, label);
        writer.newLine();
    }

    public void writeGoto(String label) {
        writer.appendLines(GOTO, label);
        writer.newLine();
    }

    public void writeFunction(String label, int argc) {
        writer.appendLines(FUNCTION, label, String.valueOf(argc));
        writer.newLine();
    }

    public void writeCall(String label, int argc) {
        writer.appendLines(CALL, label, String.valueOf(argc));
        writer.newLine();
    }

    public void writeReturn() {
        writer.appendLines(RETURN);
        writer.newLine();
    }

    public void close() {
        writer.close();
    }

    public void compileMethodDec(String className, String name, int localArgc) {
        compileFunctionDec(className, name, localArgc);
        writePush(ARG, 0);
        writePop(PTR, 0);
    }

    public void compileConstructorDec(String className, String name, int localArgc, int size) {
        compileFunctionDec(className, name, localArgc);
        writePush(CONSTANT, size);
        writeCall(ALLOC, 1);
        writePop(PTR, 0);
    }

    public void compileFunctionDec(String className, String name, int localArgc) {
        String label = className + "." + name;
        System.out.println(label + "\n\n\n\n\n");
        writeFunction(label, localArgc);
    }

    public void writePush(VariableIdentifier var) {
        writePush(var.getSegment(), var.getIndex());
    }

    public void writePop(VariableIdentifier var) {
        writePop(var.getSegment(), var.getIndex());

    }

    public void compileCall(String className, String func, int argc) {
        writeCall(className + "." + func, argc);
    }

    public void compileStringConst(String str) {
        int size = str.length();
        writePush(CONSTANT, size);
        writeCall(STRING_NEW, 1);
        str.chars().forEach(c -> {
            writePush(CONSTANT, c);
            writeCall(STR_APPEND, 2);
        });
    }

    public void compileKeywordConst(LexicalType type) {
        if (type == LexicalType.THIS) {
            writePush(PTR, 0);
            return;
        }
        writePush(CONSTANT, 0);
        if (type == LexicalType.TRUE) {
            writeArithmetic(VMCommand.NOT);
        }
    }

    public void compileOp(LexicalType type) {
        if (type == LexicalType.ASTERISK) {
            compileMult();
            return;
        }
        if (type == LexicalType.SLASH) {
            compileDiv();
            return;
        }
        writeArithmetic(VMCommand.getOP(type));
    }

    public void compileUnaryOp(LexicalType type) {
        VMCommand c = VMCommand.getUnaryOP(type);
        if (c == null) {
            throw new IllegalArgumentException();
        }
        writeArithmetic(c);
    }

    private void compileDiv() {
        writeCall(DIV, 2);
    }

    private void compileMult() {
        writeCall(MULT, 2);

    }
}
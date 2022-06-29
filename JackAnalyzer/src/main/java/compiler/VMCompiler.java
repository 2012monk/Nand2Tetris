package compiler;

import static compiler.constants.MemorySegment.ARG;
import static compiler.constants.MemorySegment.CONSTANT;
import static compiler.constants.MemorySegment.PTR;

import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.IdentifierType;
import compiler.constants.MemorySegment;

public class VMCompiler {

    private static final String IF_TRUE = "IF_TRUE_";
    private static final String IF_FALSE = "IF_FALSE_";
    private static final String IF_END = "IF_END_";
    private final VMWriter writer;


    public VMCompiler(JackFileWriter writer) {
        this.writer = new VMWriter(writer);
    }

    public void compileMethodDec(String className, String name, int localArgc) {
        compileFunctionDec(className, name, localArgc + 1);
        writer.writePush(ARG, 0);
        writer.writePop(PTR, 0);
    }

    public void compileConstructorDec(String className, String name, int localArgc, int size) {
        compileFunctionDec(className, name, localArgc);
        writer.writePush(CONSTANT, size);
        writer.writeCall("Memory.Alloc", 1);
        writer.writePop(PTR, 0);
    }

    public void compileFunctionDec(String className, String name, int localArgc) {
        String label = className + "." + name;
        writer.writeFunction(label, localArgc);
    }

    public void ifStatementStart(int idx) {
        writer.writeIf(IF_TRUE + idx);
        writer.writeGoto(IF_FALSE + idx);
    }

    public void ifStatementMid(int idx) {
        writer.writeLabel(IF_TRUE + idx);
        writer.writeGoto(IF_END + idx);
        writer.writeLabel(IF_FALSE + idx);
    }

    public void ifStatementEnd(int idx) {
        writer.writeLabel(IF_END + idx);
    }

    public void compileArgs(VariableIdentifier... vars) {
        for (VariableIdentifier var : vars) {
            writer.writePush(var.getSegment(), var.getIndex());
        }
    }

    public void callMethod(IdentifierType caller, int index, String className, String func) {
        MemorySegment segment = MemorySegment.callerSegment(caller);
        String label = className + "." + func;
        writer.writePush(segment, index);
    }

    public void callMethod(Identifier id) {

    }

    public void compileVariableDec(VariableIdentifier id) {
    }

    public void close() {
        System.out.println("closed");
        writer.close();
    }
}
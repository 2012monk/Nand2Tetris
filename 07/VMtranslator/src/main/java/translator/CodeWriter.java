package translator;

import static translator.constants.ASMCommand.ADD;
import static translator.constants.ASMCommand.AND;
import static translator.constants.ASMCommand.NEG;
import static translator.constants.ASMCommand.NOT;
import static translator.constants.ASMCommand.OR;
import static translator.constants.ASMCommand.SUB;
import static translator.constants.CommandType.C_POP;
import static translator.constants.MemorySymbol.CONSTANT;
import static translator.constants.MemorySymbol.PTR;
import static translator.constants.MemorySymbol.SP;
import static translator.constants.MemorySymbol.STATIC;
import static translator.constants.MemorySymbol.TMP;
import static translator.constants.MemorySymbol.X0;

import java.util.HashMap;
import java.util.Map;
import translator.constants.ASMCommand;
import translator.constants.CommandType;
import translator.constants.MemorySymbol;

/**
 * translate the VM command to Hack assembly command
 */
public class CodeWriter {

    private static final String SP_REG = SP.reg();
    private static final String X0_REG = X0.reg();
    private static final String END_INST = "(__END)\n@__END\n0;JMP";
    private static final String STK_POP =
        String.join("\n", SP_REG, "AM=M-1", "D=M");
    private static final String POP_INST =
        String.join("\n", "D=A", X0_REG, "M=D", STK_POP, X0_REG, "A=M", "M=D");
    private static final String PUSH_INST =
        String.join("\n", SP_REG, "A=M", "M=D", SP_REG, "M=M+1");
    private static final String COMP_JMP =
        String.join( "\n", "D=M-D", "M=-1",
            "@%s", "D;%s", "A=M-1", "M=0", "(%s)");
    private static final String ERR_ACCESS_CONST = "segfault can not access constant";
    private static final String ERR_STATIC_SEGFAULT = "static variable should allocated in 0-239";
    private static final String ERR_ACCESS_OOB = "invalid segment index";
    private static final String DEFAULT_FUNC_NAME = "__Root";
    private final ASMFileWriter fw;
    private final Map<Integer, String> staticVariables = new HashMap<>();
    private String fileName;
    private String currentFunction;
    private int lineNo;

    /**
     * open the file stream to write
     *
     * @param destPath path of the destination
     */
    public CodeWriter(String destPath) {
        fw = new ASMFileWriter(destPath);
    }

    /**
     * notify that the translation of a new file has begun
     *
     * @param fileName set new File to translate
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        lineNo = 1;
    }

    public void writeInit() {

    }

    public void writeLabel(String label) {
        if (currentFunction == null) {
            currentFunction = DEFAULT_FUNC_NAME;
        }
        fw.writeLine(String.format("(%s.%s$%s)", fileName, currentFunction, label));
    }

    public void writeGoto(String label) {
        fw.writeLines(String.format("@%s.%s$%s", fileName, currentFunction, label), "0;JMP");
    }

    public void writeIf(String label) {
        fw.writeLines(STK_POP,
            String.format("@%s.%s$%s", fileName, currentFunction, label),
            "D;JNE");
    }

    public void writeCall(String func, int argc) {

    }

    public void writeReturn() {

    }

    public void writeFunction(String label, int argc) {

    }

    /**
     * write the given arithmetic command in the translated asm code
     *
     * @param command arithmetic command
     */
    public void writeArithmetic(String command) {
        lineNo++;
        processArithmetic(ASMCommand.command(command), fileName, lineNo);
    }

    private void processArithmetic(ASMCommand command, String fileName, int lineNo) {
        if (command == NEG || command == NOT) {
            fw.writeLines(SP_REG, "A=M-1", String.format("M=%sM", command.op()));
            return;
        }
        fw.writeLines(SP_REG, "AM=M-1", "D=M", "A=A-1");
        if (command == AND || command == OR || command == ADD || command == SUB) {
            fw.writeLines(String.format("M=M%sD", command.op()));
            return;
        }
        String jumpLabel = String.format("_%s.%s:%d", command.name(), fileName, lineNo);
        fw.writeLines(String.format(COMP_JMP, jumpLabel, command.op(), jumpLabel));
    }

    /**
     * write the given push or pop command in the translated asm code
     *
     * @param type    type of the command C_PUSH or C_POP
     * @param segment the name of memory segment
     * @param index   index of segment segment[index] or (segment + i)
     */
    public void writePushPop(CommandType type, String segment, int index) {
        lineNo++;
        MemorySymbol symbol = MemorySymbol.symbol(segment);
        if (type == C_POP && symbol == CONSTANT) {
            throw new IllegalArgumentException(ERR_ACCESS_CONST);
        }
        if ((symbol == TMP || symbol == PTR) && !symbol.withinBound(index)) {
            throw new IllegalArgumentException(ERR_ACCESS_OOB);
        }
        loadSegment(symbol, index);
        if (type == C_POP) {
            fw.writeLine(POP_INST);
            return;
        }
        push(symbol);
    }

    private void loadSegment(MemorySymbol segment, int index) {
        if (segment == STATIC) {
            fw.writeLine(getStaticVar(index));
            return;
        }
        fw.writeLines("@" + index, "D=A");
        if (segment == CONSTANT) {
            return;
        }
        fw.writeLine(segment.reg());
        String ptr = "A=M+D";
        if (segment == TMP || segment == PTR) {
            ptr = "A=A+D";
        }
        fw.writeLine(ptr);
    }

    private void push(MemorySymbol segment) {
        if (segment != CONSTANT) {
            fw.writeLine("D=M");
        }
        fw.writeLine(PUSH_INST);
    }

    private String getStaticVar(int index) {
        if (!STATIC.withinBound(index)) {
            throw new IllegalArgumentException(ERR_STATIC_SEGFAULT);
        }
        if (!staticVariables.containsKey(index)) {
            staticVariables.put(index, String.format("@%s.%d", fileName, index));
        }
        return staticVariables.get(index);
    }

    /**
     * close and save the destination file stream
     */
    public void close() {
        fw.writeLine(END_INST);
        fw.close();
    }
}

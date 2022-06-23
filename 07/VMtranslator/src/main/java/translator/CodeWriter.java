package translator;

import static java.lang.String.format;
import static java.lang.String.join;
import static translator.constants.ASMCommand.ADD;
import static translator.constants.ASMCommand.AND;
import static translator.constants.ASMCommand.NEG;
import static translator.constants.ASMCommand.NOT;
import static translator.constants.ASMCommand.OR;
import static translator.constants.ASMCommand.SUB;
import static translator.constants.MemorySymbol.ARG;
import static translator.constants.MemorySymbol.CONSTANT;
import static translator.constants.MemorySymbol.FRAME;
import static translator.constants.MemorySymbol.LCL;
import static translator.constants.MemorySymbol.PTR;
import static translator.constants.MemorySymbol.RET;
import static translator.constants.MemorySymbol.SP;
import static translator.constants.MemorySymbol.STATIC;
import static translator.constants.MemorySymbol.THAT;
import static translator.constants.MemorySymbol.THIS;
import static translator.constants.MemorySymbol.TMP;
import static translator.constants.MemorySymbol.X0;
import static translator.constants.MemorySymbol.X1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import translator.constants.ASMCommand;
import translator.constants.MemorySymbol;

/**
 * translate the VM command to Hack assembly command
 */
public class CodeWriter {

    private static final String SP_REG = SP.reg();
    private static final String X0_REG = X0.reg();
    private static final String END_INST = "(__END)\n@__END\n0;JMP";
    private static final String STK_POP = join("\n",
        SP_REG, "AM=M-1", "D=M");
    private static final String POP_INST = join("\n",
        "D=A", X0_REG, "M=D", STK_POP, X0_REG, "A=M", "M=D");
    private static final String PUSH_INST = join("\n",
        SP_REG, "A=M", "M=D", SP_REG, "M=M+1");
    private static final String COMP_JMP = join("\n",
        "D=M-D", "M=-1", "@%s", "D;%s", SP.reg(), "A=M-1", "M=0", "(%s)");
    private static final String ERR_ACCESS_CONST = "segfault can not access constant";
    private static final String ERR_STATIC_SEGFAULT = "static variable should allocated in 0-239";
    private static final String ERR_ACCESS_OOB = "invalid segment index";
    private static final String DEFAULT_FUNC_NAME = "__Root";
    private static final String LABEL_FORMAT = "(%s)";
    private static final String ADDR_FORMAT = "@%s";
    private static final String INIT_FUNC = "Sys.init";
    private static final String RESTORE_STACK_FRAME = join("\n",
        FRAME.reg(), "AM=M-1", "D=M", "%s", "M=D");
    private static final String RET_A_FORMAT = "__RETURN_%s_%d_%s_%s_%d";
    private static final int INIT_SP = 256;
    private final ASMFileWriter fw;
    private final Map<String, String> functions = new HashMap<>();
    private Map<Integer, String> staticVariables;
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
        functions.put(INIT_FUNC, INIT_FUNC);
    }

    /**
     * notify that the translation of a new file has begun
     *
     * @param fileName set new File to translate
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        lineNo = 1;
        staticVariables = new HashMap<>();
    }

    public void writeInit() {
        lineNo++;
        fw.writeLines("@" + INIT_SP, "D=A", SP.reg(), "M=D");
        writeCall(INIT_FUNC, 0);
    }

    public void writeLabel(String label) {
        lineNo++;
        if (currentFunction == null) {
            currentFunction = DEFAULT_FUNC_NAME;
        }
        fw.writeLine(format("(%s.%s$%s)", fileName, currentFunction, label));
    }

    public void writeGoto(String label) {
        lineNo++;
        fw.writeLines(format("@%s.%s$%s", fileName, currentFunction, label), "0;JMP");
    }

    public void writeIf(String label) {
        lineNo++;
        fw.writeLines(STK_POP, format("@%s.%s$%s", fileName, currentFunction, label),
            "D;JNE");
    }

    public void writeCall(String label, int argc) {
        lineNo++;
        String retAddress = format(RET_A_FORMAT,
            fileName, lineNo, currentFunction, label, argc);
        fw.writeLines("@" + retAddress, "D=A", PUSH_INST); // push ret address
        List.of(LCL, ARG, THIS, THAT).forEach(seg -> writePush(seg, 0)); // push registers
        fw.writeLines(SP.reg(), "D=M", LCL.reg(), "M=D", // LCL=SP
            "@" + (argc + 5), "D=D-A", ARG.reg(), "M=D", // ARG=SP-ARGC-5
            "@" + getFunctionName(label), "0;JMP",
            format(LABEL_FORMAT, retAddress));
    }

    public void writeReturn() {
        lineNo++;
        fw.writeLines(LCL.reg(), "D=M", FRAME.reg(), "M=D", // FRAME=LCL
            "@5", "A=D-A", "D=M", RET.reg(), "M=D"); // RET=*(FRAME-5)
        writePop(ARG, 0); // *ARG=pop()
        fw.writeLines(ARG.reg(), "D=M", SP.reg(), "M=D+1"); // SP=ARG+1
        List.of(THAT, THIS, ARG, LCL)
            .forEach(seg -> fw.writeLines(format(RESTORE_STACK_FRAME, seg.reg())));
        fw.writeLines(RET.reg(), "A=M", "0;JMP");
    }

    public void writeFunction(String label, int localArgc) {
        lineNo++;
        currentFunction = getFunctionName(label);
        fw.writeLines(format(LABEL_FORMAT, currentFunction));
        if (localArgc == 0) {
            return;
        }
        String initLoop = currentFunction + "$__init__loop";
        fw.writeLines("@" + localArgc, "D=A",
            X1.reg(), "M=D",
            format(LABEL_FORMAT, initLoop),
            X1.reg(), "M=M-1");
        writePush(CONSTANT, 0);
        fw.writeLines(X1.reg(), "D=M", "@" + initLoop, "D;JNE");
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
            fw.writeLines(SP_REG, "A=M-1", format("M=%sM", command.op()));
            return;
        }
        fw.writeLines(SP_REG, "AM=M-1", "D=M", "A=A-1");
        if (command == AND || command == OR || command == ADD || command == SUB) {
            fw.writeLines(format("M=M%sD", command.op()));
            return;
        }
        String jumpLabel = format("_%s.%s:%d", command.name(), fileName, lineNo);
        fw.writeLines(format(COMP_JMP, jumpLabel, command.op(), jumpLabel));
    }

    /**
     * write the push command in the translated asm code
     *
     * @param segment the name of memory segment
     * @param index   index of segment segment[index] or (segment + i)
     */
    public void writePush(String segment, int index) {
        lineNo++;
        writePush(MemorySymbol.symbol(segment), index);
    }

    /**
     * write the pop command in the translated asm code
     *
     * @param segment the name of memory segment
     * @param index   index of segment segment[index] or (segment + i)
     */
    public void writePop(String segment, int index) {
        lineNo++;
        writePop(MemorySymbol.symbol(segment), index);
    }

    private void writePop(MemorySymbol segment, int index) {
        validateSegmentIndex(segment, index);
        if (segment == CONSTANT) {
            throw new IllegalArgumentException(ERR_ACCESS_CONST);
        }
        loadSegment(segment, index);
        fw.writeLine(POP_INST);
    }

    private void writePush(MemorySymbol segment, int index) {
        validateSegmentIndex(segment, index);
        loadSegment(segment, index);
        if (segment != CONSTANT) {
            fw.writeLine("D=M");
        }
        fw.writeLine(PUSH_INST);
    }

    private void validateSegmentIndex(MemorySymbol segment, int index) {
        if ((segment == TMP || segment == PTR) && !segment.withinBound(index)) {
            throw new IllegalArgumentException(ERR_ACCESS_OOB);
        }
    }

    private void loadSegment(MemorySymbol segment, int index) {
        if (segment == STATIC) {
            fw.writeLines(getStaticVariable(index));
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

    private String getStaticVariable(int index) {
        if (!STATIC.withinBound(index)) {
            throw new IllegalArgumentException(ERR_STATIC_SEGFAULT);
        }
        if (!staticVariables.containsKey(index)) {
            staticVariables.put(index, format("@%s.%d", fileName, index));
        }
        return staticVariables.get(index);
    }

    private String getFunctionName(String label) {
        if (!functions.containsKey(label)) {
            functions.put(label, format("%s.%s", fileName, label));
        }
        return functions.get(label);
    }

    /**
     * close and save the destination file stream
     */
    public void close() {
        fw.writeLine(END_INST);
        fw.close();
    }
}

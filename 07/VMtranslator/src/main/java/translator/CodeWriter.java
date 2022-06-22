package translator;

import static translator.constants.ASMCommand.*;
import static translator.constants.CommandType.*;
import static translator.constants.MemorySymbol.CONSTANT;
import static translator.constants.MemorySymbol.PTR;
import static translator.constants.MemorySymbol.SP;
import static translator.constants.MemorySymbol.STATIC;
import static translator.constants.MemorySymbol.TMP;
import static translator.constants.MemorySymbol.X0;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import translator.constants.ASMCommand;
import translator.constants.CommandType;
import translator.constants.MemorySymbol;

/**
 * translate the VM command to Hack assembly command
 */
public class CodeWriter {

    private static final String END_INST = "(__END)\n@__END\n0;JMP";
    private static final int STATIC_SIZE = 255 - 16;
    private static final String SP_REG = SP.reg();
    private static final String X0_REG = X0.reg();
    private final FileWriter fw;
    private String currentFile;
    private final Map<Integer, String> staticVariables = new HashMap<>();
    private int lineNo;

    /**
     * open the file stream to write
     * @param destPath path of the destination
     */
    public CodeWriter(String destPath) {
        try {
            fw = new FileWriter(destPath, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * notify that the translation of a new file has begun
     * @param fileName set new File to translate
     */
    public void setFileName(String fileName) {
        currentFile = fileName;
        lineNo = 1;
    }

    /**
     * write the given arithmetic command in the translated asm code
     * @param command arithmetic command
     */
    public void writeArithmetic(String command) {
        lineNo++;
        write(String.join("\n", processArithmetic(ASMCommand.command(command), currentFile, lineNo)));
    }

    private List<String> processArithmetic(ASMCommand command, String fileName, int lineNo) {
        List<String> ret;
        if (command == NEG || command == NOT) {
            return (List.of(SP_REG, "A=M-1", String.format("M=%sM", command.op())));
        }
        ret = new ArrayList<>(List.of(SP_REG, "AM=M-1", "D=M", "A=A-1"));
        if (command == AND || command == OR || command == ADD || command == SUB) {
            ret.addAll(List.of(String.format("M=M%sD", command.op())));
            return ret;
        }
        String jumpLabel = String.format("_%s.%s:%d", command.name(),fileName, lineNo);
        ret.addAll(List.of(
            "D=M-D", "M=-1",
            "@"+jumpLabel, "D;"+command.op(), SP_REG,
            "A=M-1", "M=0",
            "M=0", "("+jumpLabel + ")"
        ));
        return ret;
    }

    /**
     * write the given push or pop command in the translated asm code
     * @param type type of the command C_PUSH or C_POP
     * @param segment the name of memory segment
     * @param index index of segment
     *              segment[index] or (segment + i)
     */
    public void writePushPop(CommandType type, String segment, int index) {
        lineNo++;
        MemorySymbol symbol = MemorySymbol.symbol(segment);
        if (type == C_POP && symbol == CONSTANT) {
            throw new IllegalArgumentException("segfault can not access constant");
        }
        List<String> ret = new ArrayList<>(loadSegment(symbol, index));
        if (type == C_PUSH) {
            ret.addAll(push(symbol));
        } else {
            ret.addAll(pop());
        }
        write(String.join("\n", ret));
    }

    private List<String> loadSegment(MemorySymbol segment, int index) {
        if (segment == STATIC) {
            return List.of(getStaticVar(index));
        }
        ArrayList<String> ret;
        ret = new ArrayList<>(loadConstant(index));
        if (segment == CONSTANT) return ret;
        ret.add(segment.reg());
        String ptr = "A=M+D";
        if (segment == TMP || segment == PTR) {
            ptr = "A=A+D";
        }
        ret.add(ptr);
        return ret;
    }

    private List<String> push(MemorySymbol segment) {
        String dReg = "D=M";
        if (segment == CONSTANT) {
            dReg = "D=A";
        }
        return (List.of(dReg, X0_REG, "M=D", SP_REG, "AM=M-1", "D=M", X0_REG, "A=M", "M=D"));
    }

    private List<String> pop() {
        return (List.of("D=A", X0_REG, "M=D", SP_REG, "AM=M-1", "D=M", X0_REG, "A=M", "M=D"));
    }

    private List<String> loadConstant(int val) {
        return List.of("@" + val, "D=A");
    }

    private String getStaticVar(int index) {
        if (index > STATIC_SIZE) {
            throw new IllegalArgumentException("static variable should allocated in 0-239");
        }
        if (!staticVariables.containsKey(index)) {
            staticVariables.put(index, String.format("@%s.%d", currentFile, index));
        }
        return staticVariables.get(index);
    }

    private void write(String msg) {
        try {
            fw.write(msg);
            fw.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  close and save the destination file stream
     */
    public void close() {
        try {
            write(END_INST);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

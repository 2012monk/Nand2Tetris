package translator;

import static translator.constants.ASMCommand.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import translator.constants.ASMCommand;

/**
 * translate the VM command to Hack assembly command
 */
public class CodeWriter {

    private static final String SP = "@SP";
    private static final String X0 = "@R13";
    private static final String X1 = "@R14";
    private static final String X2 = "@R15";
    private final FileWriter fw;
    private String currentFile;
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
    }

    /**
     * write the given arithmetic command in the translated asm code
     * @param command arithmetic command
     */
    public void writeArithmetic(String command) {
        write(String.join("\n", processArithmetic(ASMCommand.command(command), currentFile, lineNo)));
    }

    private List<String> processArithmetic(ASMCommand command, String fileName, int lineNo) {
        List<String> ret;
        if (command == NEG || command == NOT) {
            return (List.of(SP, "A=M-1", String.format("D=%sM", command.op())));
        }
        ret = new ArrayList<>(List.of(SP, "AM=M-1", "D=M", "A=A-1"));
        if (command == AND || command == OR || command == ADD || command == SUB) {
            ret.add(String.format("M=D%sM", command.op()));
            return ret;
        }
        String jumpLabel = String.format("_%s.%s:%d", command.name(),fileName, lineNo);
        ret.addAll(List.of(
            "D=M-D", "M=0", "@"+jumpLabel, "D;"+command.op(), "M=-1", "("+jumpLabel + ")"
        ));
        return ret;
    }

    /**
     * write the given push command in the translated asm code
     * @param segment the name of memory segment
     * @param index index of segment
     *              segment[index] or (segment + i)
     */
    public void writePush(String segment, int index) {
    }

    /**
     * write the given pop command in the translated asm code
     * @param segment the name of memory segment
     * @param index index of segment
     *              segment[index] or (segment + i)
     */
    public void writePop(String segment, int index) {

    }

    private void write(String msg) {
        try {
            fw.write(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  close the destination file stream
     */
    public void close() {

    }
}

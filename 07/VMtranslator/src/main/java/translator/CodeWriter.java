package translator;

import translator.constants.CommandType;

/**
 * translate the VM command to Hack assembly command
 */
public class CodeWriter {

    /**
     *
     * open the file stream to write
     * @param destPath path of the destination
     */
    public CodeWriter(String destPath) {
    }

    /**
     *
     * notify that the translation of a new file has begun
     * @param fileName set new File to translate
     */
    public void setFileName(String fileName) {

    }

    /**
     * write the given arithmetic command in the translated asm code
     * @param command arithmetic command
     */
    public void writeArithmetic(String command) {

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

    /**
     *  close the destination file stream
     */
    public void close() {

    }
}

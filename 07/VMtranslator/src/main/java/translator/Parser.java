package translator;

import translator.constants.CommandType;

/**
 * parse single .vm file
 * strip all the white spaces and comments
 */
public class Parser {

    public Parser(String path) {
    }

    public boolean hasMoreCommands() {
        return false;
    }

    public void advance() {

    }

    public CommandType commandType() {
        return null;
    }

    /**
     * @return the first argument of the current command
     * @throws IllegalArgumentException if the current commandType is C_RETURN
     */
    public String arg1() {
        return null;
    }

    /**
     * @return the second argument of the current command
     * @throws IllegalArgumentException if the current command type is not C_PUSH, C_POP, C_FUNCTION or C_CALL
     */
    public int arg2() {
        return 0;
    }
}

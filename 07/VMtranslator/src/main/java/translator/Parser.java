package translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import translator.constants.CommandType;

/**
 * parse single .vm file strip all the white spaces and comments
 * one command in one line
 * command structure
 * command command_arg1 command_arg2
 */
public class Parser {

    private Queue<Command> commands = new LinkedList<>();

    public Parser(String path) {
        try {
            parse(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasMoreCommands() {
        return !commands.isEmpty();
    }

    public void advance() {
        commands.poll();
    }

    public CommandType commandType() {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("commands is empty");
        }
        return commands.peek().type();
    }

    /**
     * @return the first argument of the current command
     * @throws IllegalArgumentException if the current commandType is C_RETURN
     */
    public String arg1() {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("commands is empty");
        }
        return commands.peek().arg1();
    }

    /**
     * @return the second argument of the current command
     * @throws IllegalArgumentException if the current command type is not C_PUSH, C_POP, C_FUNCTION
     *                                  or C_CALL
     */
    public int arg2() {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("commands is empty");
        }
        return commands.peek().arg2();
    }

    private void parse(String path) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            line = line.strip();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            try {
                commands.add(new Command(line.strip()));
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + " at line " + lineCount);
            }
        }
    }
}

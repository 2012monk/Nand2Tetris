package translator;

import translator.constants.CommandType;

public class Command {

    private CommandType type;
    private String arg1;
    private Integer arg2;

    public Command(String command) {
        parseCommand(command);
    }

    public CommandType type() {
        return type;
    }

    public String arg1() {
        if (type != CommandType.C_ARITHMETIC && type.argc() == 0) {
            throw new IllegalArgumentException(type.name());
        }
        return arg1;
    }

    public int arg2() {
        if (type.argc() < 2) {
            throw new IllegalArgumentException();
        }
        return arg2;
    }

    private void parseCommand(String command) {
        String[] split = command.split(" ");
        type = CommandType.type(split[0]);
        if (type.argc() > split.length - 1) {
            throw new IllegalArgumentException("unexpected end of command");
        }
        if (type == CommandType.C_ARITHMETIC) {
            arg1 = split[0];
        }
        if (type.argc() > 0) {
            arg1 = split[1];
        }
        if (type.argc() > 1) {
            arg2 = Integer.parseInt(split[2]);
        }
    }
}

package components;

public class Command {

    private CommandType type;
    private String dest;
    private String comp;
    private String jmp;
    private String symbol;

    public Command(String command) {
        setType(command);
        isValidCommand(command);
    }

    public CommandType type() {
        return type;
    }

    public String dest() {
        return dest;
    }

    public String comp() {
        return comp;
    }

    public String jmp() {
        return jmp;
    }

    public String symbol() {
        return symbol;
    }

    private void setType(String command) {
        if (command.startsWith("@")) {
            type = CommandType.A_COMMAND;
            symbol = command.replace("@", "");
            validateVariable();
            return;
        }
        if (command.startsWith("(")) {
            type = CommandType.L_COMMAND;
            symbol = command.replace("(", "").replace(")", "");
            validateLabel();
            return;
        }
        type = CommandType.C_COMMAND;
        String[] jump = command.split(";");
        if (jump.length > 1) {
            jmp = jump[1];
        }
        String[] sp = jump[0].split("=");
        if (sp.length > 1) {
            dest = sp[0];
            comp = sp[1];
        } else {
            comp = sp[0];
        }
    }

    private void validateVariable() {
        String symbol = symbol();
        if (symbol.matches("[0-9]+")) {
            return;
        }
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("invalid variable name");
        }
        if (!symbol.matches("^(?![0-9])[a-zA-Z0-9_.:$]*")) {
            throw new IllegalArgumentException("Invalid variable name");
        }
    }

    private void validateLabel() {
        String symbol = symbol();
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("invalid Label name");
        }
        if (!symbol.matches("^(?![0-9])[a-zA-Z0-9_.:$]*")) {
            throw new IllegalArgumentException("Invalid Label Name");
        }
    }

    private void isValidCommand(String command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("Invalid Command");
        }
    }

    public void changeSymbol(String symbol) {
        this.symbol = symbol;
    }
}

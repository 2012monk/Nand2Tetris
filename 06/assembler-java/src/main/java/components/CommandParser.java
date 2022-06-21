package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class CommandParser implements Parser {

    private Queue<Command> commands = new LinkedList<>();
    private SymbolTable table = new SymbolTable();
    private int currentLine = 0;
    private int currentAddress = 16;
    private int currentCommand = 0;

    public CommandParser(String filePath) {
        readFile(new BufferedReader(openFile(filePath)));
        makeSymbolTable();
        parse();
        saveLfile(filePath);
    }

    private void saveLfile(String filepath){
        StringBuilder file = new StringBuilder();
        while (hasMoreCommands()) {
            if (commandType() == CommandType.A_COMMAND) {
                file.append("@").append(symbol()).append("\n");
            }
            if (commandType() == CommandType.C_COMMAND) {
                if (dest() != null) {
                    file.append(dest()).append("=");
                }
                file.append(comp());
                if (jump() != null) {
                    file.append(";").append(jump());
                }
                file.append("\n");
            }
            advance();
        }
        reset();
        try {
            FileWriter fw = new FileWriter(filepath.replace(".asm", "L.asm"));
            fw.write(file.toString());
            fw.close();
        } catch (Exception e) {
            System.out.println("file save failed");
        }
    }

    @Override
    public boolean hasMoreCommands() {
        return currentCommand < commands.size();
    }

    @Override
    public void advance() {
        validateNotEmpty();
        commands.add(commands.poll());
        currentCommand++;
    }

    @Override
    public CommandType commandType() {
        validateNotEmpty();
        return commands.peek().type();
    }

    @Override
    public String symbol() {
        validateNotEmpty();
        validateSymbol();
        return commands.peek().symbol();
    }

    @Override
    public String dest() {
        validateNotEmpty();
        validateCompCommand();
        return commands.peek().dest();
    }

    @Override
    public String comp() {
        validateNotEmpty();
        validateCompCommand();
        return commands.peek().comp();
    }

    @Override
    public String jump() {
        validateNotEmpty();
        validateCompCommand();
        return commands.peek().jmp();
    }

    private void makeSymbolTable() {
        while (hasMoreCommands()) {
            if (commandType() == CommandType.L_COMMAND) {
                registerLabel(symbol());
                advance();
                continue;
            }
            currentLine++;
            advance();
        }
        reset();
    }

    private void parse() {
        while (hasMoreCommands()) {
            if (commandType() == CommandType.A_COMMAND && !isDigit(symbol())) {
                currentCommand().changeSymbol(registerAddress(symbol()));
            }
            advance();
        }
        reset();
    }

    private void reset() {
        currentCommand = 0;
    }

    private String registerAddress(String symbol) {
        if (!table.contains(symbol)) {
            table.addEntry(symbol, currentAddress++);
        }
        return String.valueOf(table.getAddress(symbol));
    }

    private Command currentCommand() {
        return commands.peek();
    }

    private void registerLabel(String symbol) {
        if (table.contains(symbol)) return;
        table.addEntry(symbol, currentLine);
    }

    private boolean isDigit(String symbol) {
        return symbol.matches("[0-9]+");
    }

    private void validateSymbol() {
        CommandType type = commands.peek().type();
        if (type == CommandType.C_COMMAND) {
            throw new IllegalArgumentException();
        }
    }

    private void validateCompCommand() {
        if (commands.peek().type() != CommandType.C_COMMAND) {
            throw new IllegalArgumentException();
        }
    }
    private void validateNotEmpty() {
        if (currentCommand > commands.size()){
            throw new IllegalArgumentException();
        }
    }

    private FileReader openFile(String filePath) {
        String[] split = filePath.split("/");
        if (split.length < 1) {
            throw new IllegalArgumentException("Invalid Path");
        }
        if (!split[split.length - 1].endsWith(".asm")) {
            throw new IllegalArgumentException("Invalid File Format");
        }
        try {
            return new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not Exist");
        }
    }

    private void readFile(BufferedReader reader){
        String line;
        try {
            while ((line = reader.readLine()) !=  null) {
                line = strip(line);
                if (!line.startsWith("//") && !line.isEmpty()) {
                    commands.add(new Command(strip(line)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String strip(String command) {
        return command.replaceAll(" ", "").split("//")[0];
    }
}

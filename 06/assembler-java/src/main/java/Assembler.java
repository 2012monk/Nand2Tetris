import components.Code;
import components.CodeParser;
import components.CommandParser;
import components.CommandType;
import components.Parser;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {

    private Parser parser;
    private Code codeParser;
    private StringBuilder result;
    private String filePath;
    public Assembler(String filePath) {
        this.filePath = filePath;
        codeParser = new CodeParser();
        result = new StringBuilder();
    }

    public void assemble() {
        parser = new CommandParser(filePath);
        while (parser.hasMoreCommands()) {
            try {
                assembleCommand();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            saveResult();
        } catch (IOException e) {
            System.out.println("save Failed");
        }
    }

    private void saveResult() throws IOException {
        String destPath = filePath.replace(".asm", ".hack");
        FileWriter fw = new FileWriter(destPath, false);
        fw.write(result.toString());
        fw.close();
    }


    private void assembleCommand() {
        if (parser.commandType() == CommandType.A_COMMAND) {
            ACommand();
        }
        if (parser.commandType() == CommandType.C_COMMAND) {
            CCommand();
        }
        parser.advance();
    }

    private void CCommand() {
        String comp = BinaryConverter.convert(codeParser.comp(parser.comp()), 8);
        String dest = BinaryConverter.convert(codeParser.dest(parser.dest()), 4);
        String jump = BinaryConverter.convert(codeParser.jump(parser.jump()), 4);
        result.append(111)
            .append(comp, 1, 8)
            .append(dest, 1, 4)
            .append(jump, 1, 4)
            .append("\n");
    }

    private void ACommand() {
        String symbol = parser.symbol();
        result.append(0)
            .append(BinaryConverter.convert(Integer.parseInt(symbol), 16), 1, 16)
            .append("\n");
    }
}

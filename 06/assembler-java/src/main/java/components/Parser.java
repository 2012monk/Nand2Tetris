package components;

public interface Parser {

    boolean hasMoreCommands();

    /**
     * read the following command from the input, making it the current command.
     * it should be called after hasMorCommands() == true;
     * @throws java.util.NoSuchElementException if there are no more command in instruction
     */
    void advance();
    Command commandType();
    String symbol();
    String dest();
    String comp();
    String jump();
}

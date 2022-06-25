package compiler;

public class Application {

    public static void main(String[] args) {
        new JackAnalyzer(args[0]).analyze();
    }
}

package compiler;

public class Application {

    public static void main(String[] args) {
        new Compiler(args[0]).compile();
    }
}

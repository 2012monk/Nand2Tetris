
public class Application {

    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        assembler.assemble(args[0]);
    }
}

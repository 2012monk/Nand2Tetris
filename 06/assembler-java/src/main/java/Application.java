
public class Application {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        Assembler assembler = new Assembler(args[0]);
        assembler.assemble();
    }
}

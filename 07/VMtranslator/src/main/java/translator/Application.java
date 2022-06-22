package translator;

public class Application {

    public static void main(String[] args) {
        VMTranslator translator = new VMTranslator(args[0]);
        translator.translate();
    }
}

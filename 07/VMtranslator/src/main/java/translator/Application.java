package translator;

public class Application {

    public static void main(String[] args) {
        Translator translator = new Translator(args[0]);
        translator.translate();
    }
}

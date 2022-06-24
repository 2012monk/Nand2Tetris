import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class RegexTest {

    private static final String tc2 = "// This file is part of www.nand2tetris.org\n"
        + "// and the book \"The Elements of Computing Systems\"\n"
        + "// by Nisan and Schocken, MIT Press.\n"
        + "// File name: projects/10/ArrayTest/Main.jack\n"
        + "\n"
        + "// (identical to projects/09/Average/Main.jack)\n"
        + "\n"
        + "/** Computes the average of a sequence of integers. */\n"
        + "class Main {\n"
        + "    function void main() {\n"
        + "        var Array a;\n"
        + "        var int length;\n"
        + "        var int i, sum;\n"
        + "\t\n"
        + "\tlet length = Keyboard.readInt(\"HOW MANY NUMBERS? \");\n"
        + "\tlet a = Array.new(length);\n"
        + "\tlet i = 0;\n"
        + "\t\n"
        + "\twhile (i < length) {\n"
        + "\t    let a[i] = Keyboard.readInt(\"ENTER THE NEXT NUMBER: \");\n"
        + "\t    let i = i + 1;\n"
        + "\t}\n"
        + "\t\n"
        + "\tlet i = 0;\n"
        + "\tlet sum = 0;\n"
        + "\t\n"
        + "\twhile (i < length) {\n"
        + "\t    let sum = sum + a[i];\n"
        + "\t    let i = i + 1;\n"
        + "\t}\n"
        + "\t\n"
        + "\tdo Output.printString(\"THE AVERAGE IS: \");\n"
        + "\tdo Output.printInt(sum / length);\n"
        + "\tdo Output.println();\n"
        + "\t\n"
        + "\treturn;\n"
        + "    }\n"
        + "}";
    @Test
    void comments() {
        Pattern whiteSpaceOrComments =
            Pattern.compile("(//.*\\R*?|/[*](.|\\s)*[*]/|\\s++)");
//        Pattern whiteSpaceOrComments = Pattern.compile("/\\*");
        String tc = "hell this \n is if (true) // hi hello\n\n while (truet) // comments\n{statements}\n";
        String tc1 = "/*comments number 1*/\n"
            + "/*\n"
            + "*/\n"
            + "/** API docs \n"
            + "*\n"
            + "*\n"
            + "*\n"
            + "* asdbasb asd fa d\n"
            + "*/\n"
            + "\t\t\ta abdf \n"
            + "\t\t\t\n"
            + "\t\t\t\n"
            + "\t\t\t\n"
            + "\t\t\t\n"
            + "\t\t\t(1) return 0; // this is comments  adsfa \n // comment?\r\n"
            + "asdf\n"
            + "adsf";
//        System.out.println(Arrays.toString(pattern.split(tc, -1)));
        System.out.println(Arrays.toString(whiteSpaceOrComments.split(tc1, -1)));
        System.out.println(tc2);
        System.out.println(Arrays.toString(whiteSpaceOrComments.split(tc2, -1)));
        assertEquals(7,
        Arrays.stream(whiteSpaceOrComments.split(tc1, -1)).filter(x -> !x.isEmpty()).count());
    }
}
